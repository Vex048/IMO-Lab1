package heuristics;

import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.Move;
import heuristics.localsearch.NeighbourhoodGenerator;
import heuristics.localsearch.SearchStrategy;
import heuristics.localsearch.SteepestAccelerationMode;
import instance.Instance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import solution.Cycle;
import solution.ObjectiveFunction;
import solution.Solution;

public class LocalSearchHeuristic implements Heuristic {

    private final SearchStrategy strategy;
    private final IntraRouteNeighborhood intraRouteType;
    private final Heuristic initHeuristic;
    private final SteepestAccelerationMode accelerationMode;
    private final int candidateK;

    public LocalSearchHeuristic(SearchStrategy strategy, IntraRouteNeighborhood intraRouteType, Heuristic initHeuristic) {
        this(strategy, intraRouteType, initHeuristic, SteepestAccelerationMode.NONE, 10);
    }

    public LocalSearchHeuristic(SearchStrategy strategy,
                                IntraRouteNeighborhood intraRouteType,
                                Heuristic initHeuristic,
                                SteepestAccelerationMode accelerationMode,
                                int candidateK) {
        this.strategy = strategy;
        this.intraRouteType = intraRouteType;
        this.initHeuristic = initHeuristic;
        this.accelerationMode = accelerationMode;
        this.candidateK = candidateK;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        Solution initialSolution = (initHeuristic != null)
                ? initHeuristic.solve(instance, startNode, rng)
                : generateRandomSolution(instance, startNode, rng);

        List<Integer> tour = new ArrayList<>(initialSolution.getCycle().getTour());
        List<Integer> unvisited = getUnvisitedNodes(instance, tour);

        int[][] nearestCandidates = null;
        if (accelerationMode == SteepestAccelerationMode.CANDIDATE) {
            nearestCandidates = NeighbourhoodGenerator.buildNearestCandidates(instance, candidateK);
        }

        if (strategy == SearchStrategy.STEEPEST && accelerationMode == SteepestAccelerationMode.MOVE_LIST) {
            runSteepestWithMoveList(instance, tour, unvisited);
        } else {
            runClassicSearch(instance, tour, unvisited, rng, nearestCandidates);
        }

        Cycle finalCycle = new Cycle(tour);
        int finalDistance = ObjectiveFunction.calculateTotalDistance(instance, finalCycle);
        int finalReward = ObjectiveFunction.calculateTotalReward(instance, finalCycle);
        int finalObjective = ObjectiveFunction.calculateValue(finalReward, finalDistance);

        return new Solution(finalCycle, finalReward, finalDistance, finalObjective);
    }

    private void runClassicSearch(Instance instance,
                                  List<Integer> tour,
                                  List<Integer> unvisited,
                                  Random rng,
                                  int[][] nearestCandidates) {
        boolean improvementFound = true;
        while (improvementFound) {
            improvementFound = false;

            List<Move> neighborhood = generateNeighborhood(instance, tour, unvisited, nearestCandidates);

            if (strategy == SearchStrategy.GREEDY) {
                Collections.shuffle(neighborhood, rng);
                for (Move move : neighborhood) {
                    if (move.applicability(tour, unvisited) != Move.Applicability.APPLICABLE) {
                        continue;
                    }
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
                    if (move.applicability(tour, unvisited) != Move.Applicability.APPLICABLE) {
                        continue;
                    }
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
    }

    private void runSteepestWithMoveList(Instance instance, List<Integer> tour, List<Integer> unvisited) {
        List<ScoredMove> improvingMoves = new ArrayList<>();

        while (true) {
            List<Move> neighborhood = NeighbourhoodGenerator.generate(tour, unvisited, intraRouteType);
            for (Move move : neighborhood) {
                Move.Applicability applicability = move.applicability(tour, unvisited);
                if (applicability != Move.Applicability.APPLICABLE) {
                    continue;
                }
                int delta = move.evaluateDelta(instance, tour);
                if (delta > 0) {
                    improvingMoves.add(new ScoredMove(move, delta));
                }
            }
            improvingMoves.sort(Comparator.comparingInt(ScoredMove::delta).reversed());

            Move selectedMove = null;
            for (int i = 0; i < improvingMoves.size(); i++) {
                ScoredMove candidate = improvingMoves.get(i);
                Move.Applicability applicability = candidate.move().applicability(tour, unvisited);
                if (applicability == Move.Applicability.REMOVE) {
                    improvingMoves.remove(i);
                    i--;
                    continue;
                }
                if (applicability == Move.Applicability.KEEP_FOR_LATER) {
                    continue;
                }

                int refreshedDelta = candidate.move().evaluateDelta(instance, tour);
                if (refreshedDelta <= 0) {
                    improvingMoves.remove(i);
                    i--;
                    continue;
                }
                selectedMove = candidate.move();
                break;
            }

            if (selectedMove == null) {
                break;
            }

            selectedMove.apply(tour, unvisited);
        }
    }

    private List<Move> generateNeighborhood(Instance instance,
                                            List<Integer> tour,
                                            List<Integer> unvisited,
                                            int[][] nearestCandidates) {
        if (strategy == SearchStrategy.STEEPEST && accelerationMode == SteepestAccelerationMode.CANDIDATE) {
            return NeighbourhoodGenerator.generateCandidate(tour, unvisited, intraRouteType, instance, nearestCandidates);
        }
        return NeighbourhoodGenerator.generate(tour, unvisited, intraRouteType);
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

    private record ScoredMove(Move move, int delta) {
    }
}