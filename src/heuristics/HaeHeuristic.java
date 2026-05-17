package heuristics;

import globalconvexity.SolutionSignature;
import heuristics.localsearch.IntraRouteNeighborhood;
import instance.Instance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import solution.Cycle;
import solution.CycleDeltas;
import solution.ObjectiveFunction;
import solution.Solution;

public class HaeHeuristic implements Heuristic, IterationCountProvider {
    private final long timeLimitMs;
    private final int maxIterations;
    private final int populationSize;
    private final HaeRecombinationOperator operator;
    private final boolean applyLocalSearchAfterRecombination;
    private final IntraRouteNeighborhood neighborhood;
    private int iterationCount;

    public HaeHeuristic(long timeLimitMs,
                        int maxIterations,
                        int populationSize,
                        HaeRecombinationOperator operator,
                        boolean applyLocalSearchAfterRecombination,
                        IntraRouteNeighborhood neighborhood) {
        this.timeLimitMs = Math.max(0L, timeLimitMs);
        this.maxIterations = maxIterations;
        this.populationSize = Math.max(2, populationSize);
        this.operator = operator;
        this.applyLocalSearchAfterRecombination = applyLocalSearchAfterRecombination;
        this.neighborhood = neighborhood;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        long endTime = timeLimitMs > 0L ? System.currentTimeMillis() + timeLimitMs : Long.MAX_VALUE;
        iterationCount = 0;

        List<PopulationEntry> population = new ArrayList<>();
        Set<SolutionSignature> signatures = new HashSet<>();
        initPopulation(instance, startNode, rng, population, signatures);

        if (population.isEmpty()) {
            return runLocalSearch(instance, startNode, rng);
        }

        Solution best = bestOf(population);

        while (!isStopConditionMet(endTime)) {
            if (population.size() < 2) {
                addIfUnique(runLocalSearch(instance, startNode, rng), instance, population, signatures);
                continue;
            }

            iterationCount++;
            int firstIdx = rng.nextInt(population.size());
            int secondIdx = rng.nextInt(population.size() - 1);
            if (secondIdx >= firstIdx) {
                secondIdx++;
            }

            Solution parent1 = population.get(firstIdx).solution;
            Solution parent2 = population.get(secondIdx).solution;
            Solution child = recombine(parent1, parent2, instance, rng);
            if (child == null) {
                continue;
            }

            Solution candidate = applyLocalSearchAfterRecombination
                    ? improveWithLocalSearch(instance, child, rng)
                    : child;

            if (candidate.objectiveValue() > best.objectiveValue()) {
                best = candidate;
            }

            if (shouldInsert(candidate, instance, population, signatures)) {
                insertAndEvictWorst(candidate, instance, population, signatures);
            }
        }

        return bestOf(population);
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    private boolean isStopConditionMet(long endTime) {
        boolean timeExpired = System.currentTimeMillis() >= endTime;
        boolean iterationLimitReached = maxIterations > 0 && iterationCount >= maxIterations;
        return timeExpired || iterationLimitReached;
    }

    private void initPopulation(Instance instance,
                                int startNode,
                                Random rng,
                                List<PopulationEntry> population,
                                Set<SolutionSignature> signatures) {
        int attempts = 0;
        int maxAttempts = Math.max(populationSize * 50, populationSize + 10);

        while (population.size() < populationSize && attempts < maxAttempts) {
            attempts++;
            Solution candidate = runLocalSearch(instance, startNode, rng);
            addIfUnique(candidate, instance, population, signatures);
        }
    }

    private Solution runLocalSearch(Instance instance, int startNode, Random rng) {
        return new LmSteepestLocalSearchHeuristic(neighborhood, null).solve(instance, startNode, rng);
    }

    private Solution improveWithLocalSearch(Instance instance, Solution seed, Random rng) {
        Heuristic seeded = new SeedSolutionHeuristic(seed);
        Solution improved = new LmSteepestLocalSearchHeuristic(neighborhood, seeded).solve(instance, -1, rng);
        return withPhase1Distance(improved, seed.getPhase1Distance());
    }

    private Solution withPhase1Distance(Solution solution, int phase1Distance) {
        return new Solution(
                solution.getCycle(),
                solution.getTotalReward(),
                solution.getTotalDistance(),
                solution.objectiveValue(),
                phase1Distance
        );
    }

    private Solution recombine(Solution parent1, Solution parent2, Instance instance, Random rng) {
        Solution base = rng.nextBoolean() ? parent1 : parent2;
        Solution other = (base == parent1) ? parent2 : parent1;
        List<Integer> childTour;

        if (operator == HaeRecombinationOperator.OP1_COMMON_EDGES_AND_VERTICES) {
            childTour = buildFromCommonEdgesAndVertices(parent1.getCycle().getTour(), parent2.getCycle().getTour(), instance, rng, true);
        } else if (operator == HaeRecombinationOperator.OP2_COMMON_EDGES_ONLY) {
            childTour = buildFromCommonEdgesAndVertices(base.getCycle().getTour(), other.getCycle().getTour(), instance, rng, false);
        } else {
            childTour = buildFromCommonVertices(base.getCycle().getTour(), other.getCycle().getTour(), instance);
        }

        if (childTour.isEmpty()) {
            return null;
        }

        List<Integer> repairCandidates = buildUnvisited(instance, childTour);
        repairWithRegret(instance, childTour, repairCandidates);
        removeUnprofitableNodes(instance, childTour);

        int phase1Distance = ObjectiveFunction.calculateTotalDistance(instance, new Cycle(childTour));
        return buildSolution(instance, childTour, phase1Distance);
    }

    private List<Integer> buildFromCommonVertices(List<Integer> baseTour, List<Integer> otherTour, Instance instance) {
        boolean[] inOther = buildNodeMask(instance.size(), otherTour);
        List<Integer> filtered = new ArrayList<>();
        for (int node : baseTour) {
            if (inOther[node]) {
                filtered.add(node);
            }
        }
        return filtered;
    }

    private List<Integer> buildFromCommonEdgesAndVertices(List<Integer> tourA,
                                                          List<Integer> tourB,
                                                          Instance instance,
                                                          Random rng,
                                                          boolean includeSingletons) {
        int nodeCount = instance.size();
        boolean[] inA = buildNodeMask(nodeCount, tourA);
        boolean[] inB = buildNodeMask(nodeCount, tourB);
        boolean[] edgeSetB = buildEdgeSet(nodeCount, tourB);

        @SuppressWarnings("unchecked")
        List<Integer>[] adjacency = (List<Integer>[]) new List[nodeCount];

        for (int i = 0; i < tourA.size(); i++) {
            int a = tourA.get(i);
            int b = tourA.get((i + 1) % tourA.size());
            int key = edgeKey(a, b, nodeCount);
            if (edgeSetB[key]) {
                if (adjacency[a] == null) adjacency[a] = new ArrayList<>();
                if (adjacency[b] == null) adjacency[b] = new ArrayList<>();
                adjacency[a].add(b);
                adjacency[b].add(a);
            }
        }

        List<List<Integer>> segments = buildSegmentsFromAdjacency(adjacency, rng);
        if (includeSingletons) {
            for (int node = 0; node < nodeCount; node++) {
                if (inA[node] && inB[node] && adjacency[node] == null) {
                    segments.add(new ArrayList<>(List.of(node)));
                }
            }
        }

        return joinSegments(segments, rng);
    }

    private List<List<Integer>> buildSegmentsFromAdjacency(List<Integer>[] adjacency, Random rng) {
        List<List<Integer>> segments = new ArrayList<>();
        if (adjacency == null) {
            return segments;
        }

        boolean[] visited = new boolean[adjacency.length];

        for (int node = 0; node < adjacency.length; node++) {
            if (adjacency[node] != null && adjacency[node].size() == 1 && !visited[node]) {
                segments.add(traversePath(node, adjacency, visited));
            }
        }

        for (int node = 0; node < adjacency.length; node++) {
            if (adjacency[node] != null && !visited[node]) {
                List<Integer> cycle = traverseCycle(node, adjacency, visited);
                if (!cycle.isEmpty()) {
                    segments.add(rotateCycle(cycle, rng));
                }
            }
        }

        return segments;
    }

    private List<Integer> traversePath(int start, List<Integer>[] adjacency, boolean[] visited) {
        List<Integer> segment = new ArrayList<>();
        int prev = -1;
        int current = start;

        while (true) {
            segment.add(current);
            visited[current] = true;
            List<Integer> neighbors = adjacency[current];
            int next = -1;
            if (neighbors != null) {
                for (int candidate : neighbors) {
                    if (candidate != prev && !visited[candidate]) {
                        next = candidate;
                        break;
                    }
                }
            }
            if (next == -1) {
                break;
            }
            prev = current;
            current = next;
        }

        return segment;
    }

    private List<Integer> traverseCycle(int start, List<Integer>[] adjacency, boolean[] visited) {
        List<Integer> cycle = new ArrayList<>();
        int prev = -1;
        int current = start;

        while (true) {
            cycle.add(current);
            visited[current] = true;
            int next = nextNeighbor(current, prev, adjacency);
            if (next == -1 || next == start) {
                break;
            }
            prev = current;
            current = next;
        }

        return cycle;
    }

    private int nextNeighbor(int current, int prev, List<Integer>[] adjacency) {
        List<Integer> neighbors = adjacency[current];
        if (neighbors == null) {
            return -1;
        }
        for (int candidate : neighbors) {
            if (candidate != prev) {
                return candidate;
            }
        }
        return -1;
    }

    private List<Integer> rotateCycle(List<Integer> cycle, Random rng) {
        if (cycle.isEmpty()) {
            return cycle;
        }
        int shift = rng.nextInt(cycle.size());
        List<Integer> rotated = new ArrayList<>(cycle.size());
        for (int i = 0; i < cycle.size(); i++) {
            rotated.add(cycle.get((i + shift) % cycle.size()));
        }
        return rotated;
    }

    private List<Integer> joinSegments(List<List<Integer>> segments, Random rng) {
        if (segments.isEmpty()) {
            return new ArrayList<>();
        }
        Collections.shuffle(segments, rng);
        List<Integer> tour = new ArrayList<>();
        for (List<Integer> segment : segments) {
            if (rng.nextBoolean()) {
                Collections.reverse(segment);
            }
            tour.addAll(segment);
        }
        return tour;
    }

    private boolean[] buildNodeMask(int nodeCount, List<Integer> tour) {
        boolean[] mask = new boolean[nodeCount];
        for (int node : tour) {
            mask[node] = true;
        }
        return mask;
    }

    private boolean[] buildEdgeSet(int nodeCount, List<Integer> tour) {
        boolean[] edgeSet = new boolean[nodeCount * nodeCount];
        if (tour.size() < 2) {
            return edgeSet;
        }

        for (int i = 0; i < tour.size(); i++) {
            int a = tour.get(i);
            int b = tour.get((i + 1) % tour.size());
            edgeSet[edgeKey(a, b, nodeCount)] = true;
        }

        return edgeSet;
    }

    private int edgeKey(int a, int b, int nodeCount) {
        int first = Math.min(a, b);
        int second = Math.max(a, b);
        return first * nodeCount + second;
    }

    private List<Integer> buildUnvisited(Instance instance, List<Integer> tour) {
        boolean[] inTour = new boolean[instance.size()];
        for (int node : tour) {
            inTour[node] = true;
        }

        List<Integer> unvisited = new ArrayList<>();
        for (int i = 0; i < instance.size(); i++) {
            if (!inTour[i]) {
                unvisited.add(i);
            }
        }

        return unvisited;
    }

    private void repairWithRegret(Instance instance, List<Integer> tour, List<Integer> repairCandidates) {
        while (!repairCandidates.isEmpty()) {
            int bestNode = -1;
            int bestPos = -1;
            int bestRegret = Integer.MIN_VALUE;
            int bestDelta = Integer.MIN_VALUE;

            for (int node : repairCandidates) {
                int bestNodeDelta = Integer.MIN_VALUE;
                int secondDelta = Integer.MIN_VALUE;
                int bestNodePos = -1;

                if (tour.isEmpty()) {
                    bestNodeDelta = instance.reward(node);
                    secondDelta = bestNodeDelta;
                } else {
                    for (int pos = 0; pos < tour.size(); pos++) {
                        int delta = CycleDeltas.insertionObjectiveDelta(instance, tour, pos, node);
                        if (delta > bestNodeDelta) {
                            secondDelta = bestNodeDelta;
                            bestNodeDelta = delta;
                            bestNodePos = pos;
                        } else if (delta > secondDelta) {
                            secondDelta = delta;
                        }
                    }
                }

                if (secondDelta == Integer.MIN_VALUE) {
                    secondDelta = bestNodeDelta;
                }

                int regret = bestNodeDelta - secondDelta;
                if (regret > bestRegret || (regret == bestRegret && bestNodeDelta > bestDelta)) {
                    bestRegret = regret;
                    bestDelta = bestNodeDelta;
                    bestNode = node;
                    bestPos = bestNodePos;
                }
            }

            if (bestNode == -1) {
                break;
            }

            tour.add(bestPos + 1, bestNode);
            repairCandidates.remove(Integer.valueOf(bestNode));
        }
    }

    private void removeUnprofitableNodes(Instance instance, List<Integer> tour) {
        boolean improved = true;
        while (improved && tour.size() > 2) {
            improved = false;
            int bestPos = -1;
            int bestDelta = 0;

            for (int pos = 0; pos < tour.size(); pos++) {
                int delta = CycleDeltas.removalObjectiveDelta(instance, tour, pos);
                if (delta > bestDelta) {
                    bestDelta = delta;
                    bestPos = pos;
                }
            }

            if (bestPos >= 0) {
                tour.remove(bestPos);
                improved = true;
            }
        }
    }

    private Solution buildSolution(Instance instance, List<Integer> tour, int phase1Distance) {
        Cycle cycle = new Cycle(tour);
        int distance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int reward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objective = ObjectiveFunction.calculateValue(reward, distance);
        return new Solution(cycle, reward, distance, objective, phase1Distance);
    }

    private boolean addIfUnique(Solution candidate,
                                Instance instance,
                                List<PopulationEntry> population,
                                Set<SolutionSignature> signatures) {
        SolutionSignature signature = SolutionSignature.from(candidate, instance.size());
        if (signatures.contains(signature)) {
            return false;
        }
        population.add(new PopulationEntry(candidate, signature));
        signatures.add(signature);
        return true;
    }

    private boolean shouldInsert(Solution candidate,
                                 Instance instance,
                                 List<PopulationEntry> population,
                                 Set<SolutionSignature> signatures) {
        SolutionSignature signature = SolutionSignature.from(candidate, instance.size());
        if (signatures.contains(signature)) {
            return false;
        }

        PopulationEntry worst = worstOf(population);
        return worst == null || candidate.objectiveValue() > worst.solution.objectiveValue();
    }

    private void insertAndEvictWorst(Solution candidate,
                                     Instance instance,
                                     List<PopulationEntry> population,
                                     Set<SolutionSignature> signatures) {
        SolutionSignature signature = SolutionSignature.from(candidate, instance.size());
        if (signatures.contains(signature)) {
            return;
        }

        PopulationEntry worst = worstOf(population);
        if (worst != null) {
            population.remove(worst);
            signatures.remove(worst.signature);
        }

        population.add(new PopulationEntry(candidate, signature));
        signatures.add(signature);
    }

    private PopulationEntry worstOf(List<PopulationEntry> population) {
        PopulationEntry worst = null;
        for (PopulationEntry entry : population) {
            if (worst == null || entry.solution.objectiveValue() < worst.solution.objectiveValue()) {
                worst = entry;
            }
        }
        return worst;
    }

    private Solution bestOf(List<PopulationEntry> population) {
        Solution best = population.get(0).solution;
        for (PopulationEntry entry : population) {
            if (entry.solution.objectiveValue() > best.objectiveValue()) {
                best = entry.solution;
            }
        }
        return best;
    }

    private static class PopulationEntry {
        private final Solution solution;
        private final SolutionSignature signature;

        private PopulationEntry(Solution solution, SolutionSignature signature) {
            this.solution = solution;
            this.signature = signature;
        }
    }

    private static class SeedSolutionHeuristic implements Heuristic {
        private final Solution seed;

        private SeedSolutionHeuristic(Solution seed) {
            this.seed = seed;
        }

        @Override
        public Solution solve(Instance instance, int startNode, Random rng) {
            return seed;
        }
    }
}

