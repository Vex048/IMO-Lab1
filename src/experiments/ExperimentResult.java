package experiments;

import java.util.List;

public record ExperimentResult(int nodeCount, int totalReward, int totalDistance, int objectiveValue, List<Integer> tour) {

    @Override
    public String toString() {
        return String.format("Experiment(nodes=%d reward=%d distance=%d objective=%d)",
                nodeCount(),
                totalReward(),
                totalDistance(),
                objectiveValue()
        );
    }
}
