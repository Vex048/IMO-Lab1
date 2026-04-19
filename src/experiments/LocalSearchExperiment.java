package experiments;

import heuristics.Heuristic;
import heuristics.LocalSearchHeuristic;
import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.SearchStrategy;
import instance.Instance;
import instance.InstanceLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import solution.Solution;

public class LocalSearchExperiment implements Experiment {
    private final Path datasetPath;
    private final SearchStrategy strategy;
    private final IntraRouteNeighborhood neighborhood;
    private final Heuristic initHeuristic;
    private final Heuristic heuristicOverride;
    private final int startNode;
    private final Path savePath;
    private final Random rng;

    public LocalSearchExperiment(Path datasetPath, SearchStrategy strategy, IntraRouteNeighborhood neighborhood, Heuristic initHeuristic, int startNode, Path savePath, Random rng) {
        this.datasetPath = datasetPath;
        this.strategy = strategy;
        this.neighborhood = neighborhood;
        this.initHeuristic = initHeuristic;
        this.heuristicOverride = null;
        this.startNode = startNode;
        this.savePath = (savePath != null && !savePath.toString().trim().isEmpty()) ? savePath : null;
        this.rng = (rng == null) ? new Random() : rng;
    }

    public LocalSearchExperiment(Path datasetPath, Heuristic heuristicOverride, int startNode, Path savePath, Random rng) {
        this.datasetPath = datasetPath;
        this.strategy = SearchStrategy.STEEPEST;
        this.neighborhood = IntraRouteNeighborhood.EDGE_SWAP;
        this.initHeuristic = null;
        this.heuristicOverride = heuristicOverride;
        this.startNode = startNode;
        this.savePath = (savePath != null && !savePath.toString().trim().isEmpty()) ? savePath : null;
        this.rng = (rng == null) ? new Random() : rng;
    }

    @Override
    public ExperimentResult run() throws Exception {
        Instance instance = InstanceLoader.loadFromFile(datasetPath);
        Heuristic h = (heuristicOverride != null)
                ? heuristicOverride
                : new LocalSearchHeuristic(strategy, neighborhood, initHeuristic);

        long start = System.currentTimeMillis();
        Solution sol = h.solve(instance, startNode, rng);
        long end = System.currentTimeMillis();
        long timeMs = end - start;

        if (savePath != null) {
            Path parent = savePath.getParent();
            if (parent != null) Files.createDirectories(parent);
            sol.saveToFile(savePath);
        }

        return new ExperimentResult(
                sol.getCycle().size(),
                sol.getTotalReward(),
                sol.getTotalDistance(),
                sol.getPhase1Distance(),
                sol.objectiveValue(),
                timeMs,
                sol.getCycle().getTour()
        );
    }
}