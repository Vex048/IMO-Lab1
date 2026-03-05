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

    public Solution(Cycle cycle, int totalReward, int totalDistance, int objectiveValue) {
        this.cycle = cycle;
        this.totalReward = totalReward;
        this.totalDistance = totalDistance;
        this.objectiveValue = objectiveValue;
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

    public void saveToFile(Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Integer idx : cycle.getTour()) {
            lines.add(idx.toString());
        }
        Files.write(path, lines);
    }
}
