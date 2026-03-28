package experiments;

import heuristics.Heuristic;
import heuristics.RandomWalkHeuristic;
import instance.Instance;
import instance.InstanceLoader;
import solution.Solution;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class RandomWalkExperiment implements Experiment {
    private final Path datasetPath;
    private final long timeLimitMs;
    private final int startNode;
    private final Path savePath;
    private final Random rng;

    public RandomWalkExperiment(Path datasetPath, long timeLimitMs, int startNode, Path savePath, Random rng) {
        this.datasetPath = datasetPath;
        this.timeLimitMs = timeLimitMs;
        this.startNode = startNode;
        this.savePath = (savePath != null && !savePath.toString().trim().isEmpty()) ? savePath : null;
        this.rng = (rng == null) ? new Random() : rng;
    }

    @Override
    public ExperimentResult run() throws Exception {
        Instance instance = InstanceLoader.loadFromFile(datasetPath);
        Heuristic h = new RandomWalkHeuristic(timeLimitMs);

        long start = System.currentTimeMillis();
        Solution sol = h.solve(instance, startNode, rng);
        long end = System.currentTimeMillis();
        long actualTimeMs = end - start;

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
                actualTimeMs,
                sol.getCycle().getTour()
        );
    }
}