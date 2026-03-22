package experiments;

import heuristics.WeightedTwoRegretHeuristic;
import instance.Instance;
import instance.InstanceLoader;
import solution.Solution;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class WeightedTwoRegretExperiment implements Experiment {
    public enum Mode {
        HAMILTONIAN,
        REDUCTION
    }
    private final Path datasetPath;
    private final Mode mode;
    private final int startNode;
    private final Path savePath;
    private final Random rng;
    private final double regretWeight;
    private final double greedyWeight;

    public WeightedTwoRegretExperiment(Path datasetPath, Mode mode, int startNode) {
        this(datasetPath, mode, startNode, 1.0, 1.0, null, null);
    }

    public WeightedTwoRegretExperiment(Path datasetPath, Mode mode, int startNode, double regretWeight, double greedyWeight) {
        this(datasetPath, mode, startNode, regretWeight, greedyWeight, null, null);
    }

    public WeightedTwoRegretExperiment(Path datasetPath, Mode mode, int startNode, double regretWeight, double greedyWeight, Path savePath) {
        this(datasetPath, mode, startNode, regretWeight, greedyWeight, savePath, null);
    }

    public WeightedTwoRegretExperiment(Path datasetPath, Mode mode, int startNode, double regretWeight, double greedyWeight, Random rng) {
        this(datasetPath, mode, startNode, regretWeight, greedyWeight, null, rng);
    }

    public WeightedTwoRegretExperiment(Path datasetPath,
                                       Mode mode,
                                       int startNode,
                                       double regretWeight,
                                       double greedyWeight,
                                       Path savePath,
                                       Random rng) {
        this.datasetPath = datasetPath;
        this.mode = mode;
        this.startNode = startNode;
        this.regretWeight = regretWeight;
        this.greedyWeight = greedyWeight;
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

        boolean applyReduction = (mode == Mode.REDUCTION);
        WeightedTwoRegretHeuristic heuristic = new WeightedTwoRegretHeuristic(regretWeight, greedyWeight, applyReduction);
        Solution sol = heuristic.solve(instance, startNode, rng);

        if (savePath != null) {
            Path parent = savePath.getParent();
            if (parent != null) Files.createDirectories(parent);
            sol.saveToFile(savePath);
        }

        return new ExperimentResult(
                sol.getCycle().size(),
                sol.getTotalReward(),
                sol.getTotalDistance(),
                sol.objectiveValue(),
                sol.getCycle().getTour());
    }
}
