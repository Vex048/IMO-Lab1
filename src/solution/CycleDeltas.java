package solution;

import instance.Instance;
import java.util.List;

public final class CycleDeltas {
    private CycleDeltas() {
    }

    public static int insertionDistanceDelta(Instance instance, List<Integer> cycle, int positionIndex, int node) {
        int a = cycle.get(positionIndex);
        int b = cycle.get((positionIndex + 1) % cycle.size());
        return instance.distance(a, node)
                + instance.distance(node, b)
                - instance.distance(a, b);
    }

    public static int insertionObjectiveDelta(Instance instance, List<Integer> cycle, int positionIndex, int node) {
        return instance.reward(node) - insertionDistanceDelta(instance, cycle, positionIndex, node);
    }

    public static int removalDistanceDelta(Instance instance, List<Integer> cycle, int positionIndex) {
        int prev = cycle.get((positionIndex - 1 + cycle.size()) % cycle.size());
        int node = cycle.get(positionIndex);
        int next = cycle.get((positionIndex + 1) % cycle.size());
        return instance.distance(prev, next)
                - instance.distance(prev, node)
                - instance.distance(node, next);
    }

    public static int removalObjectiveDelta(Instance instance, List<Integer> cycle, int positionIndex) {
        int node = cycle.get(positionIndex);
        return -instance.reward(node) - removalDistanceDelta(instance, cycle, positionIndex);
    }

    // TODO we can change method signatures this is just a mock
    public static int exchangeObjectiveDelta(Instance instance, List<Integer> tour, int tourIndex, int unvisitedNode) {
        // TODO
        return 0;
    }

    public static int vertexSwapObjectiveDelta(Instance instance, List<Integer> tour, int index1, int index2) {
        // TODO
        return 0;
    }

    public static int edgeSwapObjectiveDelta(Instance instance, List<Integer> tour, int index1, int index2) {
        // TODO
        return 0;
    }
}

