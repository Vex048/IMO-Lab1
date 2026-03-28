package heuristics.localsearch;

import instance.Instance;
import solution.CycleDeltas;
import java.util.Collections;
import java.util.List;

public class EdgeSwapMove implements Move {
    private final int index1;
    private final int index2;

    public EdgeSwapMove(int index1, int index2) {
        this.index1 = index1;
        this.index2 = index2;
    }

    @Override
    public int evaluateDelta(Instance instance, List<Integer> tour) {
        return CycleDeltas.edgeSwapObjectiveDelta(instance, tour, index1, index2);
    }

    @Override
    public void apply(List<Integer> tour, List<Integer> unvisited) {
        int start = Math.min(index1, index2);
        int end = Math.max(index1, index2);

        // Klasyczny 2-opt: odwrócenie ścieżki między indeksami
        while (start < end) {
            Collections.swap(tour, start, end);
            start++;
            end--;
        }
    }
}