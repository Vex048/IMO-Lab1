package globalconvexity;

import heuristics.IlsHeuristic;
import heuristics.LocalSearchHeuristic;
import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.SearchStrategy;
import instance.Instance;
import instance.InstanceLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import solution.Solution;

public class GlobalConvexityRunner {
    public record Config(List<Path> datasetPaths,
                         Path outputDir,
                         int localOptimaCount,
                         long goodSolutionTimeLimitMs,
                         long baseSeed) {
    }

    public record Artifacts(Path detailsCsv,
                            Path correlationsCsv,
                            Path bestSolutionsCsv,
                            Path summaryMarkdown) {
    }

    private record LocalOptimum(Solution solution,
                                SolutionSignature signature,
                                long seed) {
    }

    private record BestSolution(String instanceName,
                                long seed,
                                long timeMs,
                                Solution solution) {
    }

    private record PointStats(int count,
                              double avgObjective,
                              int minObjective,
                              int maxObjective,
                              double avgNodeCount,
                              double avgVertexSimilarityToBest,
                              double avgEdgeSimilarityToBest,
                              double avgVertexSimilarityToOthers,
                              double avgEdgeSimilarityToOthers) {
    }

    private final Config config;

    public GlobalConvexityRunner(Config config) {
        this.config = config;
    }

    public Artifacts run() throws Exception {
        Files.createDirectories(config.outputDir());

        List<GlobalConvexityPoint> allPoints = new ArrayList<>();
        List<GlobalConvexityCorrelation> allCorrelations = new ArrayList<>();
        List<BestSolution> bestSolutions = new ArrayList<>();

        for (int datasetIndex = 0; datasetIndex < config.datasetPaths().size(); datasetIndex++) {
            Path datasetPath = config.datasetPaths().get(datasetIndex);
            String instanceName = datasetName(datasetPath);
            Instance instance = InstanceLoader.loadFromFile(datasetPath);

            System.out.printf("Generating %d greedy local optima for %s...%n",
                    config.localOptimaCount(),
                    instanceName);

            List<LocalOptimum> localOptima = generateLocalOptima(instance, datasetIndex);
            BestSolution bestSolution = generateBestSolution(instance, instanceName, datasetIndex);
            List<GlobalConvexityPoint> points = buildPoints(
                    instanceName,
                    localOptima,
                    bestSolution.solution(),
                    instance.size()
            );

            bestSolutions.add(bestSolution);
            allPoints.addAll(points);
            allCorrelations.addAll(buildCorrelations(instanceName, points));
        }

        Path detailsCsv = config.outputDir().resolve("details.csv");
        Path correlationsCsv = config.outputDir().resolve("correlations.csv");
        Path bestSolutionsCsv = config.outputDir().resolve("best_solutions.csv");
        Path summaryMarkdown = config.outputDir().resolve("summary.md");

        writeDetails(detailsCsv, allPoints);
        writeCorrelations(correlationsCsv, allCorrelations);
        writeBestSolutions(bestSolutionsCsv, bestSolutions);
        writeSummaryMarkdown(summaryMarkdown, allPoints, allCorrelations, bestSolutions);

        return new Artifacts(detailsCsv, correlationsCsv, bestSolutionsCsv, summaryMarkdown);
    }

    private List<LocalOptimum> generateLocalOptima(Instance instance, int datasetIndex) {
        List<LocalOptimum> localOptima = new ArrayList<>(config.localOptimaCount());

        for (int i = 0; i < config.localOptimaCount(); i++) {
            long seed = localSeed(datasetIndex, i);
            Random rng = new Random(seed);
            LocalSearchHeuristic heuristic = new LocalSearchHeuristic(
                    SearchStrategy.GREEDY,
                    IntraRouteNeighborhood.EDGE_SWAP,
                    null
            );
            Solution solution = heuristic.solve(instance, -1, rng);
            SolutionSignature signature = SolutionSignature.from(solution, instance.size());
            localOptima.add(new LocalOptimum(solution, signature, seed));

            if ((i + 1) % 100 == 0) {
                System.out.printf("  generated %d/%d%n", i + 1, config.localOptimaCount());
            }
        }

        return localOptima;
    }

    private BestSolution generateBestSolution(Instance instance, String instanceName, int datasetIndex) {
        long seed = bestSeed(datasetIndex);
        Random rng = new Random(seed);
        IlsHeuristic heuristic = new IlsHeuristic(
                config.goodSolutionTimeLimitMs(),
                5,
                IntraRouteNeighborhood.EDGE_SWAP
        );

        long start = System.currentTimeMillis();
        Solution solution = heuristic.solve(instance, -1, rng);
        long timeMs = System.currentTimeMillis() - start;

        System.out.printf("Best %s solution by ILS: objective=%d time=%dms iterations=%d%n",
                instanceName,
                solution.objectiveValue(),
                timeMs,
                heuristic.getIterationCount());

        return new BestSolution(instanceName, seed, timeMs, solution);
    }

    private List<GlobalConvexityPoint> buildPoints(String instanceName,
                                                   List<LocalOptimum> localOptima,
                                                   Solution bestSolution,
                                                   int nodeCount) {
        SolutionSignature bestSignature = SolutionSignature.from(bestSolution, nodeCount);
        int count = localOptima.size();
        double[] vertexSums = new double[count];
        double[] edgeSums = new double[count];

        for (int i = 0; i < count; i++) {
            SolutionSignature first = localOptima.get(i).signature();
            for (int j = i + 1; j < count; j++) {
                SolutionSignature second = localOptima.get(j).signature();
                int commonVertices = SolutionSimilarity.commonVertices(first, second);
                int commonEdges = SolutionSimilarity.commonEdges(first, second);
                vertexSums[i] += commonVertices;
                vertexSums[j] += commonVertices;
                edgeSums[i] += commonEdges;
                edgeSums[j] += commonEdges;
            }
        }

        List<GlobalConvexityPoint> points = new ArrayList<>(count);
        double divisor = count - 1.0;
        for (int i = 0; i < count; i++) {
            LocalOptimum localOptimum = localOptima.get(i);
            Solution solution = localOptimum.solution();

            points.add(new GlobalConvexityPoint(
                    instanceName,
                    i + 1,
                    localOptimum.seed(),
                    solution.objectiveValue(),
                    solution.getTotalReward(),
                    solution.getTotalDistance(),
                    solution.getCycle().size(),
                    SolutionSimilarity.commonVertices(localOptimum.signature(), bestSignature),
                    SolutionSimilarity.commonEdges(localOptimum.signature(), bestSignature),
                    vertexSums[i] / divisor,
                    edgeSums[i] / divisor,
                    tourString(solution)
            ));
        }

        return points;
    }

    private List<GlobalConvexityCorrelation> buildCorrelations(String instanceName,
                                                               List<GlobalConvexityPoint> points) {
        double[] objectives = new double[points.size()];
        double[] verticesToBest = new double[points.size()];
        double[] edgesToBest = new double[points.size()];
        double[] avgVerticesToOthers = new double[points.size()];
        double[] avgEdgesToOthers = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            GlobalConvexityPoint point = points.get(i);
            objectives[i] = point.objectiveValue();
            verticesToBest[i] = point.vertexSimilarityToBest();
            edgesToBest[i] = point.edgeSimilarityToBest();
            avgVerticesToOthers[i] = point.avgVertexSimilarityToOthers();
            avgEdgesToOthers[i] = point.avgEdgeSimilarityToOthers();
        }

        return List.of(
                new GlobalConvexityCorrelation(instanceName, "best_solution", "vertices",
                        PearsonCorrelation.calculate(objectives, verticesToBest), points.size()),
                new GlobalConvexityCorrelation(instanceName, "best_solution", "edges",
                        PearsonCorrelation.calculate(objectives, edgesToBest), points.size()),
                new GlobalConvexityCorrelation(instanceName, "local_optima_average", "vertices",
                        PearsonCorrelation.calculate(objectives, avgVerticesToOthers), points.size()),
                new GlobalConvexityCorrelation(instanceName, "local_optima_average", "edges",
                        PearsonCorrelation.calculate(objectives, avgEdgesToOthers), points.size())
        );
    }

    private void writeDetails(Path path, List<GlobalConvexityPoint> points) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("instance,index,seed,objective,totalReward,totalDistance,nodeCount,vertexSimilarityToBest,edgeSimilarityToBest,avgVertexSimilarityToOthers,avgEdgeSimilarityToOthers,tour");

        for (GlobalConvexityPoint point : points) {
            lines.add(String.join(",",
                    point.instanceName(),
                    Integer.toString(point.index()),
                    Long.toString(point.seed()),
                    Integer.toString(point.objectiveValue()),
                    Integer.toString(point.totalReward()),
                    Integer.toString(point.totalDistance()),
                    Integer.toString(point.nodeCount()),
                    Integer.toString(point.vertexSimilarityToBest()),
                    Integer.toString(point.edgeSimilarityToBest()),
                    formatDouble(point.avgVertexSimilarityToOthers()),
                    formatDouble(point.avgEdgeSimilarityToOthers()),
                    quote(point.tour())
            ));
        }

        Files.write(path, lines);
    }

    private void writeCorrelations(Path path, List<GlobalConvexityCorrelation> correlations) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("instance,similarityScope,similarityMeasure,pearsonCorrelation,points");

        for (GlobalConvexityCorrelation correlation : correlations) {
            lines.add(String.join(",",
                    correlation.instanceName(),
                    correlation.similarityScope(),
                    correlation.similarityMeasure(),
                    formatDouble(correlation.pearsonCorrelation()),
                    Integer.toString(correlation.points())
            ));
        }

        Files.write(path, lines);
    }

    private void writeBestSolutions(Path path, List<BestSolution> bestSolutions) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("instance,seed,timeMs,objective,totalReward,totalDistance,nodeCount,tour");

        for (BestSolution bestSolution : bestSolutions) {
            Solution solution = bestSolution.solution();
            lines.add(String.join(",",
                    bestSolution.instanceName(),
                    Long.toString(bestSolution.seed()),
                    Long.toString(bestSolution.timeMs()),
                    Integer.toString(solution.objectiveValue()),
                    Integer.toString(solution.getTotalReward()),
                    Integer.toString(solution.getTotalDistance()),
                    Integer.toString(solution.getCycle().size()),
                    quote(tourString(solution))
            ));
        }

        Files.write(path, lines);
    }

    private void writeSummaryMarkdown(Path path,
                                      List<GlobalConvexityPoint> points,
                                      List<GlobalConvexityCorrelation> correlations,
                                      List<BestSolution> bestSolutions) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# Global convexity results");
        lines.add("");
        lines.add("Generated local optima per instance: " + config.localOptimaCount());
        lines.add("Local optima generation: randomized starting solution followed by greedy local search with EDGE_SWAP neighborhood.");
        lines.add("Good solution heuristic: ILS, time limit " + config.goodSolutionTimeLimitMs() + " ms.");
        lines.add("");

        lines.add("## Good ILS solutions");
        lines.add("");
        lines.add("| Instance | Seed | Time [ms] | Objective | Reward | Distance | Nodes |");
        lines.add("|---|---:|---:|---:|---:|---:|---:|");
        for (BestSolution bestSolution : bestSolutions) {
            Solution solution = bestSolution.solution();
            lines.add(String.format(Locale.US,
                    "| %s | %d | %d | %d | %d | %d | %d |",
                    bestSolution.instanceName(),
                    bestSolution.seed(),
                    bestSolution.timeMs(),
                    solution.objectiveValue(),
                    solution.getTotalReward(),
                    solution.getTotalDistance(),
                    solution.getCycle().size()
            ));
        }
        lines.add("");

        lines.add("## Local optima summary");
        lines.add("");
        lines.add("| Instance | Points | Avg objective | Min objective | Max objective | Avg nodes | Avg vertices to best | Avg edges to best | Avg vertices to others | Avg edges to others |");
        lines.add("|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|");
        for (BestSolution bestSolution : bestSolutions) {
            PointStats stats = statsFor(bestSolution.instanceName(), points);
            lines.add(String.format(Locale.US,
                    "| %s | %d | %s | %d | %d | %s | %s | %s | %s | %s |",
                    bestSolution.instanceName(),
                    stats.count(),
                    formatDouble(stats.avgObjective()),
                    stats.minObjective(),
                    stats.maxObjective(),
                    formatDouble(stats.avgNodeCount()),
                    formatDouble(stats.avgVertexSimilarityToBest()),
                    formatDouble(stats.avgEdgeSimilarityToBest()),
                    formatDouble(stats.avgVertexSimilarityToOthers()),
                    formatDouble(stats.avgEdgeSimilarityToOthers())
            ));
        }
        lines.add("");

        lines.add("## Pearson correlations");
        lines.add("");
        lines.add("| Instance | Compared with | Measure | Pearson r | Points |");
        lines.add("|---|---|---|---:|---:|");
        for (GlobalConvexityCorrelation correlation : correlations) {
            lines.add(String.format(Locale.US,
                    "| %s | %s | %s | %s | %d |",
                    correlation.instanceName(),
                    scopeLabel(correlation.similarityScope()),
                    measureLabel(correlation.similarityMeasure()),
                    formatDouble(correlation.pearsonCorrelation()),
                    correlation.points()
            ));
        }
        lines.add("");

        lines.add("The best ILS solution is not included as a plotted point and is not included in the correlation sample.");
        lines.add("Plot x-axis: objective value of a greedy local optimum. Plot y-axis: selected similarity measure.");

        Files.write(path, lines);
    }

    private PointStats statsFor(String instanceName, List<GlobalConvexityPoint> points) {
        int count = 0;
        int minObjective = Integer.MAX_VALUE;
        int maxObjective = Integer.MIN_VALUE;
        double objectiveSum = 0.0;
        double nodeCountSum = 0.0;
        double verticesToBestSum = 0.0;
        double edgesToBestSum = 0.0;
        double verticesToOthersSum = 0.0;
        double edgesToOthersSum = 0.0;

        for (GlobalConvexityPoint point : points) {
            if (!point.instanceName().equals(instanceName)) {
                continue;
            }

            count++;
            minObjective = Math.min(minObjective, point.objectiveValue());
            maxObjective = Math.max(maxObjective, point.objectiveValue());
            objectiveSum += point.objectiveValue();
            nodeCountSum += point.nodeCount();
            verticesToBestSum += point.vertexSimilarityToBest();
            edgesToBestSum += point.edgeSimilarityToBest();
            verticesToOthersSum += point.avgVertexSimilarityToOthers();
            edgesToOthersSum += point.avgEdgeSimilarityToOthers();
        }

        if (count == 0) {
            return new PointStats(0, Double.NaN, 0, 0, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
        }

        return new PointStats(
                count,
                objectiveSum / count,
                minObjective,
                maxObjective,
                nodeCountSum / count,
                verticesToBestSum / count,
                edgesToBestSum / count,
                verticesToOthersSum / count,
                edgesToOthersSum / count
        );
    }

    private String scopeLabel(String scope) {
        return switch (scope) {
            case "best_solution" -> "good ILS solution";
            case "local_optima_average" -> "average local optimum";
            default -> scope;
        };
    }

    private String measureLabel(String measure) {
        return switch (measure) {
            case "vertices" -> "common vertices";
            case "edges" -> "common edges";
            default -> measure;
        };
    }

    private long localSeed(int datasetIndex, int index) {
        return config.baseSeed() + (long) datasetIndex * 1_000_003L + index + 1L;
    }

    private long bestSeed(int datasetIndex) {
        return config.baseSeed() + (long) datasetIndex * 1_000_003L + 900_001L;
    }

    private String datasetName(Path path) {
        String fileName = path.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    private String tourString(Solution solution) {
        return solution.getCycle().getTour().stream()
                .map(String::valueOf)
                .collect(Collectors.joining("->"));
    }

    private String formatDouble(double value) {
        if (Double.isNaN(value)) {
            return "NaN";
        }
        return String.format(Locale.US, "%.6f", value);
    }

    private String quote(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}

