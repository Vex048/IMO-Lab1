package heuristics;

import heuristics.localsearch.CandidateLists;
import heuristics.localsearch.CandidateNeighbourhoodGenerator;
import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.Move;
import instance.Instance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import solution.Cycle;
import solution.ObjectiveFunction;
import solution.Solution;

public class CandidateSteepestLocalSearchHeuristic implements Heuristic {
    private final IntraRouteNeighborhood intraRouteType;
    private final Heuristic initHeuristic;
    private final int candidateCount;

    public CandidateSteepestLocalSearchHeuristic() {
        this(IntraRouteNeighborhood.EDGE_SWAP, null, 10);
    }

    public CandidateSteepestLocalSearchHeuristic(IntraRouteNeighborhood intraRouteType,
                                                 Heuristic initHeuristic,
                                                 int candidateCount) {
        this.intraRouteType = intraRouteType;
        this.initHeuristic = initHeuristic;
        this.candidateCount = candidateCount;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        Solution initialSolution = (initHeuristic != null)
                ? initHeuristic.solve(instance, startNode, rng)
                : generateRandomSolution(instance, startNode, rng);

        List<Integer> tour = new ArrayList<>(initialSolution.getCycle().getTour());
        List<Integer> unvisited = getUnvisitedNodes(instance, tour);
        CandidateLists candidateLists = CandidateLists.build(instance, candidateCount);

        boolean improvementFound = true;
        while (improvementFound) {
            improvementFound = false;

            List<Move> neighborhood = CandidateNeighbourhoodGenerator.generate(
                    instance,
                    tour,
                    unvisited,
                    intraRouteType,
                    candidateLists
            );

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

        Cycle finalCycle = new Cycle(tour);
        int finalDistance = ObjectiveFunction.calculateTotalDistance(instance, finalCycle);
        int finalReward = ObjectiveFunction.calculateTotalReward(instance, finalCycle);
        int finalObjective = ObjectiveFunction.calculateValue(finalReward, finalDistance);

        return new Solution(finalCycle, finalReward, finalDistance, finalObjective);
    }

    private List<Integer> getUnvisitedNodes(Instance instance, List<Integer> tour) {
        boolean[] inTour = new boolean[instance.size()];
        for (int node : tour) {
            inTour[node] = true;
        }

        List<Integer> unvisited = new ArrayList<>();
        for (int i = 0; i < instance.size(); i++) {
            if (!inTour[i]) {
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

        Cycle cycle = new Cycle(randomTour);
        int distance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int reward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objective = ObjectiveFunction.calculateValue(reward, distance);

        return new Solution(cycle, reward, distance, objective);
    }
}