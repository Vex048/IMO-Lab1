package heuristics;

import heuristics.localsearch.AddNodeMove;
import heuristics.localsearch.CandidateLists;
import heuristics.localsearch.EdgeSwapMove;
import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.NodeExchangeMove;
import heuristics.localsearch.RemoveNodeMove;
import heuristics.localsearch.VertexSwapMove;
import instance.Instance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import solution.Cycle;
import solution.CycleDeltas;
import solution.ObjectiveFunction;
import solution.Solution;

public class CandidateSteepestLocalSearchHeuristic implements Heuristic {
    private final IntraRouteNeighborhood intraRouteType;
    private final Heuristic initHeuristic;
    private final int candidateCount;

    public CandidateSteepestLocalSearchHeuristic() {
        this(IntraRouteNeighborhood.EDGE_SWAP, null, 10);
    }

    public CandidateSteepestLocalSearchHeuristic(IntraRouteNeighborhood intraRouteType,
                                                 Heuristic initHeuristic,
                                                 int candidateCount) {
        this.intraRouteType = intraRouteType;
        this.initHeuristic = initHeuristic;
        this.candidateCount = candidateCount;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        Solution initialSolution = (initHeuristic != null)
                ? initHeuristic.solve(instance, startNode, rng)
                : generateRandomSolution(instance, startNode, rng);

        List<Integer> tour = new ArrayList<>(initialSolution.getCycle().getTour());
        List<Integer> unvisited = getUnvisitedNodes(instance, tour);
        CandidateLists candidateLists = CandidateLists.build(instance, candidateCount);
        boolean[] isUnvisited = buildUnvisitedMask(instance.size(), unvisited);
        int[] seen = new int[instance.size()];
        int stamp = 1;

        boolean improvementFound = true;
        while (improvementFound) {
            improvementFound = false;

            int bestDelta = 0;
            MoveType bestType = null;
            int bestI = -1;
            int bestJ = -1;
            int bestNode = -1;

            int cycleSize = tour.size();

            for (int i = 0; i < cycleSize; i++) {
                int prev = tour.get((i - 1 + cycleSize) % cycleSize);
                int current = tour.get(i);
                int next = tour.get((i + 1) % cycleSize);

                int exchangeStamp = ++stamp;
                for (int node : candidateLists.nearestOf(prev)) {
                    if (!isUnvisited[node] || seen[node] == exchangeStamp) {
                        continue;
                    }
                    seen[node] = exchangeStamp;
                    int delta = CycleDeltas.exchangeObjectiveDelta(instance, tour, i, node);
                    if (delta > bestDelta) {
                        bestDelta = delta;
                        bestType = MoveType.NODE_EXCHANGE;
                        bestI = i;
                        bestNode = node;
                    }
                }
                for (int node : candidateLists.nearestOf(next)) {
                    if (!isUnvisited[node] || seen[node] == exchangeStamp) {
                        continue;
                    }
                    seen[node] = exchangeStamp;
                    int delta = CycleDeltas.exchangeObjectiveDelta(instance, tour, i, node);
                    if (delta > bestDelta) {
                        bestDelta = delta;
                        bestType = MoveType.NODE_EXCHANGE;
                        bestI = i;
                        bestNode = node;
                    }
                }

                int addStamp = ++stamp;
                for (int node : candidateLists.nearestOf(current)) {
                    if (!isUnvisited[node] || seen[node] == addStamp) {
                        continue;
                    }
                    seen[node] = addStamp;
                    int delta = CycleDeltas.insertionObjectiveDelta(instance, tour, i, node);
                    if (delta > bestDelta) {
                        bestDelta = delta;
                        bestType = MoveType.ADD_NODE;
                        bestI = i;
                        bestNode = node;
                    }
                }
                for (int node : candidateLists.nearestOf(next)) {
                    if (!isUnvisited[node] || seen[node] == addStamp) {
                        continue;
                    }
                    seen[node] = addStamp;
                    int delta = CycleDeltas.insertionObjectiveDelta(instance, tour, i, node);
                    if (delta > bestDelta) {
                        bestDelta = delta;
                        bestType = MoveType.ADD_NODE;
                        bestI = i;
                        bestNode = node;
                    }
                }

                if (candidateLists.isCandidateEdge(prev, next)) {
                    int delta = CycleDeltas.removalObjectiveDelta(instance, tour, i);
                    if (delta > bestDelta) {
                        bestDelta = delta;
                        bestType = MoveType.REMOVE_NODE;
                        bestI = i;
                    }
                }
            }

            for (int i = 0; i < cycleSize - 1; i++) {
                for (int j = i + 1; j < cycleSize; j++) {
                    if (intraRouteType == IntraRouteNeighborhood.EDGE_SWAP) {
                        int a = tour.get(i);
                        int b = tour.get((i + 1) % cycleSize);
                        int c = tour.get(j);
                        int d = tour.get((j + 1) % cycleSize);

                        if (!candidateLists.isCandidateEdge(a, c) && !candidateLists.isCandidateEdge(b, d)) {
                            continue;
                        }

                        int delta = CycleDeltas.edgeSwapObjectiveDelta(instance, tour, i, j);
                        if (delta > bestDelta) {
                            bestDelta = delta;
                            bestType = MoveType.EDGE_SWAP;
                            bestI = i;
                            bestJ = j;
                        }
                    } else if (intraRouteType == IntraRouteNeighborhood.VERTEX_SWAP) {
                        if (!introducesCandidateInVertexSwap(tour, i, j, candidateLists)) {
                            continue;
                        }

                        int delta = CycleDeltas.vertexSwapObjectiveDelta(instance, tour, i, j);
                        if (delta > bestDelta) {
                            bestDelta = delta;
                            bestType = MoveType.VERTEX_SWAP;
                            bestI = i;
                            bestJ = j;
                        }
                    }
                }
            }

            if (bestType != null) {
                applyBestMove(bestType, bestI, bestJ, bestNode, tour, unvisited, isUnvisited);
                improvementFound = true;
            }
        }

        Cycle finalCycle = new Cycle(tour);
        int finalDistance = ObjectiveFunction.calculateTotalDistance(instance, finalCycle);
        int finalReward = ObjectiveFunction.calculateTotalReward(instance, finalCycle);
        int finalObjective = ObjectiveFunction.calculateValue(finalReward, finalDistance);

        return new Solution(finalCycle, finalReward, finalDistance, finalObjective);
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

    private boolean[] buildUnvisitedMask(int nodeCount, List<Integer> unvisited) {
        boolean[] isUnvisited = new boolean[nodeCount];
        for (int node : unvisited) {
            isUnvisited[node] = true;
        }
        return isUnvisited;
    }

    private void applyBestMove(MoveType type,
                               int bestI,
                               int bestJ,
                               int bestNode,
                               List<Integer> tour,
                               List<Integer> unvisited,
                               boolean[] isUnvisited) {
        switch (type) {
            case NODE_EXCHANGE -> {
                int oldNode = tour.get(bestI);
                new NodeExchangeMove(bestI, bestNode).apply(tour, unvisited);
                isUnvisited[bestNode] = false;
                isUnvisited[oldNode] = true;
            }
            case ADD_NODE -> {
                new AddNodeMove(bestI, bestNode).apply(tour, unvisited);
                isUnvisited[bestNode] = false;
            }
            case REMOVE_NODE -> {
                int removed = tour.get(bestI);
                new RemoveNodeMove(bestI).apply(tour, unvisited);
                isUnvisited[removed] = true;
            }
            case EDGE_SWAP -> new EdgeSwapMove(bestI, bestJ).apply(tour, unvisited);
            case VERTEX_SWAP -> new VertexSwapMove(bestI, bestJ).apply(tour, unvisited);
        }
    }

    private boolean introducesCandidateInVertexSwap(List<Integer> tour,
                                                    int i,
                                                    int j,
                                                    CandidateLists candidateLists) {
        int n = tour.size();

        int nodeI = tour.get(i);
        int prevI = tour.get((i - 1 + n) % n);
        int nextI = tour.get((i + 1) % n);

        int nodeJ = tour.get(j);
        int prevJ = tour.get((j - 1 + n) % n);
        int nextJ = tour.get((j + 1) % n);

        if (nextI == nodeJ) {
            return candidateLists.isCandidateEdge(prevI, nodeJ)
                    || candidateLists.isCandidateEdge(nodeJ, nodeI)
                    || candidateLists.isCandidateEdge(nodeI, nextJ);
        }
        if (nextJ == nodeI) {
            return candidateLists.isCandidateEdge(prevJ, nodeI)
                    || candidateLists.isCandidateEdge(nodeI, nodeJ)
                    || candidateLists.isCandidateEdge(nodeJ, nextI);
        }

        return candidateLists.isCandidateEdge(prevI, nodeJ)
                || candidateLists.isCandidateEdge(nodeJ, nextI)
                || candidateLists.isCandidateEdge(prevJ, nodeI)
                || candidateLists.isCandidateEdge(nodeI, nextJ);
    }

    private enum MoveType {
        NODE_EXCHANGE,
        ADD_NODE,
        REMOVE_NODE,
        EDGE_SWAP,
        VERTEX_SWAP
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