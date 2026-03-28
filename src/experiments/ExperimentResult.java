package experiments;

import java.util.List;

public record ExperimentResult(int nodeCount,
                               int totalReward,
                               int totalDistance,
                               int phase1Distance,
                               int objectiveValue,
                               long timeMs,
                               List<Integer> tour) {

    public ExperimentResult(int nodeCount, int totalReward, int totalDistance, int objectiveValue, List<Integer> tour) {
        this(nodeCount, totalReward, totalDistance, totalDistance, objectiveValue, 0L, tour);
    }

    public ExperimentResult(int nodeCount, int totalReward, int totalDistance, int phase1Distance, int objectiveValue, List<Integer> tour) {
        this(nodeCount, totalReward, totalDistance, phase1Distance, objectiveValue, 0L, tour);
    }

    @Override
    public String toString() {
        return String.format("Experiment(nodes=%d reward=%d distance=%d phase1Distance=%d objective=%d time=%dms)",
                nodeCount(),
                totalReward(),
                totalDistance(),
                phase1Distance(),
                objectiveValue(),
                timeMs()
        );
    }
}