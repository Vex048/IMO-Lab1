package globalconvexity;

import java.util.BitSet;

public final class SolutionSimilarity {
    private SolutionSimilarity() {
    }

    public static int commonVertices(SolutionSignature first, SolutionSignature second) {
        return intersectionSize(first.vertices(), second.vertices());
    }

    public static int commonEdges(SolutionSignature first, SolutionSignature second) {
        return intersectionSize(first.edges(), second.edges());
    }

    private static int intersectionSize(BitSet first, BitSet second) {
        BitSet intersection = (BitSet) first.clone();
        intersection.and(second);
        return intersection.cardinality();
    }
}

