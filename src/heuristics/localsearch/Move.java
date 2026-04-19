package heuristics.localsearch;

import instance.Instance;
import java.util.List;

public interface Move {
    int evaluateDelta(Instance instance, List<Integer> tour);

    void apply(List<Integer> tour, List<Integer> unvisited);

    default Applicability applicability(List<Integer> tour, List<Integer> unvisited) {
        return Applicability.APPLICABLE;
    }

    enum Applicability {
        APPLICABLE,
        KEEP_FOR_LATER,
        REMOVE
    }
}

