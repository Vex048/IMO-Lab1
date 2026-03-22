package heuristics;

import instance.Instance;
import solution.Cycle;
import solution.CycleDeltas;
import solution.ObjectiveFunction;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedTwoRegretHeuristic implements Heuristic {
    private final double regretWeight;
    private final double greedyWeight;
    private final boolean applyReduction;

    public WeightedTwoRegretHeuristic(double regretWeight, double greedyWeight) {
        this(regretWeight, greedyWeight, false);
    }

    public WeightedTwoRegretHeuristic(double regretWeight, double greedyWeight, boolean applyReduction) {
        this.regretWeight = regretWeight;
        this.greedyWeight = greedyWeight;
        this.applyReduction = applyReduction;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        int n = instance.size();
        List<Integer> tour = new ArrayList<>();

        if (n == 0) {
            Cycle cycle = new Cycle(tour);
            return new Solution(cycle, 0, 0, 0);
        }

        int seedNode;
        if (startNode == -1) {
            seedNode = rng.nextInt(n);
        } else if (startNode >= 0 && startNode < n) {
            seedNode = startNode;
        } else {
            throw new IllegalArgumentException("startNode out of bounds: " + startNode + ", expected -1 or [0, " + (n - 1) + "]");
        }
        tour.add(seedNode);

        boolean[] visited = new boolean[n];
        visited[seedNode] = true;

        while (tour.size() < n) {
            int bestNode = -1;
            int bestInsertAfter = -1;
            int bestDelta = Integer.MIN_VALUE;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (int candidate = 0; candidate < n; candidate++) {
                if (visited[candidate]) continue;

                int candidateBestDelta = Integer.MIN_VALUE;
                int candidateSecondBestDelta = Integer.MIN_VALUE;
                int candidateBestInsertAfter = -1;

                for (int insertAfter = 0; insertAfter < tour.size(); insertAfter++) {
                    int delta = ObjectiveFunction.calculateInsertObjectiveDelta(instance, tour, insertAfter, candidate);
                    if (delta > candidateBestDelta) {
                        candidateSecondBestDelta = candidateBestDelta;
                        candidateBestDelta = delta;
                        candidateBestInsertAfter = insertAfter;
                    } else if (delta > candidateSecondBestDelta) {
                        candidateSecondBestDelta = delta;
                    }
                }

                if (candidateSecondBestDelta == Integer.MIN_VALUE) {
                    candidateSecondBestDelta = candidateBestDelta;
                }

                int regret2 = candidateBestDelta - candidateSecondBestDelta;
                double score = regretWeight * regret2 + greedyWeight * candidateBestDelta;

                if (score > bestScore
                        || (Double.compare(score, bestScore) == 0 && candidateBestDelta > bestDelta)
                        || (Double.compare(score, bestScore) == 0 && candidateBestDelta == bestDelta && candidate < bestNode)) {
                    bestScore = score;
                    bestDelta = candidateBestDelta;
                    bestNode = candidate;
                    bestInsertAfter = candidateBestInsertAfter;
                }
            }

            if (bestNode == -1) break;

            int insertAt = bestInsertAfter + 1;
            tour.add(insertAt, bestNode);
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
