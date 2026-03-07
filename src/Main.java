import tsp.io.InstanceLoader;
import tsp.model.TSPInstance;
import tsp.model.TSPSolution;
import tsp.solver.GreedyCycleSolver;
import tsp.solver.RandomSolver;
import tsp.solver.TSPSolver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.LongFunction;

/**
 * Entry point — loads every dataset from {@code datasets/}, runs all
 * registered solvers 200 times each, and prints aggregated statistics.
 */
public class Main {

    private static final String[] DATASETS = {"TSPA.csv", "TSPB.csv"};
    private static final int RUNS = 200;
    private static final long BASE_SEED = 42L;
    private static final Path RESULTS_DIR = Path.of("results");

    /**
     * Each entry is a factory: given a seed, it produces a fresh solver.
     * This way every run gets its own Random state.
     */
    private static final LongFunction<TSPSolver>[] SOLVER_FACTORIES = new LongFunction[]{
            (LongFunction<TSPSolver>) seed -> new RandomSolver(seed),
            (LongFunction<TSPSolver>) seed -> new GreedyCycleSolver(seed, false),
            (LongFunction<TSPSolver>) seed -> new GreedyCycleSolver(seed, true),
    };

    public static void main(String[] args) throws IOException {
        Files.createDirectories(RESULTS_DIR);

        for (String datasetFile : DATASETS) {
            Path path = Path.of("datasets", datasetFile);
            TSPInstance instance = InstanceLoader.load(path);

            System.out.println("=".repeat(72));
            System.out.printf("Instance: %s  (%d nodes, %d runs per solver)%n",
                    instance.name(), instance.size(), RUNS);
            System.out.println("=".repeat(72));

            for (LongFunction<TSPSolver> factory : SOLVER_FACTORIES) {
                runExperiment(instance, factory);
            }
            System.out.println();
        }
    }

    private static void runExperiment(TSPInstance instance, LongFunction<TSPSolver> factory)
            throws IOException {
        int minObj = Integer.MAX_VALUE;
        int maxObj = Integer.MIN_VALUE;
        long sumObj = 0;

        String solverName = null;
        TSPSolution bestSolution = null;

        String safeName = null;
        Path file = null;
        BufferedWriter writer = null;

        try {
            for (int run = 0; run < RUNS; run++) {
                long seed = BASE_SEED + run;
                TSPSolver solver = factory.apply(seed);
                if (solverName == null) {
                    solverName = solver.getName();
                    safeName = solverName.replaceAll("[^a-zA-Z0-9_()-]", "_");
                    file = RESULTS_DIR.resolve(instance.name() + "_" + safeName + ".csv");
                    writer = Files.newBufferedWriter(file);
                }

                TSPSolution solution = solver.solve(instance);
                int obj = solution.getObjective();

                sumObj += obj;
                if (obj < minObj) minObj = obj;
                if (obj > maxObj) {
                    maxObj = obj;
                    bestSolution = solution;
                }

                // Write this run: objective;node1;node2;...;nodeN
                List<Integer> cycle = solution.getCycle();
                StringBuilder sb = new StringBuilder();
                sb.append(obj);
                for (int node : cycle) {
                    sb.append(';').append(node);
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } finally {
            if (writer != null) writer.close();
        }

        double avgObj = (double) sumObj / RUNS;
        System.out.printf("  %-20s | min: %7d | avg: %10.1f | max: %7d | best nodes: %3d%n",
                solverName, minObj, avgObj, maxObj, bestSolution.size());
        System.out.printf("    -> all %d solutions saved to %s%n", RUNS, file);
    }
}
