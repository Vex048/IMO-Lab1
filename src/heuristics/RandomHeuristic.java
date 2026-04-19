package heuristics;

import instance.Instance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import solution.Cycle;
import solution.ObjectiveFunction;
import solution.Solution;

public class RandomHeuristic implements Heuristic {

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        int n = instance.size();
        if (n == 0) {
            return new Solution(new Cycle(List.of()), 0, 0, 0, 0);
        }

        int count = 1 + rng.nextInt(n);

        Set<Integer> selectedSet = new HashSet<>();
        while (selectedSet.size() < count) {
            selectedSet.add(rng.nextInt(n));
        }

        List<Integer> selected = new ArrayList<>(selectedSet);
        Collections.shuffle(selected, rng);

        Cycle cycle = new Cycle(selected);
        int totalDistance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int totalReward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objectiveValue = ObjectiveFunction.calculateValue(totalReward, totalDistance);
        return new Solution(cycle, totalReward, totalDistance, objectiveValue, totalDistance);
    }
}

