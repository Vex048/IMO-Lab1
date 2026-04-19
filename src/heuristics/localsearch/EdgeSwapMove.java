package heuristics.localsearch;

import instance.Instance;
import solution.CycleDeltas;
import java.util.Collections;
import java.util.List;

public class EdgeSwapMove implements Move {
    private final int a;
    private final int b;
    private final int c;
    private final int d;

    public EdgeSwapMove(int a, int b, int c, int d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public int evaluateDelta(Instance instance, List<Integer> tour) {
        EdgeOrientation orientation = findApplicableOrientation(tour);
        if (orientation == null) {
            return Integer.MIN_VALUE;
        }
        return CycleDeltas.edgeSwapObjectiveDelta(instance, tour, orientation.firstEdgeIndex, orientation.secondEdgeIndex);
    }

    @Override
    public void apply(List<Integer> tour, List<Integer> unvisited) {
        EdgeOrientation orientation = findApplicableOrientation(tour);
        if (orientation == null) {
            return;
        }

        int start = Math.min(orientation.firstEdgeIndex, orientation.secondEdgeIndex) + 1;
        int end = Math.max(orientation.firstEdgeIndex, orientation.secondEdgeIndex);

        while (start < end) {
            Collections.swap(tour, start, end);
            start++;
            end--;
        }
    }

    @Override
    public Applicability applicability(List<Integer> tour, List<Integer> unvisited) {
        DirectedState first = directedState(tour, a, b);
        DirectedState second = directedState(tour, c, d);

        if (first == DirectedState.MISSING || second == DirectedState.MISSING) {
            return Applicability.REMOVE;
        }
        if (first == second) {
            return Applicability.APPLICABLE;
        }
        return Applicability.KEEP_FOR_LATER;
    }

    private EdgeOrientation findApplicableOrientation(List<Integer> tour) {
        DirectedState first = directedState(tour, a, b);
        DirectedState second = directedState(tour, c, d);
        if (first == DirectedState.MISSING || second == DirectedState.MISSING || first != second) {
            return null;
        }

        if (first == DirectedState.FORWARD) {
            return edgeOrientation(tour, a, c);
        }
        return edgeOrientation(tour, b, d);
    }

    private EdgeOrientation edgeOrientation(List<Integer> tour, int firstEdgeFrom, int secondEdgeFrom) {
        int i = tour.indexOf(firstEdgeFrom);
        int j = tour.indexOf(secondEdgeFrom);
        if (i < 0 || j < 0 || i == j) {
            return null;
        }
        return new EdgeOrientation(i, j);
    }

    private DirectedState directedState(List<Integer> tour, int from, int to) {
        int fromIndex = tour.indexOf(from);
        int toIndex = tour.indexOf(to);
        if (fromIndex < 0 || toIndex < 0) {
            return DirectedState.MISSING;
        }

        int n = tour.size();
        int successorOfFrom = tour.get((fromIndex + 1) % n);
        int successorOfTo = tour.get((toIndex + 1) % n);

        if (successorOfFrom == to) {
            return DirectedState.FORWARD;
        }
        if (successorOfTo == from) {
            return DirectedState.REVERSED;
        }
        return DirectedState.MISSING;
    }

    private enum DirectedState {
        FORWARD,
        REVERSED,
        MISSING
    }

    private record EdgeOrientation(int firstEdgeIndex, int secondEdgeIndex) {
    }
}