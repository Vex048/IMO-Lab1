package heuristics.localsearch;

import instance.Instance;
import java.util.ArrayList;
import java.util.List;

public final class CandidateLists {
    private final int[][] nearest;
    private final boolean[][] candidateEdge;

    private CandidateLists(int[][] nearest, boolean[][] candidateEdge) {
        this.nearest = nearest;
        this.candidateEdge = candidateEdge;
    }

    public static CandidateLists build(Instance instance, int k) {
        int n = instance.size();
        int limit = Math.max(0, Math.min(k, n - 1));

        int[][] nearest = new int[n][limit];
        boolean[][] candidateEdge = new boolean[n][n];

        for (int node = 0; node < n; node++) {
            int sourceNode = node;
            List<Integer> others = new ArrayList<>(Math.max(0, n - 1));
            for (int other = 0; other < n; other++) {
                if (other != sourceNode) {
                    others.add(other);
                }
            }

            others.sort((a, b) -> {
                int da = instance.distance(sourceNode, a);
                int db = instance.distance(sourceNode, b);
                if (da != db) {
                    return Integer.compare(da, db);
                }
                return Integer.compare(a, b);
            });

            for (int idx = 0; idx < limit; idx++) {
                int candidate = others.get(idx);
                nearest[sourceNode][idx] = candidate;

                candidateEdge[sourceNode][candidate] = true;
                candidateEdge[candidate][sourceNode] = true;
            }
        }

        return new CandidateLists(nearest, candidateEdge);
    }

    public int[] nearestOf(int node) {
        return nearest[node];
    }

    public boolean isCandidateEdge(int nodeA, int nodeB) {
        return candidateEdge[nodeA][nodeB];
    }
}