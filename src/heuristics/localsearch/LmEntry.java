package heuristics.localsearch;

public final class LmEntry {
    public enum Kind {
        NODE_EXCHANGE,
        EDGE_SWAP
    }

    private final Kind kind;
    private final Move move;
    private final int cachedDelta;

    private final int tourIndex;
    private final int insertedNode;
    private final int expectedNode;
    private final int expectedPrev;
    private final int expectedNext;

    private final int edge1From;
    private final int edge1To;
    private final int edge2From;
    private final int edge2To;

    private LmEntry(Kind kind,
                    Move move,
                    int cachedDelta,
                    int tourIndex,
                    int insertedNode,
                    int expectedNode,
                    int expectedPrev,
                    int expectedNext,
                    int edge1From,
                    int edge1To,
                    int edge2From,
                    int edge2To) {
        this.kind = kind;
        this.move = move;
        this.cachedDelta = cachedDelta;
        this.tourIndex = tourIndex;
        this.insertedNode = insertedNode;
        this.expectedNode = expectedNode;
        this.expectedPrev = expectedPrev;
        this.expectedNext = expectedNext;
        this.edge1From = edge1From;
        this.edge1To = edge1To;
        this.edge2From = edge2From;
        this.edge2To = edge2To;
    }

    public static LmEntry forNodeExchange(Move move,
                                          int cachedDelta,
                                          int tourIndex,
                                          int insertedNode,
                                          int expectedNode,
                                          int expectedPrev,
                                          int expectedNext) {
        return new LmEntry(
                Kind.NODE_EXCHANGE,
                move,
                cachedDelta,
                tourIndex,
                insertedNode,
                expectedNode,
                expectedPrev,
                expectedNext,
                -1,
                -1,
                -1,
                -1
        );
    }

    public static LmEntry forEdgeSwap(Move move,
                                      int cachedDelta,
                                      int edge1From,
                                      int edge1To,
                                      int edge2From,
                                      int edge2To) {
        return new LmEntry(
                Kind.EDGE_SWAP,
                move,
                cachedDelta,
                -1,
                -1,
                -1,
                -1,
                -1,
                edge1From,
                edge1To,
                edge2From,
                edge2To
        );
    }

    public Kind kind() {
        return kind;
    }

    public Move move() {
        return move;
    }

    public int cachedDelta() {
        return cachedDelta;
    }

    public int tourIndex() {
        return tourIndex;
    }

    public int insertedNode() {
        return insertedNode;
    }

    public int expectedNode() {
        return expectedNode;
    }

    public int expectedPrev() {
        return expectedPrev;
    }

    public int expectedNext() {
        return expectedNext;
    }

    public int edge1From() {
        return edge1From;
    }

    public int edge1To() {
        return edge1To;
    }

    public int edge2From() {
        return edge2From;
    }

    public int edge2To() {
        return edge2To;
    }
}