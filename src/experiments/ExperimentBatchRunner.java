package experiments;

import heuristics.RegretCycleHeuristic;
import heuristics.localsearch.IntraRouteNeighborhood;
import heuristics.localsearch.SearchStrategy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class ExperimentBatchRunner {

    public enum Method {
        RANDOM("Random") {
            @Override
            Experiment create(Path datasetPath, int startNode, Path savePath, Random rng, long timeLimitMs) {
                return new RandomExperiment(datasetPath, startNode, savePath, rng);
            }
        },
        NEAREST_NEIGHBOUR_DISTANCE("NN_Distance") {
            @Override
            Experiment create(Path datasetPath, int startNode, Path savePath, Random rng, long timeLimitMs) {
                return new NearestNeighbourExperiment(datasetPath, NearestNeighbourExperiment.Criterion.DISTANCE, NearestNeighbourExperiment.Mode.REDUCTION, startNode, savePath, rng);
            }
        },
        NEAREST_NEIGHBOUR_COST("NN_Cost") {
            @Override
            Experiment create(Path datasetPath, int startNode, Path savePath, Random rng, long timeLimitMs) {
                return new NearestNeighbourExperiment(datasetPath, NearestNeighbourExperiment.Criterion.COST, NearestNeighbourExperiment.Mode.REDUCTION, startNode, savePath, rng);
            }
        },
        GREEDY_CYCLE_DISTANCE("GreedyCycle_Distance") {
            @Override
            Experiment create(Path datasetPath, int startNode, Path savePath, Random rng, long timeLimitMs) {
                return new GreedyCycleExperiment(datasetPath, GreedyCycleExperiment.Mode.DISTANCE, startNode, savePath, rng);
            }
        },
        GREEDY_CYCLE_OBJECTIVE("GreedyCycle_Objective") {
            @Override
            Experiment create(Path datasetPath, int startNode, Path savePath, Random rng, long timeLimitMs) {
                return new GreedyCycleExperiment(datasetPath, GreedyCycleExperiment.Mode.OBJECTIVE, startNode, savePath, rng);
            }
        },
        REGRET_CYCLE("RegretCycle") {
            @Override
            Experiment create(Path datasetPath, int startNode, Path savePath, Random rng, long timeLimitMs) {
                return new RegretCycleExperiment(datasetPath, startNode, savePath, rng);
            }
            
        },
        WEIGHTED_REGRET_CYCLE("WeightedRegrestCycle"){
            @Override
            Experiment create(Path datasetPath, int startNode, Path savePath, Random rng, long timeLimitMs) {
                return new WeightedTwoRegretExperiment(datasetPath, startNode,10.0 ,savePath, rng);
            }
        },
        LS_STEEPEST_VERTEX_RANDOM("LS_Steepest_Vertex_Random") {
            @Override
            Experiment create(Path ds, int sn, Path sp, Random r, long timeLimitMs) {
                return new LocalSearchExperiment(ds, SearchStrategy.STEEPEST, IntraRouteNeighborhood.VERTEX_SWAP, null, sn, sp, r);
            }
        },
        LS_STEEPEST_VERTEX_HEURISTIC("LS_Steepest_Vertex_Heuristic") {
            @Override
            Experiment create(Path ds, int sn, Path sp, Random r, long timeLimitMs) {
                return new LocalSearchExperiment(ds, SearchStrategy.STEEPEST, IntraRouteNeighborhood.VERTEX_SWAP, new RegretCycleHeuristic(), sn, sp, r);
            }
        },
        LS_STEEPEST_EDGE_RANDOM("LS_Steepest_Edge_Random") {
            @Override
            Experiment create(Path ds, int sn, Path sp, Random r, long timeLimitMs) {
                return new LocalSearchExperiment(ds, SearchStrategy.STEEPEST, IntraRouteNeighborhood.EDGE_SWAP, null, sn, sp, r);
            }
        },
        LS_STEEPEST_EDGE_HEURISTIC("LS_Steepest_Edge_Heuristic") {
            @Override
            Experiment create(Path ds, int sn, Path sp, Random r, long timeLimitMs) {
                return new LocalSearchExperiment(ds, SearchStrategy.STEEPEST, IntraRouteNeighborhood.EDGE_SWAP, new RegretCycleHeuristic(), sn, sp, r);
            }
        },
        LS_GREEDY_VERTEX_RANDOM("LS_Greedy_Vertex_Random") {
            @Override
            Experiment create(Path ds, int sn, Path sp, Random r, long timeLimitMs) {
                return new LocalSearchExperiment(ds, SearchStrategy.GREEDY, IntraRouteNeighborhood.VERTEX_SWAP, null, sn, sp, r);
            }
        },
        LS_GREEDY_VERTEX_HEURISTIC("LS_Greedy_Vertex_Heuristic") {
            @Override
            Experiment create(Path ds, int sn, Path sp, Random r, long timeLimitMs) {
                return new LocalSearchExperiment(ds, SearchStrategy.GREEDY, IntraRouteNeighborhood.VERTEX_SWAP, new RegretCycleHeuristic(), sn, sp, r);
            }
        },
        LS_GREEDY_EDGE_RANDOM("LS_Greedy_Edge_Random") {
            @Override
            Experiment create(Path ds, int sn, Path sp, Random r, long timeLimitMs) {
                return new LocalSearchExperiment(ds, SearchStrategy.GREEDY, IntraRouteNeighborhood.EDGE_SWAP, null, sn, sp, r);
            }
        },
        LS_GREEDY_EDGE_HEURISTIC("LS_Greedy_Edge_Heuristic") {
            @Override
            Experiment create(Path ds, int sn, Path sp, Random r, long timeLimitMs) {
                return new LocalSearchExperiment(ds, SearchStrategy.GREEDY, IntraRouteNeighborhood.EDGE_SWAP, new RegretCycleHeuristic(), sn, sp, r);
            }
        },
        RANDOM_WALK("RandomWalk") {
            @Override
            Experiment create(Path ds, int sn, Path sp, Random r, long timeLimitMs) {
                return new RandomWalkExperiment(ds, timeLimitMs, sn, sp, r);
            }
        };
        

        private final String label;

        Method(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }

        abstract Experiment create(Path datasetPath, int startNode, Path savePath, Random rng, long timeLimitMs);
    }

    public record BatchArtifacts(Path detailsCsv,
                                 Path aggregateCsv,
                                 Path aggregateMarkdown,
                                 Path bestWorstCsv) {
    }

    private record RunDetail(String instanceName,
                             Method method,
                             int runIndex,
                             long seed,
                             ExperimentResult result,
                             Path tourFile) {
    }

    private record AggregateStats(String instanceName,
                                  Method method,
                                  int runs,
                                  double avgObjective,
                                  int minObjective,
                                  int maxObjective,
                                  double avgDistance,
                                  int minDistance,
                                  int maxDistance,
                                  double avgPhase1Distance,
                                  int minPhase1Distance,
                                  int maxPhase1Distance,
                                  double avgReward,
                                  int minReward,
                                  int maxReward,
                                  double avgNodeCount,
                                  int minNodeCount,
                                  int maxNodeCount,
                                  double avgTimeMs,
                                  long minTimeMs,
                                  long maxTimeMs) {
    }

    private final List<Path> datasetPaths;
    private final EnumSet<Method> methods;
    private final int runsPerCombination;
    private final int startNode;
    private final long baseSeed;
    private final Path outputDir;

    public ExperimentBatchRunner(List<Path> datasetPaths,
                                 EnumSet<Method> methods,
                                 int runsPerCombination,
                                 int startNode,
                                 long baseSeed,
                                 Path outputDir) {
        this.datasetPaths = datasetPaths;
        this.methods = methods;
        this.runsPerCombination = runsPerCombination;
        this.startNode = startNode;
        this.baseSeed = baseSeed;
        this.outputDir = outputDir;
    }

    public BatchArtifacts runAll() throws Exception {
        List<RunDetail> runDetails = runAllExperiments();
        List<AggregateStats> aggregates = aggregate(runDetails);

        Path detailsCsv = outputDir.resolve("details_runs.csv");
        Path aggregateCsv = outputDir.resolve("aggregates.csv");
        Path aggregateMarkdown = outputDir.resolve("aggregates.md");
        Path bestWorstCsv = outputDir.resolve("best_worst_runs.csv");

        writeDetailsCsv(detailsCsv, runDetails);
        writeAggregateCsv(aggregateCsv, aggregates);
        writeAggregateMarkdown(aggregateMarkdown, aggregates);
        writeBestWorstCsv(bestWorstCsv, runDetails);

        return new BatchArtifacts(detailsCsv, aggregateCsv, aggregateMarkdown, bestWorstCsv);
    }

    private List<RunDetail> runAllExperiments() throws Exception {
        Files.createDirectories(outputDir);
        List<RunDetail> details = new ArrayList<>();
        int combinationIndex = 0;

        for (Path datasetPath : datasetPaths) {
            String instanceName = datasetName(datasetPath);

            long maxLSAverageTimeMs = 0;

            for (Method method : methods) {
                if (method == Method.RANDOM_WALK) continue;

                long totalTimeForMethod = 0;
                for (int runIndex = 1; runIndex <= runsPerCombination; runIndex++) {
                    long seed = computeSeed(combinationIndex, runIndex);
                    Random rng = new Random(seed);
                    Path tourFile = outputDir.resolve("runs").resolve(method.name().toLowerCase(Locale.ROOT)).resolve(instanceName).resolve(String.format("run_%03d.txt", runIndex));

                    Experiment experiment = method.create(datasetPath, startNode, tourFile, rng, 0L);
                    ExperimentResult result = experiment.run();

                    totalTimeForMethod += result.timeMs();
                    details.add(new RunDetail(instanceName, method, runIndex, seed, result, tourFile));
                }

                if (method.name().startsWith("LS_")) {
                    long avgTime = totalTimeForMethod / runsPerCombination;
                    maxLSAverageTimeMs = Math.max(maxLSAverageTimeMs, avgTime);
                }
                combinationIndex++;
            }

            if (methods.contains(Method.RANDOM_WALK)) {
                if (maxLSAverageTimeMs == 0) maxLSAverageTimeMs = 1000L;

                for (int runIndex = 1; runIndex <= runsPerCombination; runIndex++) {
                    long seed = computeSeed(combinationIndex, runIndex);
                    Random rng = new Random(seed);
                    Path tourFile = outputDir.resolve("runs").resolve(Method.RANDOM_WALK.name().toLowerCase(Locale.ROOT)).resolve(instanceName).resolve(String.format("run_%03d.txt", runIndex));

                    Experiment experiment = Method.RANDOM_WALK.create(datasetPath, startNode, tourFile, rng, maxLSAverageTimeMs);
                    ExperimentResult result = experiment.run();
                    details.add(new RunDetail(instanceName, Method.RANDOM_WALK, runIndex, seed, result, tourFile));
                }
                combinationIndex++;
            }
        }
        return details;
    }

    private long computeSeed(int combinationIndex, int runIndex) {
        return baseSeed + (long) combinationIndex * 1_000_003L + runIndex;
    }

    private String datasetName(Path path) {
        String fileName = path.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    private List<AggregateStats> aggregate(List<RunDetail> runDetails) {
        Map<String, List<RunDetail>> grouped = new HashMap<>();

        for (RunDetail detail : runDetails) {
            String key = detail.instanceName + "|" + detail.method.name();
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(detail);
        }

        List<AggregateStats> aggregates = new ArrayList<>();

        for (List<RunDetail> group : grouped.values()) {
            RunDetail first = group.get(0);
            int runs = group.size();

            long sumTime = 0;
            long minTime = Long.MAX_VALUE;
            long maxTime = Long.MIN_VALUE;

            int sumObjective = 0;
            int minObjective = Integer.MAX_VALUE;
            int maxObjective = Integer.MIN_VALUE;

            int sumDistance = 0;
            int minDistance = Integer.MAX_VALUE;
            int maxDistance = Integer.MIN_VALUE;

            int sumPhase1Distance = 0;
            int minPhase1Distance = Integer.MAX_VALUE;
            int maxPhase1Distance = Integer.MIN_VALUE;

            int sumReward = 0;
            int minReward = Integer.MAX_VALUE;
            int maxReward = Integer.MIN_VALUE;

            int sumNodeCount = 0;
            int minNodeCount = Integer.MAX_VALUE;
            int maxNodeCount = Integer.MIN_VALUE;

            for (RunDetail detail : group) {
                ExperimentResult result = detail.result;

                int objective = result.objectiveValue();
                sumObjective += objective;
                minObjective = Math.min(minObjective, objective);
                maxObjective = Math.max(maxObjective, objective);

                int distance = result.totalDistance();
                sumDistance += distance;
                minDistance = Math.min(minDistance, distance);
                maxDistance = Math.max(maxDistance, distance);

                int phase1Distance = result.phase1Distance();
                sumPhase1Distance += phase1Distance;
                minPhase1Distance = Math.min(minPhase1Distance, phase1Distance);
                maxPhase1Distance = Math.max(maxPhase1Distance, phase1Distance);

                int reward = result.totalReward();
                sumReward += reward;
                minReward = Math.min(minReward, reward);
                maxReward = Math.max(maxReward, reward);

                int nodeCount = result.nodeCount();
                sumNodeCount += nodeCount;
                minNodeCount = Math.min(minNodeCount, nodeCount);
                maxNodeCount = Math.max(maxNodeCount, nodeCount);

                long time = result.timeMs();
                sumTime += time;
                minTime = Math.min(minTime, time);
                maxTime = Math.max(maxTime, time);
            }

            aggregates.add(new AggregateStats(
                    first.instanceName,
                    first.method,
                    runs,
                    (double) sumObjective / runs,
                    minObjective,
                    maxObjective,
                    (double) sumDistance / runs,
                    minDistance,
                    maxDistance,
                    (double) sumPhase1Distance / runs,
                    minPhase1Distance,
                    maxPhase1Distance,
                    (double) sumReward / runs,
                    minReward,
                    maxReward,
                    (double) sumNodeCount / runs,
                    minNodeCount,
                    maxNodeCount,
                    (double) sumTime / runs,
                    minTime,
                    maxTime
            ));
        }

        aggregates.sort(Comparator
                .comparing(AggregateStats::instanceName)
                .thenComparing(a -> a.method.name()));

        return aggregates;
    }

    private void writeDetailsCsv(Path path, List<RunDetail> runDetails) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>();
        lines.add("instance,method,run,seed,nodeCount,totalReward,totalDistance,phase1Distance,objective,tourFile,tour,timeMs");

        for (RunDetail detail : runDetails) {
            String tour = detail.result.tour().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining("->"));

            lines.add(String.join(",",
                    detail.instanceName,
                    detail.method.label(),
                    Integer.toString(detail.runIndex),
                    Long.toString(detail.seed),
                    Integer.toString(detail.result.nodeCount()),
                    Integer.toString(detail.result.totalReward()),
                    Integer.toString(detail.result.totalDistance()),
                    Integer.toString(detail.result.phase1Distance()),
                    Integer.toString(detail.result.objectiveValue()),
                    detail.tourFile.toString(),
                    "\"" + tour + "\"",
                    Long.toString(detail.result.timeMs())
            ));
        }

        Files.write(path, lines);
    }

    private void writeAggregateCsv(Path path, List<AggregateStats> aggregates) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>();
        lines.add("instance,method,runs,avgObjective,minObjective,maxObjective,avgDistance,minDistance,maxDistance,avgPhase1Distance,minPhase1Distance,maxPhase1Distance,avgReward,minReward,maxReward,avgNodeCount,minNodeCount,maxNodeCount,avgTimeMs,minTimeMs,maxTimeMs");

        for (AggregateStats stats : aggregates) {
            lines.add(String.join(",",
                    stats.instanceName,
                    stats.method.label(),
                    Integer.toString(stats.runs),
                    formatDouble(stats.avgObjective),
                    Integer.toString(stats.minObjective),
                    Integer.toString(stats.maxObjective),
                    formatDouble(stats.avgDistance),
                    Integer.toString(stats.minDistance),
                    Integer.toString(stats.maxDistance),
                    formatDouble(stats.avgPhase1Distance),
                    Integer.toString(stats.minPhase1Distance),
                    Integer.toString(stats.maxPhase1Distance),
                    formatDouble(stats.avgReward),
                    Integer.toString(stats.minReward),
                    Integer.toString(stats.maxReward),
                    formatDouble(stats.avgNodeCount),
                    Integer.toString(stats.minNodeCount),
                    Integer.toString(stats.maxNodeCount),
                    formatDouble(stats.avgTimeMs),
                    Long.toString(stats.minTimeMs),
                    Long.toString(stats.maxTimeMs)
            ));
        }

        Files.write(path, lines);
    }

    private void writeAggregateMarkdown(Path path, List<AggregateStats> aggregates) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> instanceNames = aggregates.stream()
                .map(AggregateStats::instanceName)
                .distinct()
                .sorted()
                .toList();

        List<String> lines = new ArrayList<>();
        lines.add("# Experiment aggregates");
        lines.add("");
        lines.add("## Objective value: average (min - max)");
        lines.add("");
        lines.add(buildMarkdownTable(instanceNames, aggregates, MetricType.OBJECTIVE));
        lines.add("");
        lines.add("## Path length (totalDistance): average (min - max)");
        lines.add("");
        lines.add(buildMarkdownTable(instanceNames, aggregates, MetricType.FINAL_DISTANCE));
        lines.add("");
        lines.add("## Path length after phase I (phase1Distance): average (min - max)");
        lines.add("");
        lines.add(buildMarkdownTable(instanceNames, aggregates, MetricType.PHASE1_DISTANCE));
        lines.add("");
        lines.add("## Time [ms]: average (min - max)");
        lines.add("");
        lines.add(buildMarkdownTable(instanceNames, aggregates, MetricType.TIME));
        lines.add("");
        lines.add("## Notes");
        lines.add("- `details_runs.csv` contains every single run and the complete tour.");
        lines.add("- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.");
        lines.add("- `best_worst_runs.csv` points to best and worst run per instance/method.");

        Files.write(path, lines);
    }

    private enum MetricType {
        OBJECTIVE,
        FINAL_DISTANCE,
        PHASE1_DISTANCE,
        TIME
    }

    private String buildMarkdownTable(List<String> instanceNames, List<AggregateStats> aggregates, MetricType metricType) {
        StringBuilder sb = new StringBuilder();

        sb.append("| Method |");
        for (String instanceName : instanceNames) {
            sb.append(' ').append(instanceName).append(" |");
        }
        sb.append('\n');

        sb.append("|---|");
        for (int i = 0; i < instanceNames.size(); i++) {
            sb.append("---|");
        }
        sb.append('\n');

        for (Method method : methods) {
            sb.append("| ").append(method.label()).append(" |");
            for (String instanceName : instanceNames) {
                AggregateStats stats = findAggregate(aggregates, instanceName, method);
                if (stats == null) {
                    sb.append(" - |");
                    continue;
                }

                String value;
                value = metricValue(stats, metricType);
                sb.append(' ').append(value).append(" |");
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    private AggregateStats findAggregate(List<AggregateStats> aggregates, String instanceName, Method method) {
        for (AggregateStats stats : aggregates) {
            if (stats.instanceName.equals(instanceName) && stats.method == method) {
                return stats;
            }
        }
        return null;
    }

    private String metricValue(AggregateStats stats, MetricType metricType) {
        return switch (metricType) {
            case OBJECTIVE -> formatDouble(stats.avgObjective) + " (" + stats.minObjective + " - " + stats.maxObjective + ")";
            case FINAL_DISTANCE -> formatDouble(stats.avgDistance) + " (" + stats.minDistance + " - " + stats.maxDistance + ")";
            case PHASE1_DISTANCE -> formatDouble(stats.avgPhase1Distance) + " (" + stats.minPhase1Distance + " - " + stats.maxPhase1Distance + ")";
            case TIME -> formatDouble(stats.avgTimeMs) + "ms (" + stats.minTimeMs + " - " + stats.maxTimeMs + ")";
        };
    }

    private void writeBestWorstCsv(Path path, List<RunDetail> runDetails) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Map<String, List<RunDetail>> grouped = new HashMap<>();
        for (RunDetail detail : runDetails) {
            String key = detail.instanceName + "|" + detail.method.name();
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(detail);
        }

        List<String> lines = new ArrayList<>();
        lines.add("instance,method,bestRun,bestObjective,bestTotalDistance,bestPhase1Distance,bestTourFile,worstRun,worstObjective,worstTotalDistance,worstPhase1Distance,worstTourFile");

        List<String> keys = new ArrayList<>(grouped.keySet());
        keys.sort(String::compareTo);

        for (String key : keys) {
            List<RunDetail> group = grouped.get(key);
            RunDetail best = group.stream().max(Comparator.comparingInt(r -> r.result.objectiveValue())).orElseThrow();
            RunDetail worst = group.stream().min(Comparator.comparingInt(r -> r.result.objectiveValue())).orElseThrow();

            lines.add(String.join(",",
                    best.instanceName,
                    best.method.label(),
                    Integer.toString(best.runIndex),
                    Integer.toString(best.result.objectiveValue()),
                    Integer.toString(best.result.totalDistance()),
                    Integer.toString(best.result.phase1Distance()),
                    best.tourFile.toString(),
                    Integer.toString(worst.runIndex),
                    Integer.toString(worst.result.objectiveValue()),
                    Integer.toString(worst.result.totalDistance()),
                    Integer.toString(worst.result.phase1Distance()),
                    worst.tourFile.toString()
            ));
        }

        Files.write(path, lines);
    }

    private String formatDouble(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}

