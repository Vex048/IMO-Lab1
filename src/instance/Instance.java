package instance;

import java.util.List;

public class Instance {
    private final List<Node> nodes;
    private final int[][] distanceMatrix;

    public Instance(List<Node> nodes, int[][] distanceMatrix) {
        this.nodes = nodes;
        this.distanceMatrix = distanceMatrix;
    }

    public int size() { return nodes.size(); }
    public int distance(int i, int j) { return distanceMatrix[i][j]; }
    public int reward(int i) { return nodes.get(i).getReward(); }
    public List<Node> getNodes() { return nodes; }
}
