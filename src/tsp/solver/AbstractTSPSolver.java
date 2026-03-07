package tsp.solver;

import tsp.model.TSPInstance;
import tsp.model.TSPSolution;

import java.util.Random;

/**
 * Base class for TSP-with-profits solvers.
 * <p>
 * Provides a shared {@link Random} source and the {@link #getName()} contract.
 * Subclasses implement the actual solving logic in {@link #solve(TSPInstance)}.
 */
public abstract class AbstractTSPSolver implements TSPSolver {

    /** Shared random source — seed can be set for reproducibility. */
    protected final Random random;

    /**
     * Creates a solver with a new unseeded {@link Random}.
     */
    protected AbstractTSPSolver() {
        this.random = new Random();
    }

    /**
     * Creates a solver with a specific random seed (for reproducible results).
     *
     * @param seed random seed
     */
    protected AbstractTSPSolver(long seed) {
        this.random = new Random(seed);
    }
}
