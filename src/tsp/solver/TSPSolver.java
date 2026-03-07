package tsp.solver;

import tsp.model.TSPInstance;
import tsp.model.TSPSolution;

/**
 * Contract for all TSP-with-profits solvers.
 * <p>
 * Every solver must accept a {@link TSPInstance} and return a
 * {@link TSPSolution} — a cycle over a subset (or all) of the nodes
 * that maximises {@code totalProfit − totalDistance}.
 */
public interface TSPSolver {

    /**
     * Returns a human-readable name identifying this solver.
     */
    String getName();

    /**
     * Solves the given instance and returns the best solution found.
     *
     * @param instance the problem instance (coordinates are NOT to be used —
     *                 only the distance matrix)
     * @return a valid {@link TSPSolution}
     */
    TSPSolution solve(TSPInstance instance);
}
