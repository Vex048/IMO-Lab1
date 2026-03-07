package tsp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mutable representation of a TSP-with-profits solution.
 * <p>
 * The solution is a <em>cycle</em> over a subset of nodes.  The cycle is stored
 * as an ordered list where an implicit edge connects the last element back to
 * the first.  All distance queries go through the {@link TSPInstance} distance
 * matrix — coordinates are never used.
 * <p>
 * This class provides efficient <b>delta-evaluation</b> helpers so that
 * solvers never need to recompute the objective from scratch.
 */
public class TSPSolution {

    private final TSPInstance instance;
    /** Ordered list of node indices forming the cycle. */
    private final List<Integer> cycle;
    /** Fast membership lookup. */
    private final Set<Integer> inCycle;
    /** Running total of route length. */
    private int totalDistance;
    /** Running total of collected profit. */
    private int totalProfit;

    /**
     * Creates an empty solution for the given instance.
     */
    public TSPSolution(TSPInstance instance) {
        this.instance = instance;
        this.cycle = new ArrayList<>();
        this.inCycle = new HashSet<>();
        this.totalDistance = 0;
        this.totalProfit = 0;
    }

    /**
     * Copy constructor — creates an independent deep copy.
     */
    public TSPSolution(TSPSolution other) {
        this.instance = other.instance;
        this.cycle = new ArrayList<>(other.cycle);
        this.inCycle = new HashSet<>(other.inCycle);
        this.totalDistance = other.totalDistance;
        this.totalProfit = other.totalProfit;
    }

    // ── Accessors ──────────────────────────────────────────────────────

    public TSPInstance getInstance()    { return instance; }
    public List<Integer> getCycle()     { return Collections.unmodifiableList(cycle); }
    public int getTotalDistance()       { return totalDistance; }
    public int getTotalProfit()         { return totalProfit; }
    public int getObjective()           { return totalProfit - totalDistance; }
    public int size()                   { return cycle.size(); }
    public boolean contains(int node)   { return inCycle.contains(node); }

    // ── Cycle construction (used during initial setup) ─────────────────

    /**
     * Adds a node to the end of the cycle (before closing the loop).
     * Updates running totals.  Should only be used during initial
     * construction or when building a cycle from scratch.
     */
    public void appendNode(int node) {
        if (!cycle.isEmpty()) {
            int last = cycle.get(cycle.size() - 1);
            // We will recalculate the closing edge when needed
            totalDistance += instance.distance(last, node);
        }
        cycle.add(node);
        inCycle.add(node);
        totalProfit += instance.profit(node);
    }

    /**
     * Call once after all nodes have been appended to close the cycle
     * (add the edge from the last node back to the first).
     */
    public void closeCycle() {
        if (cycle.size() >= 2) {
            totalDistance += instance.distance(cycle.get(cycle.size() - 1), cycle.get(0));
        }
    }

    /**
     * Rebuilds running totals from the current cycle contents.
     * Useful after bulk modifications.
     */
    public void recalculate() {
        totalProfit = 0;
        totalDistance = 0;
        for (int node : cycle) {
            totalProfit += instance.profit(node);
        }
        for (int i = 0; i < cycle.size(); i++) {
            int next = (i + 1) % cycle.size();
            totalDistance += instance.distance(cycle.get(i), cycle.get(next));
        }
    }

    // ── Delta evaluation helpers ───────────────────────────────────────

    /**
     * Computes the <b>change in route length</b> (distance delta) when
     * inserting {@code node} between positions {@code posA} and the next
     * position in the cycle.
     * <p>
     * Delta = d(a, node) + d(node, b) − d(a, b)
     *
     * @param positionIndex index in the cycle of the node <em>before</em> the insertion point
     * @param node          the node to insert
     * @return the distance increase (positive means the route gets longer)
     */
    public int insertionDistanceDelta(int positionIndex, int node) {
        int a = cycle.get(positionIndex);
        int b = cycle.get((positionIndex + 1) % cycle.size());
        return instance.distance(a, node)
             + instance.distance(node, b)
             - instance.distance(a, b);
    }

    /**
     * Computes the <b>change in the objective function</b> when inserting
     * {@code node} between positions {@code positionIndex} and the next one.
     * <p>
     * Objective delta = profit(node) − insertionDistanceDelta
     *
     * @param positionIndex index of the predecessor in the cycle
     * @param node          the node to insert
     * @return positive value means the objective improves
     */
    public int insertionObjectiveDelta(int positionIndex, int node) {
        return instance.profit(node) - insertionDistanceDelta(positionIndex, node);
    }

    /**
     * Inserts {@code node} into the cycle right after {@code positionIndex}
     * and updates running totals using the pre-computed distance delta.
     *
     * @param positionIndex index of the predecessor
     * @param node          the node to insert
     */
    public void insertNode(int positionIndex, int node) {
        int distDelta = insertionDistanceDelta(positionIndex, node);
        cycle.add(positionIndex + 1, node);
        inCycle.add(node);
        totalDistance += distDelta;
        totalProfit += instance.profit(node);
    }

    /**
     * Computes the <b>change in route length</b> when removing the node
     * at position {@code positionIndex} from the cycle.
     * <p>
     * Delta = d(prev, next) − d(prev, node) − d(node, next)
     * (negative means the route gets shorter)
     *
     * @param positionIndex index of the node to remove
     * @return the distance change (negative = route shrinks)
     */
    public int removalDistanceDelta(int positionIndex) {
        int prev = cycle.get((positionIndex - 1 + cycle.size()) % cycle.size());
        int node = cycle.get(positionIndex);
        int next = cycle.get((positionIndex + 1) % cycle.size());
        return instance.distance(prev, next)
             - instance.distance(prev, node)
             - instance.distance(node, next);
    }

    /**
     * Computes the <b>change in the objective function</b> when removing
     * the node at position {@code positionIndex}.
     * <p>
     * Objective delta = −profit(node) − removalDistanceDelta
     * <p>
     * A positive result means removal <em>improves</em> the objective.
     *
     * @param positionIndex index of the node to remove
     * @return positive value means the objective improves upon removal
     */
    public int removalObjectiveDelta(int positionIndex) {
        int node = cycle.get(positionIndex);
        // Removing the node: we lose its profit, and the distance changes
        // objectiveDelta = (−profit) − distanceDelta
        // Since distanceDelta is negative when route shrinks:
        //   objectiveDelta = −profit − (negative) = −profit + |shrinkage|
        return -instance.profit(node) - removalDistanceDelta(positionIndex);
    }

    /**
     * Removes the node at position {@code positionIndex} from the cycle
     * and updates running totals using the pre-computed distance delta.
     *
     * @param positionIndex index of the node to remove
     */
    public void removeNode(int positionIndex) {
        int distDelta = removalDistanceDelta(positionIndex);
        int node = cycle.get(positionIndex);
        cycle.remove(positionIndex);
        inCycle.remove(node);
        totalDistance += distDelta;
        totalProfit -= instance.profit(node);
    }

    @Override
    public String toString() {
        return String.format("Solution[nodes=%d, distance=%d, profit=%d, objective=%d]",
                cycle.size(), totalDistance, totalProfit, getObjective());
    }
}
