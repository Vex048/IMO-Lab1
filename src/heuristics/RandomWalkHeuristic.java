package heuristics;

import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.Move;
import heuristics.localsearch.NeighbourhoodGenerator;
import instance.Instance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import solution.Cycle;
import solution.ObjectiveFunction;
import solution.Solution;

public class RandomWalkHeuristic implements Heuristic {

    private final long timeLimitMs;

    public RandomWalkHeuristic(long timeLimitMs) {
        this.timeLimitMs = timeLimitMs;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        Solution bestSolution = generateRandomSolution(instance, startNode, rng);
        int bestObjective = bestSolution.objectiveValue();

        List<Integer> currentTour = new ArrayList<>(bestSolution.getCycle().getTour());
        List<Integer> currentUnvisited = getUnvisitedNodes(instance, currentTour);

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeLimitMs) {
            List<Move> neighborhood = NeighbourhoodGenerator.generate(currentTour, currentUnvisited, IntraRouteNeighborhood.VERTEX_SWAP);

            if (neighborhood.isEmpty()) break;

            Move randomMove = neighborhood.get(rng.nextInt(neighborhood.size()));
            randomMove.apply(currentTour, currentUnvisited);

            Cycle currentCycle = new Cycle(currentTour);
            int currentDistance = ObjectiveFunction.calculateTotalDistance(instance, currentCycle);
            int currentReward = ObjectiveFunction.calculateTotalReward(instance, currentCycle);
            int currentObjective = ObjectiveFunction.calculateValue(currentReward, currentDistance);

            if (currentObjective > bestObjective) {
                bestObjective = currentObjective;
                bestSolution = new Solution(currentCycle, currentReward, currentDistance, currentObjective);
            }
        }

        return bestSolution;
    }

    private List<Integer> getUnvisitedNodes(Instance instance, List<Integer> tour) {
        List<Integer> unvisited = new ArrayList<>();
        int totalNodes = instance.size();
        for (int i = 0; i < totalNodes; i++) {
            if (!tour.contains(i)) {
                unvisited.add(i);
            }
        }
        return unvisited;
    }

    private Solution generateRandomSolution(Instance instance, int startNode, Random rng) {
        int actualStartNode = (startNode >= 0) ? startNode : rng.nextInt(instance.size());

        List<Integer> otherNodes = new ArrayList<>();
        for (int i = 0; i < instance.size(); i++) {
            if (i != actualStartNode) {
                otherNodes.add(i);
            }
        }
        Collections.shuffle(otherNodes, rng);

        int size = (int) Math.ceil(instance.size() / 2.0);
        List<Integer> randomTour = new ArrayList<>();
        randomTour.add(actualStartNode);
        randomTour.addAll(otherNodes.subList(0, size - 1));

        Cycle finalCycle = new Cycle(randomTour);
        int distance = ObjectiveFunction.calculateTotalDistance(instance, finalCycle);
        int reward = ObjectiveFunction.calculateTotalReward(instance, finalCycle);
        int objective = ObjectiveFunction.calculateValue(reward, distance);

        return new Solution(finalCycle, reward, distance, objective);
    }
}