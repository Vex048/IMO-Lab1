package heuristics;

import instance.Instance;
import solution.Cycle;
import solution.CycleDeltas;
import solution.ObjectiveFunction;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NearestNeighbourCostHeuristic implements Heuristic {
    private final boolean applyReduction;

    public NearestNeighbourCostHeuristic() {
        this(false);
    }

    public NearestNeighbourCostHeuristic(boolean applyReduction) {
        this.applyReduction = applyReduction;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        int n = instance.size();
        if (n == 0) {
            return new Solution(new Cycle(List.of()), 0, 0, 0);
        }

        int start = startNode >= 0 ? startNode : rng.nextInt(n);
        List<Integer> tour = new ArrayList<>();
        tour.add(start);

        boolean[] visited = new boolean[n];
        visited[start] = true;

        while (true) {
            int currentNode = tour.get(tour.size() - 1);
            int bestNode = -1;
            int bestValue = Integer.MIN_VALUE;

            for (int candidate = 0; candidate < n; candidate++) {
                if (visited[candidate]) continue;
                int value = instance.reward(candidate) - instance.distance(currentNode, candidate);
                if (value > bestValue) {
                    bestValue = value;
                    bestNode = candidate;
                }
            }

            if (bestNode == -1) break;
            tour.add(bestNode);
            visited[bestNode] = true;
        }

        if (applyReduction) {
            improveByCycleReduction(instance, tour);
        }

        Cycle cycle = new Cycle(tour);
        int totalDistance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int totalReward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objectiveValue = ObjectiveFunction.calculateValue(instance, cycle);

        return new Solution(cycle, totalReward, totalDistance, objectiveValue);
    }

    private void improveByCycleReduction(Instance instance, List<Integer> cycleNodes) {
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
}
