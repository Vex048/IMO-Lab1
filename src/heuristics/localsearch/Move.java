package heuristics.localsearch;

import instance.Instance;
import java.util.List;

public interface Move {
    // Ewaluuje ruch (zwraca deltę: Zysk - Koszt)
    int evaluateDelta(Instance instance, List<Integer> tour);

    // Aplikuje ruch modyfikując stan roboczy
    void apply(List<Integer> tour, List<Integer> unvisited);
}

