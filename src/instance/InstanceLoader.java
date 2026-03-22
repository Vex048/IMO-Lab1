package instance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InstanceLoader {
    public static Instance loadFromFile(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<Node> nodes = new ArrayList<>();

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(";");
            if (parts.length < 3) continue;

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int reward = Integer.parseInt(parts[2]);

            nodes.add(new Node(x, y, reward));
        }

        int[][] distanceMatrix = DistanceMatrixBuilder.buildEuclidean(nodes);
        return new Instance(nodes, distanceMatrix);
    }
}
