package heuristics;

import instance.Instance;
import java.util.Random;
import solution.Solution;

public class MslsHeuristic implements Heuristic, IterationCountProvider {
    private final Heuristic localSearch;
    private final int iterations;
    private int lastIterationCount;

    public MslsHeuristic(Heuristic localSearch, int iterations) {
        this.localSearch = localSearch;
        this.iterations = iterations;
    }

    @Override
    public Solution solve(Instance instance, int startNode, Random rng) {
        if (iterations <= 1) {
            lastIterationCount = 1;
            return localSearch.solve(instance, startNode, rng);
        }

        Solution best = null;
        lastIterationCount = 0;
        for (int i = 0; i < iterations; i++) {
            Solution candidate = localSearch.solve(instance, startNode, rng);
            lastIterationCount++;
            if (best == null || candidate.objectiveValue() > best.objectiveValue()) {
                best = candidate;
            }
        }

        return best;
    }

    @Override
    public int getIterationCount() {
        return lastIterationCount;
    }
}
