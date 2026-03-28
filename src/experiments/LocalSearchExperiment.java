package experiments;

import heuristics.Heuristic;
import heuristics.LocalSearchHeuristic;
import heuristics.localsearch.SearchStrategy;
import heuristics.localsearch.IntraRouteNeighborhood;
import instance.Instance;
import instance.InstanceLoader;
import solution.Solution;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class LocalSearchExperiment implements Experiment {
    private final Path datasetPath;
    private final SearchStrategy strategy;
    private final IntraRouteNeighborhood neighborhood;
    private final Heuristic initHeuristic;
    private final int startNode;
    private final Path savePath;
    private final Random rng;

    public LocalSearchExperiment(Path datasetPath, SearchStrategy strategy, IntraRouteNeighborhood neighborhood, Heuristic initHeuristic, int startNode, Path savePath, Random rng) {
        this.datasetPath = datasetPath;
        this.strategy = strategy;
        this.neighborhood = neighborhood;
        this.initHeuristic = initHeuristic;
        this.startNode = startNode;
        this.savePath = (savePath != null && !savePath.toString().trim().isEmpty()) ? savePath : null;
        this.rng = (rng == null) ? new Random() : rng;
    }

    @Override
    public ExperimentResult run() throws Exception {
        Instance instance = InstanceLoader.loadFromFile(datasetPath);
        Heuristic h = new LocalSearchHeuristic(strategy, neighborhood, initHeuristic);

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