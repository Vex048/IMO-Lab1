package tsp.solver;

import tsp.model.TSPInstance;
import tsp.model.TSPSolution;

/**
 * Greedy Cycle solver (Metoda Rozbudowy Cyklu) — two-phase approach.
 *
 * <h3>Phase 1 — Construction (Cheapest Insertion to Hamiltonian cycle)</h3>
 * <ol>
 *   <li>Start from a random node.</li>
 *   <li>Find its nearest neighbour to form a 2-node cycle.</li>
 *   <li>Repeatedly pick the unvisited node &amp; insertion position using
 *       either the smallest distance increase (GCa) or the best objective
 *       delta (GCb, when {@code considerProfit} is true).</li>
 *   <li>Continue until every node is in the cycle (Hamiltonian cycle).</li>
 * </ol>
 *
 * <h3>Phase 2 — Improvement (Selective Removal)</h3>
 * <ol>
 *   <li>Evaluate the removal of each node using the objective-function delta
 *       (profit − distance).</li>
 *   <li>Remove the single node whose removal gives the greatest objective
 *       improvement.</li>
 *   <li>Repeat until no removal improves the objective.</li>
 * </ol>
 */
public class GreedyCycleSolver extends AbstractTSPSolver {

    private final boolean considerProfit;

    public GreedyCycleSolver(boolean considerProfit) {
        super();
        this.considerProfit = considerProfit;
    }

    public GreedyCycleSolver(long seed, boolean considerProfit) {
        super(seed);
        this.considerProfit = considerProfit;
    }

    @Override
    public String getName() {
        return considerProfit ? "Greedy Cycle (b)" : "Greedy Cycle (a)";
    }

    @Override
    public TSPSolution solve(TSPInstance instance) {
        TSPSolution solution = constructHamiltonianCycle(instance);
        improveByCycleRemoval(solution);
        return solution;
    }

    // ── Phase 1: Construction ──────────────────────────────────────────

    private TSPSolution constructHamiltonianCycle(TSPInstance instance) {
        int n = instance.size();
        boolean[] inCycle = new boolean[n];

        // 1. Random starting node
        int start = random.nextInt(n);

        // 2. Find nearest neighbour
        int nearest = -1;
        int nearestDist = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (i == start) continue;
            int d = instance.distance(start, i);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = i;
            }
        }

        // Build initial 2-node cycle
        TSPSolution solution = new TSPSolution(instance);
        solution.appendNode(start);
        solution.appendNode(nearest);
        solution.closeCycle();
        inCycle[start] = true;
        inCycle[nearest] = true;

        // 3. Repeatedly insert the best node
        for (int added = 2; added < n; added++) {
            int bestNode = -1;
            int bestPos = -1;

            if (considerProfit) {
                // GCb: maximise objective delta (profit − distance increase)
                int bestObjDelta = Integer.MIN_VALUE;
                for (int candidate = 0; candidate < n; candidate++) {
                    if (inCycle[candidate]) continue;
                    for (int pos = 0; pos < solution.size(); pos++) {
                        int delta = solution.insertionObjectiveDelta(pos, candidate);
                        if (delta > bestObjDelta) {
                            bestObjDelta = delta;
                            bestNode = candidate;
                            bestPos = pos;
                        }
                    }
                }
            } else {
                // GCa: minimise distance increase (ignore profit)
                int bestDistDelta = Integer.MAX_VALUE;
                for (int candidate = 0; candidate < n; candidate++) {
                    if (inCycle[candidate]) continue;
                    for (int pos = 0; pos < solution.size(); pos++) {
                        int delta = solution.insertionDistanceDelta(pos, candidate);
                        if (delta < bestDistDelta) {
                            bestDistDelta = delta;
                            bestNode = candidate;
                            bestPos = pos;
                        }
                    }
                }
            }

            solution.insertNode(bestPos, bestNode);
            inCycle[bestNode] = true;
        }

        return solution;
    }

    // ── Phase 2: Improvement by removal ────────────────────────────────

    private void improveByCycleRemoval(TSPSolution solution) {
        boolean improved = true;
        while (improved && solution.size() > 2) {
            improved = false;
            int bestPos = -1;
            int bestDelta = 0; // must be strictly positive to remove

            for (int pos = 0; pos < solution.size(); pos++) {
                int delta = solution.removalObjectiveDelta(pos);
                if (delta > bestDelta) {
                    bestDelta = delta;
                    bestPos = pos;
                }
            }

            if (bestPos >= 0) {
                solution.removeNode(bestPos);
                improved = true;
            }
        }
    }
}
