package solution;

import java.util.ArrayList;
import java.util.List;

public class Cycle {
    private final List<Integer> tour;

    public Cycle(List<Integer> tour) {
        this.tour = new ArrayList<>(tour);
    }

    public List<Integer> getTour() {
        return tour;
    }

    public int size() {
        return tour.size();
    }
}
