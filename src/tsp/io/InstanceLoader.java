package tsp.io;

import tsp.model.Node;
import tsp.model.TSPInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads TSP-with-profits instances from semicolon-delimited CSV files.
 * <p>
 * Expected line format: {@code x;y;profit}  (one node per line, no header).
 * After parsing, the loader builds a symmetric integer-rounded Euclidean
 * distance matrix so that algorithms never need raw coordinates.
 */
public final class InstanceLoader {

    private InstanceLoader() { }

    /**
     * Loads an instance from the given CSV file.
     *
     * @param path path to the CSV file
     * @return a fully constructed {@link TSPInstance}
     * @throws IOException if the file cannot be read or has an invalid format
     */
    public static TSPInstance load(Path path) throws IOException {
        List<Node> nodeList = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int id = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length != 3) {
                    throw new IOException("Invalid line format at node " + id + ": " + line);
                }
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                int profit = Integer.parseInt(parts[2].trim());
                nodeList.add(new Node(id, x, y, profit));
                id++;
            }
        }

        Node[] nodes = nodeList.toArray(Node[]::new);
        int[][] distanceMatrix = buildDistanceMatrix(nodes);
        String name = path.getFileName().toString().replaceFirst("\\.[^.]+$", "");
        return new TSPInstance(name, nodes, distanceMatrix);
    }

    /**
     * Builds a symmetric distance matrix using integer-rounded Euclidean distances.
     */
    private static int[][] buildDistanceMatrix(Node[] nodes) {
        int n = nodes.length;
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double dx = nodes[i].x() - nodes[j].x();
                double dy = nodes[i].y() - nodes[j].y();
                int dist = (int) Math.round(Math.sqrt(dx * dx + dy * dy));
                matrix[i][j] = dist;
                matrix[j][i] = dist;
            }
        }
        return matrix;
    }
}
