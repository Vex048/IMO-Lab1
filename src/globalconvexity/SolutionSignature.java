package globalconvexity;

import java.util.BitSet;
import java.util.List;
import solution.Solution;

public record SolutionSignature(BitSet vertices, BitSet edges) {
    public static SolutionSignature from(Solution solution, int nodeCount) {
        BitSet vertices = new BitSet(nodeCount);
        BitSet edges = new BitSet(nodeCount * nodeCount);

        List<Integer> tour = solution.getCycle().getTour();
        for (int node : tour) {
            vertices.set(node);
        }

        if (tour.size() > 1) {
            for (int i = 0; i < tour.size(); i++) {
                int a = tour.get(i);
                int b = tour.get((i + 1) % tour.size());
                edges.set(edgeIndex(a, b, nodeCount));
            }
        }

        return new SolutionSignature(vertices, edges);
    }

    private static int edgeIndex(int a, int b, int nodeCount) {
        int first = Math.min(a, b);
        int second = Math.max(a, b);
        return first * nodeCount + second;
    }
}

