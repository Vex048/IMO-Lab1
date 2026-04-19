package solution;

import instance.Instance;
import java.util.List;

public class ObjectiveFunction {
    public static int calculateTotalDistance(Instance instance, Cycle cycle) {
        List<Integer> tour = cycle.getTour();
        int n = tour.size();
        if (n < 2) return 0;

        int dist = 0;
        for (int i = 0; i < n; i++) {
            int currentNode = tour.get(i);
            int nextNode = tour.get((i + 1) % n);
            dist += instance.distance(currentNode, nextNode);
        }
        return dist;
    }

    public static int calculateTotalReward(Instance instance, Cycle cycle) {
        int reward = 0;
        for (int nodeId : cycle.getTour()) {
            reward += instance.reward(nodeId);
        }
        return reward;
    }

    public static int calculateValue(int totalReward, int totalDistance) {
        return totalReward - totalDistance;
    }

    public static int calculateValue(Instance instance, Cycle cycle) {
        List<Integer> tour = cycle.getTour();
        int n = tour.size();
        if (n == 0) return 0;

        int reward = 0;
        int distance = 0;

        for (int i = 0; i < n; i++) {
            int currentNode = tour.get(i);
            reward += instance.reward(currentNode);

            if (n > 1) {
                int nextNode = tour.get((i + 1) % n);
                distance += instance.distance(currentNode, nextNode);
            }
        }

        return calculateValue(reward, distance);
    }

    public static int calculateInsertObjectiveDelta(Instance instance, List<Integer> tour, int insertAfterIndex, int nodeId) {
        int size = tour.size();
        if (size == 0) return 0;

        if (insertAfterIndex < 0 || insertAfterIndex >= size) {
            throw new IllegalArgumentException("insertAfterIndex out of bounds: " + insertAfterIndex);
        }

        int previousNode = tour.get(insertAfterIndex);
        int nextNode = tour.get((insertAfterIndex + 1) % size);

        int distanceDelta = instance.distance(previousNode, nodeId)
                + instance.distance(nodeId, nextNode)
                - instance.distance(previousNode, nextNode);
        int rewardDelta = instance.reward(nodeId);
        return rewardDelta - distanceDelta;
    }
}