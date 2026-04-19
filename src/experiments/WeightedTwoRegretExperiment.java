package experiments;

import heuristics.WeightedTwoRegretHeuristic;
import instance.Instance;
import instance.InstanceLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import solution.Solution;

public class WeightedTwoRegretExperiment implements Experiment {
    public enum Mode {
        HAMILTONIAN,
        REDUCTION
    }

    private static final double DEFAULT_REGRET_WEIGHT = 1.0;
    private static final double DEFAULT_GREEDY_WEIGHT = 1.0;

    private final Path datasetPath;
    private final boolean applyReduction;
    private final int startNode;
    private final Path savePath;
    private final Random rng;
    private final double regretWeight;
    private final double greedyWeight;

    // Konstruktor zgodny z wzorcem innych eksperymentow.
    public WeightedTwoRegretExperiment(Path datasetPath, int startNode) {
        this(datasetPath, startNode, DEFAULT_REGRET_WEIGHT, DEFAULT_GREEDY_WEIGHT, true, null, null);
    }

    // Konstruktor wymagany przez ExperimentBatchRunner (source of truth).
    public WeightedTwoRegretExperiment(Path datasetPath, int startNode, double regretWeight, Path savePath, Random rng) {
        this(datasetPath, startNode, regretWeight, DEFAULT_GREEDY_WEIGHT, true, savePath, rng);
    }

    public WeightedTwoRegretExperiment(Path datasetPath,
                                       int startNode,
                                       double regretWeight,
                                       double greedyWeight,
                                       boolean applyReduction,
                                       Path savePath,
                                       Random rng) {
        this.datasetPath = datasetPath;
        this.startNode = startNode;
        this.regretWeight = regretWeight;
        this.greedyWeight = greedyWeight;
        this.applyReduction = applyReduction;
        if (savePath != null && savePath.toString().trim().isEmpty()) {
            this.savePath = null;
        } else {
            this.savePath = savePath;
        }
        this.rng = (rng == null) ? new Random() : rng;
    }

    // Kompatybilnosc wsteczna z poprzednim API opartym o Mode.
    public WeightedTwoRegretExperiment(Path datasetPath, Mode mode, int startNode) {
        this(datasetPath, startNode, DEFAULT_REGRET_WEIGHT, DEFAULT_GREEDY_WEIGHT, mode == Mode.REDUCTION, null, null);
    }

    public WeightedTwoRegretExperiment(Path datasetPath, Mode mode, int startNode, double regretWeight, double greedyWeight) {
        this(datasetPath, startNode, regretWeight, greedyWeight, mode == Mode.REDUCTION, null, null);
    }

    public WeightedTwoRegretExperiment(Path datasetPath, Mode mode, int startNode, double regretWeight, double greedyWeight, Path savePath) {
        this(datasetPath, startNode, regretWeight, greedyWeight, mode == Mode.REDUCTION, savePath, null);
    }

    public WeightedTwoRegretExperiment(Path datasetPath, Mode mode, int startNode, double regretWeight, double greedyWeight, Random rng) {
        this(datasetPath, startNode, regretWeight, greedyWeight, mode == Mode.REDUCTION, null, rng);
    }

    @Override
    public ExperimentResult run() throws Exception {
        Instance instance = InstanceLoader.loadFromFile(datasetPath);

        WeightedTwoRegretHeuristic heuristic = new WeightedTwoRegretHeuristic(regretWeight, greedyWeight, applyReduction);
        long start = System.currentTimeMillis();
        Solution sol = heuristic.solve(instance, startNode, rng);
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
                sol.getCycle().getTour());
    }
}
