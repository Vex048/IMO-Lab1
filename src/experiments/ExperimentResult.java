package experiments;

import java.util.List;

public record ExperimentResult(int nodeCount,
                               int totalReward,
                               int totalDistance,
                               int phase1Distance,
                               int objectiveValue,
                               Integer iterationCount,
                               long timeMs,
                               List<Integer> tour) {

    public ExperimentResult(int nodeCount, int totalReward, int totalDistance, int objectiveValue, List<Integer> tour) {
        this(nodeCount, totalReward, totalDistance, totalDistance, objectiveValue, null, 0L, tour);
    }

    public ExperimentResult(int nodeCount, int totalReward, int totalDistance, int phase1Distance, int objectiveValue, List<Integer> tour) {
        this(nodeCount, totalReward, totalDistance, phase1Distance, objectiveValue, null, 0L, tour);
    }

    public ExperimentResult(int nodeCount,
                            int totalReward,
                            int totalDistance,
                            int phase1Distance,
                            int objectiveValue,
                            long timeMs,
                            List<Integer> tour) {
        this(nodeCount, totalReward, totalDistance, phase1Distance, objectiveValue, null, timeMs, tour);
    }

    @Override
    public String toString() {
        return String.format("Experiment(nodes=%d reward=%d distance=%d phase1Distance=%d objective=%d iterations=%s time=%dms)",
                nodeCount(),
                totalReward(),
                totalDistance(),
                phase1Distance(),
                objectiveValue(),
                iterationCount() == null ? "-" : iterationCount().toString(),
                timeMs()
        );
    }
}
