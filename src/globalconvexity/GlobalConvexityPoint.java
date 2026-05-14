package globalconvexity;

public record GlobalConvexityPoint(String instanceName,
                                   int index,
                                   long seed,
                                   int objectiveValue,
                                   int totalReward,
                                   int totalDistance,
                                   int nodeCount,
                                   int vertexSimilarityToBest,
                                   int edgeSimilarityToBest,
                                   double avgVertexSimilarityToOthers,
                                   double avgEdgeSimilarityToOthers,
                                   String tour) {
}

