package tsp.solver;

import tsp.model.TSPInstance;
import tsp.model.TSPSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Baseline random solver.
 * <ol>
 *   <li>Uniformly pick a random number of nodes to visit (1 … n).</li>
 *   <li>Randomly select that many nodes.</li>
 *   <li>Connect them in the random order to form a cycle.</li>
 * </ol>
 */
public class RandomSolver extends AbstractTSPSolver {

    public RandomSolver() { super(); }

    public RandomSolver(long seed) { super(seed); }

    @Override
    public String getName() {
        return "Random";
    }

    @Override
    public TSPSolution solve(TSPInstance instance) {
        int n = instance.size();

        // Random count between 1 and n (inclusive)
        int count = 1 + random.nextInt(n);

        // Shuffle all indices and pick the first 'count'
        List<Integer> indices = IntStream.range(0, n)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(indices, random);
        List<Integer> selected = indices.subList(0, count);

        // Build cycle in random (shuffled) order
        TSPSolution solution = new TSPSolution(instance);
        for (int node : selected) {
            solution.appendNode(node);
        }
        solution.closeCycle();
        return solution;
    }
}
