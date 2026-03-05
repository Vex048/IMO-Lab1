package instance;

import java.util.List;

public class DistanceMatrixBuilder {
    public static int[][] buildEuclidean(List<Node> nodes) {
        int n = nodes.size();
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int dx = nodes.get(i).getX() - nodes.get(j).getX();
                int dy = nodes.get(i).getY() - nodes.get(j).getY();
                matrix[i][j] = (int) Math.round(Math.sqrt(dx * dx + dy * dy));
            }
        }
        return matrix;
    }
}
