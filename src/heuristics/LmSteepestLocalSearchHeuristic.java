package heuristics;

import heuristics.localsearch.EdgeSwapMove;
import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.LmEntry;
import heuristics.localsearch.MoveApplicabilityChecker;
import heuristics.localsearch.MoveApplicabilityChecker.MoveApplicability;
import heuristics.localsearch.NodeExchangeMove;
import instance.Instance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import solution.Cycle;
import solution.ObjectiveFunction;
import solution.Solution;

public class LmSteepestLocalSearchHeuristic implements Heuristic {
    private final IntraRouteNeighborhood intraRouteType;
    private final Heuristic initHeuristic;

    public LmSteepestLocalSearchHeuristic() {
        this(IntraRouteNeighborhood.EDGE_SWAP, null);
    }

    public LmSteepestLocalSearchHeuristic(IntraRouteNeighborhood intraRouteType, Heuristic initHeuristic) {
        this.intraRouteType = intraRouteType;
        this.initHeuristic = initHeuristic;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        if (intraRouteType != IntraRouteNeighborhood.EDGE_SWAP) {
            throw new IllegalArgumentException("LM heuristic currently supports EDGE_SWAP neighborhood only");
        }

        Solution initialSolution = (initHeuristic != null)
                ? initHeuristic.solve(instance, startNode, rng)
                : generateRandomSolution(instance, startNode, rng);

        List<Integer> tour = new ArrayList<>(initialSolution.getCycle().getTour());
        List<Integer> unvisited = getUnvisitedNodes(instance, tour);

        PriorityQueue<LmEntry> lm = new PriorityQueue<>(Comparator.comparingInt(LmEntry::cachedDelta).reversed());
        boolean refreshedAfterNoApplicable = false;

        while (true) {
            if (lm.isEmpty()) {
                populateImprovingMoves(instance, tour, unvisited, lm);
                if (lm.isEmpty()) {
                    break;
                }
            }

            int[] nodeToIndex = buildNodeToIndex(instance.size(), tour);
            boolean[] isUnvisited = buildUnvisitedMask(instance.size(), unvisited);

            boolean applied = false;
            List<LmEntry> skipped = new ArrayList<>();

            while (!lm.isEmpty()) {
                LmEntry candidate = lm.poll();
                MoveApplicability applicability = MoveApplicabilityChecker.evaluate(
                        candidate,
                        tour,
                        isUnvisited,
                        nodeToIndex
                );

                if (applicability == MoveApplicability.DISCARD) {
                    continue;
                }
                if (applicability == MoveApplicability.SKIP_KEEP) {
                    skipped.add(candidate);
                    continue;
                }

                applyEntry(candidate, tour, unvisited, nodeToIndex);
                applied = true;
                break;
            }

            lm.addAll(skipped);

            if (!applied) {
                if (refreshedAfterNoApplicable) {
                    break;
                }

                lm.clear();
                populateImprovingMoves(instance, tour, unvisited, lm);
                refreshedAfterNoApplicable = true;
                if (lm.isEmpty()) {
                    break;
                }
                continue;
            }

            refreshedAfterNoApplicable = false;
        }

        Cycle finalCycle = new Cycle(tour);
        int finalDistance = ObjectiveFunction.calculateTotalDistance(instance, finalCycle);
        int finalReward = ObjectiveFunction.calculateTotalReward(instance, finalCycle);
        int finalObjective = ObjectiveFunction.calculateValue(finalReward, finalDistance);

        return new Solution(finalCycle, finalReward, finalDistance, finalObjective);
    }

    private void populateImprovingMoves(Instance instance,
                                        List<Integer> tour,
                                        List<Integer> unvisited,
                                        PriorityQueue<LmEntry> lm) {
        int cycleSize = tour.size();

        for (int i = 0; i < cycleSize; i++) {
            int prev = tour.get((i - 1 + cycleSize) % cycleSize);
            int current = tour.get(i);
            int next = tour.get((i + 1) % cycleSize);

            for (int candidateNode : unvisited) {
                NodeExchangeMove move = new NodeExchangeMove(i, candidateNode);
                int delta = move.evaluateDelta(instance, tour);
                if (delta > 0) {
                    lm.add(LmEntry.forNodeExchange(
                            move,
                            delta,
                            i,
                            candidateNode,
                            current,
                            prev,
                            next
                    ));
                }
            }
        }

        for (int i = 0; i < cycleSize - 1; i++) {
            for (int j = i + 1; j < cycleSize; j++) {
                int a = tour.get(i);
                int b = tour.get((i + 1) % cycleSize);
                int c = tour.get(j);
                int d = tour.get((j + 1) % cycleSize);

                int oldCost = instance.distance(a, b) + instance.distance(c, d);
                int normalNewCost = instance.distance(a, c) + instance.distance(b, d);
                int mirrorNewCost = instance.distance(a, d) + instance.distance(b, c);

                int normalDelta = oldCost - normalNewCost;
                if (normalDelta > 0) {
                    lm.add(LmEntry.forEdgeSwap(
                            new EdgeSwapMove(i, j),
                            normalDelta,
                            a,
                            b,
                            c,
                            d
                    ));
                }

                int mirroredDelta = oldCost - mirrorNewCost;
                if (mirroredDelta > 0) {
                    lm.add(LmEntry.forEdgeSwap(
                            new EdgeSwapMove(i, j),
                            mirroredDelta,
                            a,
                            b,
                            d,
                            c
                    ));
                }
            }
        }
    }

    private void applyEntry(LmEntry entry,
                            List<Integer> tour,
                            List<Integer> unvisited,
                            int[] nodeToIndex) {
        if (entry.kind() == LmEntry.Kind.NODE_EXCHANGE) {
            entry.move().apply(tour, unvisited);
            return;
        }

        if (isForwardEdge(entry.edge1From(), entry.edge1To(), tour, nodeToIndex)) {
            int i = nodeToIndex[entry.edge1From()];
            int j = nodeToIndex[entry.edge2From()];
            new EdgeSwapMove(i, j).apply(tour, unvisited);
            return;
        }

        int i = nodeToIndex[entry.edge1To()];
        int j = nodeToIndex[entry.edge2To()];
        new EdgeSwapMove(i, j).apply(tour, unvisited);
    }

    private boolean isForwardEdge(int from, int to, List<Integer> tour, int[] nodeToIndex) {
        int idx = nodeToIndex[from];
        if (idx < 0) {
            return false;
        }
        int n = tour.size();
        return tour.get((idx + 1) % n) == to;
    }

    private int[] buildNodeToIndex(int nodeCount, List<Integer> tour) {
        int[] nodeToIndex = new int[nodeCount];
        Arrays.fill(nodeToIndex, -1);
        for (int idx = 0; idx < tour.size(); idx++) {
            nodeToIndex[tour.get(idx)] = idx;
        }
        return nodeToIndex;
    }

    private boolean[] buildUnvisitedMask(int nodeCount, List<Integer> unvisited) {
        boolean[] isUnvisited = new boolean[nodeCount];
        for (int node : unvisited) {
            isUnvisited[node] = true;
        }
        return isUnvisited;
    }

    private List<Integer> getUnvisitedNodes(Instance instance, List<Integer> tour) {
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

    private Solution generateRandomSolution(Instance instance, int startNode, Random rng) {
        int actualStartNode = (startNode >= 0) ? startNode : rng.nextInt(instance.size());

        List<Integer> otherNodes = new ArrayList<>();
        for (int i = 0; i < instance.size(); i++) {
            if (i != actualStartNode) {
                otherNodes.add(i);
            }
        }
        Collections.shuffle(otherNodes, rng);

        int size = (int) Math.ceil(instance.size() / 2.0);

        List<Integer> randomTour = new ArrayList<>();
        randomTour.add(actualStartNode);
        randomTour.addAll(otherNodes.subList(0, size - 1));

        Cycle cycle = new Cycle(randomTour);
        int distance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int reward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objective = ObjectiveFunction.calculateValue(reward, distance);

        return new Solution(cycle, reward, distance, objective);
    }
}