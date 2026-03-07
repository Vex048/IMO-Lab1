package tsp.model;

/**
 * Represents a single node in a TSP instance.
 *
 * @param id     zero-based index of this node
 * @param x      x-coordinate (used only for distance matrix construction)
 * @param y      y-coordinate (used only for distance matrix construction)
 * @param profit  profit collected when this node is visited
 */
public record Node(int id, int x, int y, int profit) {
}
