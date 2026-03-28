package heuristics.localsearch;

import instance.Instance;
import solution.CycleDeltas;
import java.util.List;

public class NodeExchangeMove implements Move {
    private final int tourIndex;
    private final int unvisitedNode;

    public NodeExchangeMove(int tourIndex, int unvisitedNode) {
        this.tourIndex = tourIndex;
        this.unvisitedNode = unvisitedNode;
    }

    @Override
    public int evaluateDelta(Instance instance, List<Integer> tour) {
        return CycleDeltas.exchangeObjectiveDelta(instance, tour, tourIndex, unvisitedNode);
    }

    @Override
    public void apply(List<Integer> tour, List<Integer> unvisited) {
        int oldNode = tour.get(tourIndex);
        tour.set(tourIndex, unvisitedNode);

        unvisited.remove(Integer.valueOf(unvisitedNode));
        unvisited.add(oldNode);
    }
}