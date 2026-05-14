package heuristics.localsearch;

import instance.Instance;
import java.util.List;
import solution.CycleDeltas;

public class RemoveNodeMove implements Move {
    private final int removeIndex;

    public RemoveNodeMove(int removeIndex) {
        this.removeIndex = removeIndex;
    }

    @Override
    public int evaluateDelta(Instance instance, List<Integer> tour) {
        if (tour.isEmpty()) {
            return 0;
        }
        return CycleDeltas.removalObjectiveDelta(instance, tour, removeIndex);
    }

    @Override
    public void apply(List<Integer> tour, List<Integer> unvisited) {
        if (tour.isEmpty()) {
            return;
        }
        int removed = tour.remove(removeIndex);
        unvisited.add(removed);
    }
}
