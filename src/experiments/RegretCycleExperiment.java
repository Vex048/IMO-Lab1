package experiments;

import heuristics.RegretCycleHeuristic;
import instance.Instance;
import instance.InstanceLoader;
import solution.Solution;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class RegretCycleExperiment implements Experiment {
    private final Path datasetPath;
    private final int startNode;
    private final Path savePath;
    private final Random rng;

    public RegretCycleExperiment(Path datasetPath, int startNode) {
        this(datasetPath, startNode, null, null);
    }

    public RegretCycleExperiment(Path datasetPath, int startNode, Path savePath) {
        this(datasetPath, startNode, savePath, null);
    }

    public RegretCycleExperiment(Path datasetPath, int startNode, Random rng) {
        this(datasetPath, startNode, null, rng);
    }

    public RegretCycleExperiment(Path datasetPath, int startNode, Path savePath, Random rng) {
        this.datasetPath = datasetPath;
        this.startNode = startNode;

        if (savePath != null && savePath.toString().trim().isEmpty()) {
            this.savePath = null;
        } else {
            this.savePath = savePath;
        }
        this.rng = (rng == null) ? new Random() : rng;
    }

    @Override
    public ExperimentResult run() throws Exception {
        Instance instance = InstanceLoader.loadFromFile(datasetPath);

        RegretCycleHeuristic heuristic = new RegretCycleHeuristic();
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

