package heuristics.localsearch;

import instance.Instance;
import solution.CycleDeltas;
import java.util.Collections;
import java.util.List;

public class VertexSwapMove implements Move {
    private final int node1;
    private final int node2;

    public VertexSwapMove(int node1, int node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    @Override
    public int evaluateDelta(Instance instance, List<Integer> tour) {
        int index1 = tour.indexOf(node1);
        int index2 = tour.indexOf(node2);
        if (index1 < 0 || index2 < 0) {
            return Integer.MIN_VALUE;
        }
        return CycleDeltas.vertexSwapObjectiveDelta(instance, tour, index1, index2);
    }

    @Override
    public void apply(List<Integer> tour, List<Integer> unvisited) {
        int index1 = tour.indexOf(node1);
        int index2 = tour.indexOf(node2);
        if (index1 < 0 || index2 < 0) {
            return;
        }
        Collections.swap(tour, index1, index2);
    }

    @Override
    public Applicability applicability(List<Integer> tour, List<Integer> unvisited) {
        if (!tour.contains(node1) || !tour.contains(node2) || node1 == node2) {
            return Applicability.REMOVE;
        }
        return Applicability.APPLICABLE;
    }
}