package experiments;

import heuristics.NearestNeighbourCostHeuristic;
import heuristics.NearestNeighbourDistanceHeuristic;
import heuristics.Heuristic;
import instance.Instance;
import instance.InstanceLoader;
import solution.Solution;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Random;

public class NearestNeighbourExperiment implements Experiment {
    public enum Mode {
        DISTANCE,
        COST
    }
    private final Path datasetPath;
    private final Mode mode;
    private final int startNode;
    private final Path savePath;
    private final Random rng;

    public NearestNeighbourExperiment(Path datasetPath, Mode mode, int startNode) {
        this(datasetPath, mode, startNode, null, null);
    }

    public NearestNeighbourExperiment(Path datasetPath, Mode mode, int startNode, Path savePath) {
        this(datasetPath, mode, startNode, savePath, null);
    }

    public NearestNeighbourExperiment(Path datasetPath, Mode mode, int startNode, Random rng) {
        this(datasetPath, mode, startNode, null, rng);
    }

    public NearestNeighbourExperiment(Path datasetPath, Mode mode, int startNode, Path savePath, Random rng) {
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

        Heuristic h = createHeuristic(mode);
        Solution sol = h.solve(instance, startNode, this.rng);

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
    private Heuristic createHeuristic(Mode selectedMode) {
        switch (selectedMode) {
            case COST:
                return new NearestNeighbourCostHeuristic();
            case DISTANCE:
            default:
                return new NearestNeighbourDistanceHeuristic();
        }
    }
}
