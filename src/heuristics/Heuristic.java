package heuristics;

import instance.Instance;
import solution.Solution;
import java.util.Random;

public interface Heuristic {
    Solution solve(Instance instance, int startNode, Random rng);
}
