package heuristics;

import instance.Instance;
import solution.Cycle;
import solution.ObjectiveFunction;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NearestNeighbourHeuristic implements Heuristic {
    public enum Mode {
        DISTANCE,
        COST
    }

    private final Mode mode;

    public NearestNeighbourHeuristic() { this.mode = Mode.DISTANCE; }

    public NearestNeighbourHeuristic(Mode mode) { this.mode = mode; }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        int n = instance.size();

        List<Integer> tour = new ArrayList<>();

        if (startNode == -1) {
            startNode = rng.nextInt(n);
        }
        tour.add(startNode);

        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i != startNode) remaining.add(i);
        }

        while (!remaining.isEmpty()) {
            int bestNode = startNode;
            int currentNode = tour.get(tour.size() - 1);
            int bestValue = Integer.MIN_VALUE;

            for (int candidate : remaining) {
                int value = calculate_value(instance, currentNode, candidate);
                if (value > bestValue) {
                    bestValue = value;
                    bestNode = candidate;
                }
            }

            if (bestNode == -1) break;
            tour.add(bestNode);
            remaining.remove(Integer.valueOf(bestNode));
        }

        Cycle cycle = new Cycle(tour);
        int totalDistance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int totalReward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objectiveValue = ObjectiveFunction.calculateValue(instance, cycle);

        return new Solution(cycle, totalReward, totalDistance, objectiveValue);
    }

    private int calculate_value(Instance instance, int currentNode, int nextNode) {
        switch (mode) {
            case COST:
                return instance.reward(nextNode) - instance.distance(currentNode, nextNode);
            case DISTANCE:
            default:
                return -instance.distance(currentNode, nextNode);
        }
    }
}
