package globalconvexity;

public record GlobalConvexityCorrelation(String instanceName,
                                         String similarityScope,
                                         String similarityMeasure,
                                         double pearsonCorrelation,
                                         int points) {
}

