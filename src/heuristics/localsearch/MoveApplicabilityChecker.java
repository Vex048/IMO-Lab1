package heuristics.localsearch;

import java.util.List;

public final class MoveApplicabilityChecker {
    private MoveApplicabilityChecker() {
    }

    public enum MoveApplicability {
        APPLICABLE,
        SKIP_KEEP,
        DISCARD
    }

    public static MoveApplicability evaluate(LmEntry entry,
                                             List<Integer> tour,
                                             boolean[] isUnvisited,
                                             int[] nodeToIndex) {
        if (entry.kind() == LmEntry.Kind.NODE_EXCHANGE) {
            return evaluateNodeExchange(entry, tour, isUnvisited);
        }
        if (entry.kind() == LmEntry.Kind.EDGE_SWAP) {
            return evaluateEdgeSwap(entry, tour, nodeToIndex);
        }
        return MoveApplicability.DISCARD;
    }

    private static MoveApplicability evaluateNodeExchange(LmEntry entry,
                                                          List<Integer> tour,
                                                          boolean[] isUnvisited) {
        int index = entry.tourIndex();
        if (index < 0 || index >= tour.size()) {
            return MoveApplicability.DISCARD;
        }

        int inserted = entry.insertedNode();
        if (inserted < 0 || inserted >= isUnvisited.length || !isUnvisited[inserted]) {
            return MoveApplicability.DISCARD;
        }

        int n = tour.size();
        int currentNode = tour.get(index);
        int currentPrev = tour.get((index - 1 + n) % n);
        int currentNext = tour.get((index + 1) % n);

        if (currentNode != entry.expectedNode()) {
            return MoveApplicability.DISCARD;
        }

        if (currentPrev == entry.expectedPrev() && currentNext == entry.expectedNext()) {
            return MoveApplicability.APPLICABLE;
        }

        return MoveApplicability.DISCARD;
    }

    private static MoveApplicability evaluateEdgeSwap(LmEntry entry,
                                                      List<Integer> tour,
                                                      int[] nodeToIndex) {
        EdgeDirection edge1 = edgeDirection(entry.edge1From(), entry.edge1To(), tour, nodeToIndex);
        EdgeDirection edge2 = edgeDirection(entry.edge2From(), entry.edge2To(), tour, nodeToIndex);

        if (edge1 == EdgeDirection.MISSING || edge2 == EdgeDirection.MISSING) {
            return MoveApplicability.DISCARD;
        }

        if (edge1 == edge2) {
            return MoveApplicability.APPLICABLE;
        }

        return MoveApplicability.SKIP_KEEP;
    }

    private static EdgeDirection edgeDirection(int from,
                                               int to,
                                               List<Integer> tour,
                                               int[] nodeToIndex) {
        if (from < 0 || from >= nodeToIndex.length || to < 0 || to >= nodeToIndex.length) {
            return EdgeDirection.MISSING;
        }

        int idxFrom = nodeToIndex[from];
        int idxTo = nodeToIndex[to];
        if (idxFrom < 0 || idxTo < 0) {
            return EdgeDirection.MISSING;
        }

        int n = tour.size();
        int succFrom = tour.get((idxFrom + 1) % n);
        if (succFrom == to) {
            return EdgeDirection.FORWARD;
        }

        int succTo = tour.get((idxTo + 1) % n);
        if (succTo == from) {
            return EdgeDirection.REVERSE;
        }

        return EdgeDirection.MISSING;
    }

    private enum EdgeDirection {
        FORWARD,
        REVERSE,
        MISSING
    }
}