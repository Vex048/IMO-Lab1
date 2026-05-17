package heuristics;

import heuristics.localsearch.IntraRouteNeighborhood;
import instance.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import solution.Cycle;
import solution.CycleDeltas;
import solution.ObjectiveFunction;
import solution.Solution;

public class LnsHeuristic implements Heuristic, IterationCountProvider {
    private static final double SUBPATH_DESTROY_PROBABILITY = 0.50;
    private static final double RANDOM_NODE_DESTROY_PROBABILITY = 0.15;

    private final long timeLimitMs;
    private final double destroyFraction;
    private final boolean applyLocalSearchAfterRepair;
    private final IntraRouteNeighborhood neighborhood;
    private int iterationCount;

    public LnsHeuristic(long timeLimitMs, double destroyFraction, boolean applyLocalSearchAfterRepair) {
        this(timeLimitMs, destroyFraction, applyLocalSearchAfterRepair, IntraRouteNeighborhood.EDGE_SWAP);
    }

    public LnsHeuristic(long timeLimitMs,
                        double destroyFraction,
                        boolean applyLocalSearchAfterRepair,
                        IntraRouteNeighborhood neighborhood) {
        this.timeLimitMs = Math.max(0, timeLimitMs);
        this.destroyFraction = Math.max(0.0, Math.min(1.0, destroyFraction));
        this.applyLocalSearchAfterRepair = applyLocalSearchAfterRepair;
        this.neighborhood = neighborhood;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        long endTime = System.currentTimeMillis() + timeLimitMs;
        Solution current = runLocalSearch(instance, startNode, rng);
        Solution best = current;
        iterationCount = 0;

        while (System.currentTimeMillis() < endTime) {
            iterationCount++;
            List<Integer> tour = new ArrayList<>(current.getCycle().getTour());
            List<Integer> repairCandidates = buildUnvisited(instance, tour);

            destroy(instance, tour, repairCandidates, rng);
            repairWithRegret(instance, tour, repairCandidates);

            Solution repaired = buildSolution(instance, tour);
            Solution candidate = applyLocalSearchAfterRepair
                    ? runLocalSearchFromSeed(instance, repaired, rng)
                    : repaired;

            if (candidate.objectiveValue() > current.objectiveValue()) {
                current = candidate;
            }
            if (candidate.objectiveValue() > best.objectiveValue()) {
                best = candidate;
            }
        }

        return best;
    }

    private Solution runLocalSearch(Instance instance, int startNode, Random rng) {
        return new LmSteepestLocalSearchHeuristic(neighborhood, null).solve(instance, startNode, rng);
    }

    private Solution runLocalSearchFromSeed(Instance instance, Solution seed, Random rng) {
        Heuristic seeded = new SeedSolutionHeuristic(seed);
        return new LmSteepestLocalSearchHeuristic(neighborhood, seeded).solve(instance, -1, rng);
    }

    private void destroy(Instance instance, List<Integer> tour, List<Integer> repairCandidates, Random rng) {
        int cycleSize = tour.size();
        if (cycleSize <= 1) {
            return;
        }

        int removeCount = (int) Math.round(cycleSize * destroyFraction);
        removeCount = Math.max(1, removeCount);
        removeCount = Math.min(removeCount, cycleSize - 1);

        if (tour.size() > 3 && rng.nextDouble() < SUBPATH_DESTROY_PROBABILITY) {
            destroySubpath(instance, tour, repairCandidates, removeCount, rng);
            return;
        }

        for (int i = 0; i < removeCount; i++) {
            int idx = chooseDestroyIndex(instance, tour, rng);
            repairCandidates.add(tour.remove(idx));
        }
    }

    private void destroySubpath(Instance instance,
                                List<Integer> tour,
                                List<Integer> repairCandidates,
                                int removeCount,
                                Random rng) {
        int startIndex = chooseSubpathStart(instance, tour, rng);
        for (int i = 0; i < removeCount && tour.size() > 1; i++) {
            int idx = startIndex % tour.size();
            repairCandidates.add(tour.remove(idx));
        }
    }

    private int chooseSubpathStart(Instance instance, List<Integer> tour, Random rng) {
        double totalWeight = 0.0;
        double[] weights = new double[tour.size()];
        for (int i = 0; i < tour.size(); i++) {
            int previous = tour.get(i);
            int next = tour.get((i + 1) % tour.size());
            double weight = 1.0 + instance.distance(previous, next);
            weights[i] = weight;
            totalWeight += weight;
        }

        double pick = rng.nextDouble() * totalWeight;
        for (int i = 0; i < weights.length; i++) {
            pick -= weights[i];
            if (pick <= 0.0) {
                return (i + 1) % tour.size();
            }
        }
        return rng.nextInt(tour.size());
    }

    private int chooseDestroyIndex(Instance instance, List<Integer> tour, Random rng) {
        if (rng.nextDouble() < RANDOM_NODE_DESTROY_PROBABILITY) {
            return rng.nextInt(tour.size());
        }

        double totalWeight = 0.0;
        double[] weights = new double[tour.size()];
        for (int i = 0; i < tour.size(); i++) {
            int prev = tour.get((i - 1 + tour.size()) % tour.size());
            int node = tour.get(i);
            int next = tour.get((i + 1) % tour.size());
            int distanceSaving = instance.distance(prev, node)
                    + instance.distance(node, next)
                    - instance.distance(prev, next);
            double weight = 1.0 + Math.max(0, distanceSaving - instance.reward(node));
            weights[i] = weight;
            totalWeight += weight;
        }

        double pick = rng.nextDouble() * totalWeight;
        for (int i = 0; i < weights.length; i++) {
            pick -= weights[i];
            if (pick <= 0.0) {
                return i;
            }
        }
        return weights.length - 1;
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

    private Solution buildSolution(Instance instance, List<Integer> tour) {
        Cycle cycle = new Cycle(tour);
        int distance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int reward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objective = ObjectiveFunction.calculateValue(reward, distance);
        return new Solution(cycle, reward, distance, objective);
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
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
