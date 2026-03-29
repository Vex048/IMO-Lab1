import experiments.ExperimentBatchRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<Path> datasets = List.of(
                    Paths.get("datasets/TSPA.csv"),
                    Paths.get("datasets/TSPB.csv"));

            EnumSet<ExperimentBatchRunner.Method> methods = EnumSet.of(
                    ExperimentBatchRunner.Method.LS_STEEPEST_VERTEX_RANDOM,
                    ExperimentBatchRunner.Method.LS_STEEPEST_VERTEX_HEURISTIC,
                    ExperimentBatchRunner.Method.LS_STEEPEST_EDGE_RANDOM,
                    ExperimentBatchRunner.Method.LS_STEEPEST_EDGE_HEURISTIC,
                    ExperimentBatchRunner.Method.LS_GREEDY_VERTEX_RANDOM,
                    ExperimentBatchRunner.Method.LS_GREEDY_VERTEX_HEURISTIC,
                    ExperimentBatchRunner.Method.LS_GREEDY_EDGE_RANDOM,
                    ExperimentBatchRunner.Method.LS_GREEDY_EDGE_HEURISTIC,
                    ExperimentBatchRunner.Method.RANDOM_WALK);

            int runsPerCombination = 100;
            int startNode = -1;
            long baseSeed = 20260322L;
            Path outputDir = Paths.get("outputs");

            ExperimentBatchRunner runner = new ExperimentBatchRunner(
                    datasets,
                    methods,
                    runsPerCombination,
                    startNode,
                    baseSeed,
                    outputDir);

            ExperimentBatchRunner.BatchArtifacts artifacts = runner.runAll();

            System.out.println("=== Experiment batch finished ===");
            System.out.println("Runs per method/instance: " + runsPerCombination);
            System.out.println("Details CSV: " + artifacts.detailsCsv());
            System.out.println("Aggregate CSV: " + artifacts.aggregateCsv());
            System.out.println("Aggregate Markdown: " + artifacts.aggregateMarkdown());
            System.out.println("Best/Worst CSV: " + artifacts.bestWorstCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}