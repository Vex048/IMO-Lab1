package heuristics;

import instance.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import solution.Cycle;
import solution.CycleDeltas;
import solution.ObjectiveFunction;
import solution.Solution;

public class GreedyCycleObjectiveHeuristic implements Heuristic {

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

        int start = startNode >= 0 ? startNode : rng.nextInt(n);
        List<Integer> cycleNodes = constructHamiltonianCycle(instance, start);
        int phase1Distance = ObjectiveFunction.calculateTotalDistance(instance, new Cycle(cycleNodes));
        improveByCycleRemoval(instance, cycleNodes);

        Cycle cycle = new Cycle(cycleNodes);
        int totalDistance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int totalReward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objectiveValue = ObjectiveFunction.calculateValue(totalReward, totalDistance);
        return new Solution(cycle, totalReward, totalDistance, objectiveValue, phase1Distance);
    }

    private List<Integer> constructHamiltonianCycle(Instance instance, int start) {
        int n = instance.size();
        boolean[] inCycle = new boolean[n];

        int nearest = -1;
        int nearestDist = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (i == start) {
                continue;
            }
            int d = instance.distance(start, i);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = i;
            }
        }

        List<Integer> cycleNodes = new ArrayList<>();
        cycleNodes.add(start);
        cycleNodes.add(nearest);
        inCycle[start] = true;
        inCycle[nearest] = true;

        for (int added = 2; added < n; added++) {
            int bestNode = -1;
            int bestPos = -1;
            int bestObjDelta = Integer.MIN_VALUE;

            for (int candidate = 0; candidate < n; candidate++) {
                if (inCycle[candidate]) {
                    continue;
                }
                for (int pos = 0; pos < cycleNodes.size(); pos++) {
                    int delta = CycleDeltas.insertionObjectiveDelta(instance, cycleNodes, pos, candidate);
                    if (delta > bestObjDelta) {
                        bestObjDelta = delta;
                        bestNode = candidate;
                        bestPos = pos;
                    }
                }
            }

            cycleNodes.add(bestPos + 1, bestNode);
            inCycle[bestNode] = true;
        }

        return cycleNodes;
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
}

