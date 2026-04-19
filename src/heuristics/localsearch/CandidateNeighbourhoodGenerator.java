package heuristics.localsearch;

import instance.Instance;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CandidateNeighbourhoodGenerator {
    private CandidateNeighbourhoodGenerator() {
    }

    public static List<Move> generate(Instance instance,
                                      List<Integer> tour,
                                      List<Integer> unvisited,
                                      IntraRouteNeighborhood intraRouteType,
                                      CandidateLists candidateLists) {
        List<Move> neighbourhood = new ArrayList<>();
        int cycleSize = tour.size();

        if (cycleSize == 0) {
            return neighbourhood;
        }

        boolean[] isUnvisited = new boolean[instance.size()];
        for (int node : unvisited) {
            isUnvisited[node] = true;
        }

        // Inter-route moves: keep only moves introducing at least one candidate edge.
        for (int i = 0; i < cycleSize; i++) {
            int prev = tour.get((i - 1 + cycleSize) % cycleSize);
            int next = tour.get((i + 1) % cycleSize);

            Set<Integer> candidateUnvisitedNodes = new HashSet<>();
            for (int node : candidateLists.nearestOf(prev)) {
                if (isUnvisited[node]) {
                    candidateUnvisitedNodes.add(node);
                }
            }
            for (int node : candidateLists.nearestOf(next)) {
                if (isUnvisited[node]) {
                    candidateUnvisitedNodes.add(node);
                }
            }

            for (int node : candidateUnvisitedNodes) {
                neighbourhood.add(new NodeExchangeMove(i, node));
            }
        }

        // Intra-route moves
        for (int i = 0; i < cycleSize - 1; i++) {
            for (int j = i + 1; j < cycleSize; j++) {
                if (intraRouteType == IntraRouteNeighborhood.EDGE_SWAP) {
                    int a = tour.get(i);
                    int b = tour.get((i + 1) % cycleSize);
                    int c = tour.get(j);
                    int d = tour.get((j + 1) % cycleSize);

                    if (candidateLists.isCandidateEdge(a, c) || candidateLists.isCandidateEdge(b, d)) {
                        neighbourhood.add(new EdgeSwapMove(i, j));
                    }
                } else if (intraRouteType == IntraRouteNeighborhood.VERTEX_SWAP) {
                    if (introducesCandidateInVertexSwap(tour, i, j, candidateLists)) {
                        neighbourhood.add(new VertexSwapMove(i, j));
                    }
                }
            }
        }

        return neighbourhood;
    }

    private static boolean introducesCandidateInVertexSwap(List<Integer> tour,
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
}