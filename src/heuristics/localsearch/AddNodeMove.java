package heuristics.localsearch;

import instance.Instance;
import java.util.List;
import solution.CycleDeltas;

public class AddNodeMove implements Move {
    private final int insertAfterIndex;
    private final int nodeId;

    public AddNodeMove(int insertAfterIndex, int nodeId) {
        this.insertAfterIndex = insertAfterIndex;
        this.nodeId = nodeId;
    }

    @Override
    public int evaluateDelta(Instance instance, List<Integer> tour) {
        if (tour.isEmpty()) {
            return instance.reward(nodeId);
        }
        return CycleDeltas.insertionObjectiveDelta(instance, tour, insertAfterIndex, nodeId);
    }

    @Override
    public void apply(List<Integer> tour, List<Integer> unvisited) {
        if (tour.isEmpty()) {
            tour.add(nodeId);
        } else {
            tour.add(insertAfterIndex + 1, nodeId);
        }
        unvisited.remove(Integer.valueOf(nodeId));
    }
}
