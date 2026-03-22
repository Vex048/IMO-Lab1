package experiments;

import heuristics.GreedyCycleDistanceHeuristic;
import heuristics.GreedyCycleObjectiveHeuristic;
import heuristics.Heuristic;
import instance.Instance;
import instance.InstanceLoader;
import solution.Solution;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class GreedyCycleExperiment implements Experiment {
    public enum Mode {
        DISTANCE,
        OBJECTIVE
    }

    private final Path datasetPath;
    private final Mode mode;
    private final int startNode;
    private final Path savePath;
    private final Random rng;

    public GreedyCycleExperiment(Path datasetPath, Mode mode, int startNode) {
        this(datasetPath, mode, startNode, null, null);
    }

    public GreedyCycleExperiment(Path datasetPath, Mode mode, int startNode, Path savePath) {
        this(datasetPath, mode, startNode, savePath, null);
    }

    public GreedyCycleExperiment(Path datasetPath, Mode mode, int startNode, Random rng) {
        this(datasetPath, mode, startNode, null, rng);
    }

    public GreedyCycleExperiment(Path datasetPath, Mode mode, int startNode, Path savePath, Random rng) {
        this.datasetPath = datasetPath;
        this.mode = mode;
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
        Heuristic heuristic = createHeuristic(mode);
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

    private Heuristic createHeuristic(Mode selectedMode) {
        switch (selectedMode) {
            case OBJECTIVE:
                return new GreedyCycleObjectiveHeuristic();
            case DISTANCE:
            default:
                return new GreedyCycleDistanceHeuristic();
        }
    }
}

