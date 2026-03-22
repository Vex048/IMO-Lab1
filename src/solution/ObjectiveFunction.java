package solution;

import instance.Instance;
import java.util.List;

// add/remove node delta calculated here for GC?
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

    public static int calculateValue(Instance instance, Cycle cycle) {
        return calculateTotalReward(instance, cycle) - calculateTotalDistance(instance, cycle);
    }
}
