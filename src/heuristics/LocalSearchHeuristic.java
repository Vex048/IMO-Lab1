package heuristics;

import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.Move;
import heuristics.localsearch.NeighbourhoodGenerator;
import heuristics.localsearch.SearchStrategy;
import instance.Instance;
import solution.Cycle;
import solution.ObjectiveFunction;
import solution.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LocalSearchHeuristic implements Heuristic {

    private final SearchStrategy strategy;
    private final IntraRouteNeighborhood intraRouteType;
    private final Heuristic initHeuristic;

    public LocalSearchHeuristic(SearchStrategy strategy, IntraRouteNeighborhood intraRouteType, Heuristic initHeuristic) {
        this.strategy = strategy;
        this.intraRouteType = intraRouteType;
        this.initHeuristic = initHeuristic;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        Solution initialSolution = (initHeuristic != null)
                ? initHeuristic.solve(instance, startNode, rng)
                : generateRandomSolution(instance, startNode, rng);

        List<Integer> tour = new ArrayList<>(initialSolution.getCycle().getTour());
        List<Integer> unvisited = getUnvisitedNodes(instance, tour);

        boolean improvementFound = true;

        while (improvementFound) {
            improvementFound = false;

            List<Move> neighborhood = NeighbourhoodGenerator.generate(tour, unvisited, intraRouteType);

            if (strategy == SearchStrategy.GREEDY) {
                Collections.shuffle(neighborhood, rng);
                for (Move move : neighborhood) {
                    int delta = move.evaluateDelta(instance, tour);
                    if (delta > 0) {
                        move.apply(tour, unvisited);
                        improvementFound = true;
                        break;
                    }
                }
            } else if (strategy == SearchStrategy.STEEPEST) {
                Move bestMove = null;
                int bestDelta = 0;

                for (Move move : neighborhood) {
                    int delta = move.evaluateDelta(instance, tour);
                    if (delta > bestDelta) {
                        bestDelta = delta;
                        bestMove = move;
                    }
                }

                if (bestMove != null) {
                    bestMove.apply(tour, unvisited);
                    improvementFound = true;
                }
            }
        }

        Cycle finalCycle = new Cycle(tour);
        int finalDistance = ObjectiveFunction.calculateTotalDistance(instance, finalCycle);
        int finalReward = ObjectiveFunction.calculateTotalReward(instance, finalCycle);
        int finalObjective = ObjectiveFunction.calculateValue(instance, finalCycle);

        return new Solution(finalCycle, finalReward, finalDistance, finalObjective);
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
        int objective = ObjectiveFunction.calculateValue(instance, finalCycle);

        return new Solution(finalCycle, reward, distance, objective);
    }
}