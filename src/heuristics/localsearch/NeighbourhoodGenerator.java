package heuristics.localsearch;

import java.util.ArrayList;
import java.util.List;

public class NeighbourhoodGenerator {

    public static List<Move> generate(List<Integer> tour, List<Integer> unvisited, IntraRouteNeighborhood intraRouteType) {
        List<Move> neighbourhood = new ArrayList<>();
        int cycleSize = tour.size();

        // 1. Ruchy zmieniające zbiór wierzchołków
        for (int i = 0; i < cycleSize; i++) {
            for (int unvisitedNode : unvisited) {
                neighbourhood.add(new NodeExchangeMove(i, unvisitedNode));
            }
        }

        // 2. Ruchy wewnątrztrasowe (Vertex Swap lub Edge Swap)
        for (int i = 0; i < cycleSize - 1; i++) {
            for (int j = i + 1; j < cycleSize; j++) {
                if (intraRouteType == IntraRouteNeighborhood.VERTEX_SWAP) {
                    neighbourhood.add(new VertexSwapMove(i, j));
                } else if (intraRouteType == IntraRouteNeighborhood.EDGE_SWAP) {
                    neighbourhood.add(new EdgeSwapMove(i, j));
                }
            }
        }

        return neighbourhood;
    }
}