package heuristics;

import heuristics.localsearch.AddNodeMove;
import heuristics.localsearch.EdgeSwapMove;
import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.NodeExchangeMove;
import heuristics.localsearch.RemoveNodeMove;
import instance.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import solution.Cycle;
import solution.ObjectiveFunction;
import solution.Solution;

public class IlsHeuristic implements Heuristic, IterationCountProvider {
    private final long timeLimitMs;
    private final int perturbationMoves;
    private final IntraRouteNeighborhood neighborhood;
    private int iterationCount;

    public IlsHeuristic(long timeLimitMs) {
        this(timeLimitMs, 5, IntraRouteNeighborhood.EDGE_SWAP);
    }

    public IlsHeuristic(long timeLimitMs, int perturbationMoves, IntraRouteNeighborhood neighborhood) {
        this.timeLimitMs = Math.max(0, timeLimitMs);
        this.perturbationMoves = Math.max(1, perturbationMoves);
        this.neighborhood = neighborhood;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        long endTime = System.currentTimeMillis() + timeLimitMs;
        Solution current = runLocalSearch(instance, startNode, rng);
        Solution best = current;
        iterationCount = 0;

        while (System.currentTimeMillis() < endTime) {
            iterationCount++;
            List<Integer> tour = new ArrayList<>(current.getCycle().getTour());
            List<Integer> unvisited = buildUnvisited(instance, tour);

            perturb(tour, unvisited, rng);
            Solution perturbed = buildSolution(instance, tour);
            Solution improved = runLocalSearchFromSeed(instance, perturbed, rng);

            if (improved.objectiveValue() > current.objectiveValue()) {
                current = improved;
            }
            if (improved.objectiveValue() > best.objectiveValue()) {
                best = improved;
            }
        }

        return best;
    }

    private Solution runLocalSearch(Instance instance, int startNode, Random rng) {
        return new LmSteepestLocalSearchHeuristic(neighborhood, null).solve(instance, startNode, rng);
    }

    private Solution runLocalSearchFromSeed(Instance instance, Solution seed, Random rng) {
        Heuristic seeded = new SeedSolutionHeuristic(seed);
        return new LmSteepestLocalSearchHeuristic(neighborhood, seeded).solve(instance, -1, rng);
    }

    private void perturb(List<Integer> tour, List<Integer> unvisited, Random rng) {
        if (tour.isEmpty()) {
            return;
        }

        for (int step = 0; step < perturbationMoves; step++) {
            int action = rng.nextInt(4);

            switch (action) {
                case 0 -> applyEdgeSwap(tour, unvisited, rng);
                case 1 -> applyNodeExchange(tour, unvisited, rng);
                case 2 -> applyAddNode(tour, unvisited, rng);
                case 3 -> applyRemoveNode(tour, unvisited, rng);
                default -> {
                }
            }
        }
    }

    private void applyEdgeSwap(List<Integer> tour, List<Integer> unvisited, Random rng) {
        if (tour.size() < 4) {
            return;
        }
        int i = rng.nextInt(tour.size() - 1);
        int j = i + 1 + rng.nextInt(tour.size() - i - 1);
        new EdgeSwapMove(i, j).apply(tour, unvisited);
    }

    private void applyNodeExchange(List<Integer> tour, List<Integer> unvisited, Random rng) {
        if (unvisited.isEmpty() || tour.isEmpty()) {
            return;
        }
        int i = rng.nextInt(tour.size());
        int node = unvisited.get(rng.nextInt(unvisited.size()));
        new NodeExchangeMove(i, node).apply(tour, unvisited);
    }

    private void applyAddNode(List<Integer> tour, List<Integer> unvisited, Random rng) {
        if (unvisited.isEmpty() || tour.isEmpty()) {
            return;
        }
        int i = rng.nextInt(tour.size());
        int node = unvisited.get(rng.nextInt(unvisited.size()));
        new AddNodeMove(i, node).apply(tour, unvisited);
    }

    private void applyRemoveNode(List<Integer> tour, List<Integer> unvisited, Random rng) {
        if (tour.size() <= 2) {
            return;
        }
        int i = rng.nextInt(tour.size());
        new RemoveNodeMove(i).apply(tour, unvisited);
    }

    private List<Integer> buildUnvisited(Instance instance, List<Integer> tour) {
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

    private Solution buildSolution(Instance instance, List<Integer> tour) {
        Cycle cycle = new Cycle(tour);
        int distance = ObjectiveFunction.calculateTotalDistance(instance, cycle);
        int reward = ObjectiveFunction.calculateTotalReward(instance, cycle);
        int objective = ObjectiveFunction.calculateValue(reward, distance);
        return new Solution(cycle, reward, distance, objective);
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    private static class SeedSolutionHeuristic implements Heuristic {
        private final Solution seed;

        private SeedSolutionHeuristic(Solution seed) {
            this.seed = seed;
        }

        @Override
        public Solution solve(Instance instance, int startNode, Random rng) {
            return seed;
        }
    }
}
