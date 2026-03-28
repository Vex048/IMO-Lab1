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

    public static int nodeExchangeDistanceDelta(Instance instance, List<Integer> cycle, int positionIndex, int unassignedNode) {
        int prev = cycle.get((positionIndex - 1 + cycle.size()) % cycle.size());
        int node = cycle.get(positionIndex);
        int next = cycle.get((positionIndex + 1) % cycle.size());
        return instance.distance(prev, unassignedNode)
                + instance.distance(unassignedNode, next)
                - instance.distance(prev, node)
                - instance.distance(node, next);
    }

    public static int nodeExchangeRewardDelta(Instance instance, List<Integer> cycle, int positionIndex, int unassignedNode) {
        int node = cycle.get(positionIndex);
        return instance.reward(unassignedNode) - instance.reward(node);
    }

    public static int nodeExchangeObjectiveDelta(Instance instance, List<Integer> cycle, int positionIndex, int unassignedNode) {
        return nodeExchangeRewardDelta(instance, cycle, positionIndex, unassignedNode)
               - nodeExchangeDistanceDelta(instance, cycle, positionIndex, unassignedNode);
    }

    public static int nodeSwapDistanceDelta(Instance instance, List<Integer> cycle, int i, int j) {
        int n = cycle.size();
        if (i == j) return 0;
        
        int nodeI = cycle.get(i);
        int prevI = cycle.get((i - 1 + n) % n);
        int nextI = cycle.get((i + 1) % n);
        
        int nodeJ = cycle.get(j);
        int prevJ = cycle.get((j - 1 + n) % n);
        int nextJ = cycle.get((j + 1) % n);
        
        // Handle adjacency
        if (nextI == nodeJ) { // i is right before j
            return instance.distance(prevI, nodeJ) + instance.distance(nodeJ, nodeI) + instance.distance(nodeI, nextJ)
                 - (instance.distance(prevI, nodeI) + instance.distance(nodeI, nodeJ) + instance.distance(nodeJ, nextJ));
        } else if (nextJ == nodeI) { // j is right before i
            return instance.distance(prevJ, nodeI) + instance.distance(nodeI, nodeJ) + instance.distance(nodeJ, nextI)
                 - (instance.distance(prevJ, nodeJ) + instance.distance(nodeJ, nodeI) + instance.distance(nodeI, nextI));
        } else {
            return instance.distance(prevI, nodeJ) + instance.distance(nodeJ, nextI)
                 + instance.distance(prevJ, nodeI) + instance.distance(nodeI, nextJ)
                 - (instance.distance(prevI, nodeI) + instance.distance(nodeI, nextI)
                 + instance.distance(prevJ, nodeJ) + instance.distance(nodeJ, nextJ));
        }
    }

    public static int nodeSwapObjectiveDelta(Instance instance, List<Integer> cycle, int i, int j) {
        return -nodeSwapDistanceDelta(instance, cycle, i, j);
    }
    
    public static int edgeSwapDistanceDelta(Instance instance, List<Integer> cycle, int i, int j) {
        if (i == j) return 0;
        int n = cycle.size();
        
        int a = cycle.get(i);
        int b = cycle.get((i + 1) % n);
        int c = cycle.get(j);
        int d = cycle.get((j + 1) % n);
        
        return instance.distance(a, c) + instance.distance(b, d)
             - instance.distance(a, b) - instance.distance(c, d);
    }
    
    public static int edgeSwapObjectiveDelta(Instance instance, List<Integer> cycle, int i, int j) {
        return -edgeSwapDistanceDelta(instance, cycle, i, j);
    }

    // Proxy methods for compatibility with friend's architecture
    public static int exchangeObjectiveDelta(Instance instance, List<Integer> tour, int tourIndex, int unvisitedNode) {
        return nodeExchangeObjectiveDelta(instance, tour, tourIndex, unvisitedNode);
    }

    public static int vertexSwapObjectiveDelta(Instance instance, List<Integer> tour, int index1, int index2) {
        return nodeSwapObjectiveDelta(instance, tour, index1, index2);
    }
}
