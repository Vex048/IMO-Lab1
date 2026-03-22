package experiments;

import heuristics.RandomHeuristic;
import instance.Instance;
import instance.InstanceLoader;
import solution.Solution;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class RandomExperiment implements Experiment {
    private final Path datasetPath;
    private final int startNode;
    private final Path savePath;
    private final Random rng;

    public RandomExperiment(Path datasetPath, int startNode) {
        this(datasetPath, startNode, null, null);
    }

    public RandomExperiment(Path datasetPath, int startNode, Path savePath) {
        this(datasetPath, startNode, savePath, null);
    }

    public RandomExperiment(Path datasetPath, int startNode, Random rng) {
        this(datasetPath, startNode, null, rng);
    }

    public RandomExperiment(Path datasetPath, int startNode, Path savePath, Random rng) {
        this.datasetPath = datasetPath;
        this.startNode = startNode;
        this.savePath = (savePath != null && savePath.toString().trim().isEmpty()) ? null : savePath;
        this.rng = (rng == null) ? new Random() : rng;
    }

    @Override
    public ExperimentResult run() throws Exception {
        Instance instance = InstanceLoader.loadFromFile(datasetPath);
        RandomHeuristic heuristic = new RandomHeuristic();
        Solution sol = heuristic.solve(instance, startNode, rng);

        if (savePath != null) {
            Path parent = savePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            sol.saveToFile(savePath);
        }

        return new ExperimentResult(
                sol.getCycle().size(),
                sol.getTotalReward(),
                sol.getTotalDistance(),
                sol.getPhase1Distance(),
                sol.objectiveValue(),
                sol.getCycle().getTour());
    }
}
