package heuristics.localsearch;

import instance.Instance;
import solution.CycleDeltas;
import java.util.List;

public class NodeExchangeMove implements Move {
    private final int cycleNode;
    private final int unvisitedNode;

    public NodeExchangeMove(int cycleNode, int unvisitedNode) {
        this.cycleNode = cycleNode;
        this.unvisitedNode = unvisitedNode;
    }

    @Override
    public int evaluateDelta(Instance instance, List<Integer> tour) {
        int index = tour.indexOf(cycleNode);
        if (index < 0) {
            return Integer.MIN_VALUE;
        }
        return CycleDeltas.exchangeObjectiveDelta(instance, tour, index, unvisitedNode);
    }

    @Override
    public void apply(List<Integer> tour, List<Integer> unvisited) {
        int index = tour.indexOf(cycleNode);
        if (index < 0) {
            return;
        }
        int oldNode = tour.get(index);
        tour.set(index, unvisitedNode);

        unvisited.remove(Integer.valueOf(unvisitedNode));
        unvisited.add(oldNode);
    }

    @Override
    public Applicability applicability(List<Integer> tour, List<Integer> unvisited) {
        if (!tour.contains(cycleNode)) {
            return Applicability.REMOVE;
        }
        if (!unvisited.contains(unvisitedNode)) {
            return Applicability.REMOVE;
        }
        return Applicability.APPLICABLE;
    }
}