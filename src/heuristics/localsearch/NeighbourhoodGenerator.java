package heuristics.localsearch;

import instance.Instance;
import java.util.ArrayList;
import java.util.List;

public class NeighbourhoodGenerator {

    public static List<Move> generate(List<Integer> tour, List<Integer> unvisited, IntraRouteNeighborhood intraRouteType) {
        List<Move> neighbourhood = new ArrayList<>();
        appendInterRouteMoves(neighbourhood, tour, unvisited);
        appendIntraRouteMoves(neighbourhood, tour, intraRouteType);
        return neighbourhood;
    }

    public static List<Move> generateCandidate(List<Integer> tour,
                                               List<Integer> unvisited,
                                               IntraRouteNeighborhood intraRouteType,
                                               Instance instance,
                                               int[][] nearestCandidates) {
        List<Move> neighbourhood = new ArrayList<>();

        int cycleSize = tour.size();
        for (int i = 0; i < cycleSize; i++) {
            int node = tour.get(i);
            int prev = tour.get((i - 1 + cycleSize) % cycleSize);
            int next = tour.get((i + 1) % cycleSize);
            for (int unvisitedNode : unvisited) {
                if (isCandidate(nearestCandidates, prev, unvisitedNode)
                        || isCandidate(nearestCandidates, next, unvisitedNode)) {
                    neighbourhood.add(new NodeExchangeMove(node, unvisitedNode));
                }
            }
        }

        for (int i = 0; i < cycleSize - 1; i++) {
            for (int j = i + 1; j < cycleSize; j++) {
                if (intraRouteType == IntraRouteNeighborhood.VERTEX_SWAP) {
                    int nodeI = tour.get(i);
                    int nodeJ = tour.get(j);
                    if (isCandidate(nearestCandidates, nodeI, nodeJ)) {
                        neighbourhood.add(new VertexSwapMove(nodeI, nodeJ));
                    }
                } else if (intraRouteType == IntraRouteNeighborhood.EDGE_SWAP) {
                    int a = tour.get(i);
                    int b = tour.get((i + 1) % cycleSize);
                    int c = tour.get(j);
                    int d = tour.get((j + 1) % cycleSize);
                    if (isCandidate(nearestCandidates, a, c) || isCandidate(nearestCandidates, b, d)) {
                        neighbourhood.add(new EdgeSwapMove(a, b, c, d));
                    }
                }
            }
        }

        return neighbourhood;
    }

    public static int[][] buildNearestCandidates(Instance instance, int k) {
        int n = instance.size();
        int candidateCount = Math.max(1, Math.min(k, Math.max(1, n - 1)));
        int[][] nearest = new int[n][candidateCount];

        for (int node = 0; node < n; node++) {
            final int source = node;
            List<Integer> others = new ArrayList<>();
            for (int other = 0; other < n; other++) {
                if (other != node) {
                    others.add(other);
                }
            }
            others.sort((left, right) -> Integer.compare(instance.distance(source, left), instance.distance(source, right)));
            for (int idx = 0; idx < candidateCount; idx++) {
                nearest[node][idx] = others.get(idx);
            }
        }
        return nearest;
    }

    private static void appendInterRouteMoves(List<Move> neighbourhood, List<Integer> tour, List<Integer> unvisited) {
        for (int node : tour) {
            for (int unvisitedNode : unvisited) {
                neighbourhood.add(new NodeExchangeMove(node, unvisitedNode));
            }
        }
    }

    private static void appendIntraRouteMoves(List<Move> neighbourhood, List<Integer> tour, IntraRouteNeighborhood intraRouteType) {
        int cycleSize = tour.size();
        for (int i = 0; i < cycleSize - 1; i++) {
            for (int j = i + 1; j < cycleSize; j++) {
                if (intraRouteType == IntraRouteNeighborhood.VERTEX_SWAP) {
                    neighbourhood.add(new VertexSwapMove(tour.get(i), tour.get(j)));
                } else if (intraRouteType == IntraRouteNeighborhood.EDGE_SWAP) {
                    int a = tour.get(i);
                    int b = tour.get((i + 1) % cycleSize);
                    int c = tour.get(j);
                    int d = tour.get((j + 1) % cycleSize);
                    neighbourhood.add(new EdgeSwapMove(a, b, c, d));
                }
            }
        }
    }

    private static boolean isCandidate(int[][] nearestCandidates, int from, int to) {
        if (from < 0 || from >= nearestCandidates.length || to < 0 || to >= nearestCandidates.length) {
            return false;
        }
        for (int candidate : nearestCandidates[from]) {
            if (candidate == to) {
                return true;
            }
        }
        return false;
    }
}