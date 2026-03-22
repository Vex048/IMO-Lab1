package heuristics;

import instance.Instance;
import solution.Cycle;
import solution.CycleDeltas;
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
        if (n == 0) {
            return new Solution(new Cycle(List.of()), 0, 0, 0, 0);
        }
        if (n == 1) {
            List<Integer> single = List.of(0);
            Cycle cycle = new Cycle(single);
            int distance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
            int reward = ObjectiveFunction.calculateTotalReward(instance, cycle);
            return new Solution(cycle, reward, distance, reward - distance, distance);
        }

        int start = (startNode >= 0 && startNode < n) ? startNode : rng.nextInt(n);
        List<Integer> tour = constructHamiltonianCycle(instance, start);
        int phase1Distance = ObjectiveFunction.calculateTotalDistance(instance, new Cycle(tour));
        improveByCycleRemoval(instance, tour);

        Cycle cycle = new Cycle(tour);
        int totalDistance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int totalReward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objectiveValue = ObjectiveFunction.calculateValue(instance, cycle);

        return new Solution(cycle, totalReward, totalDistance, objectiveValue, phase1Distance);
    }

    private List<Integer> constructHamiltonianCycle(Instance instance, int startNode) {
        int n = instance.size();
        List<Integer> tour = new ArrayList<>();
        tour.add(startNode);

        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i != startNode) remaining.add(i);
        }

        while (!remaining.isEmpty()) {
            int bestNode = -1;
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

        return tour;
    }

    private void improveByCycleRemoval(Instance instance, List<Integer> cycleNodes) {
        boolean improved = true;

        while (improved && cycleNodes.size() > 2) {
            improved = false;
            int bestPos = -1;
            int bestDelta = 0;

            for (int pos = 0; pos < cycleNodes.size(); pos++) {
                int delta = CycleDeltas.removalObjectiveDelta(instance, cycleNodes, pos);
                if (delta > bestDelta) {
                    bestDelta = delta;
                    bestPos = pos;
                }
            }

            if (bestDelta > 0) {
                cycleNodes.remove(bestPos);
                improved = true;
            }
        }
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
