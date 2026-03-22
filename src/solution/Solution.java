package solution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Solution {
    private final Cycle cycle;
    private final int totalReward;
    private final int totalDistance;
    private final int objectiveValue;
    private final int phase1Distance;

    public Solution(Cycle cycle, int totalReward, int totalDistance, int objectiveValue) {
        this(cycle, totalReward, totalDistance, objectiveValue, totalDistance);
    }

    public Solution(Cycle cycle, int totalReward, int totalDistance, int objectiveValue, int phase1Distance) {
        this.cycle = cycle;
        this.totalReward = totalReward;
        this.totalDistance = totalDistance;
        this.objectiveValue = objectiveValue;
        this.phase1Distance = phase1Distance;
    }

    public Cycle getCycle() {
        return cycle;
    }

    public int getTotalReward() {
        return totalReward;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public int objectiveValue() {return objectiveValue; }

    public int getPhase1Distance() {
        return phase1Distance;
    }

    public void saveToFile(Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Integer idx : cycle.getTour()) {
            lines.add(idx.toString());
        }
        Files.write(path, lines);
    }
}
