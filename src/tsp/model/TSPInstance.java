package tsp.model;

/**
 * Immutable representation of a TSP-with-profits problem instance.
 * <p>
 * After construction the {@link #distanceMatrix} is the sole source of
 * distance information.  Algorithms must never access node coordinates
 * directly — only the pre-computed matrix.
 *
 * @param name           human-readable instance name (e.g. file name)
 * @param nodes          array of all nodes (indexed 0 … n-1)
 * @param distanceMatrix symmetric n×n matrix of integer-rounded Euclidean distances
 */
public record TSPInstance(String name, Node[] nodes, int[][] distanceMatrix) {

    /** Number of nodes in this instance. */
    public int size() {
        return nodes.length;
    }

    /** Shorthand: distance between node {@code i} and node {@code j}. */
    public int distance(int i, int j) {
        return distanceMatrix[i][j];
    }

    /** Shorthand: profit of the node with index {@code i}. */
    public int profit(int i) {
        return nodes[i].profit();
    }
}
