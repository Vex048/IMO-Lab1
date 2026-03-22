package experiments;

import heuristics.NearestNeighbourHeuristic;
import heuristics.NearestNeighbourHeuristic.Mode;
import instance.Instance;
import instance.InstanceLoader;
import solution.Solution;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Random;

public class NearestNeighbourExperiment implements Experiment{
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

        NearestNeighbourHeuristic h = new NearestNeighbourHeuristic(mode);
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
                sol.getPhase1Distance(),
                sol.objectiveValue(),
                sol.getCycle().getTour());
    }
}
