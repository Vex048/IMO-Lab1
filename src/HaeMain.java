import heuristics.HaeHeuristic;
import heuristics.HaeRecombinationOperator;
import heuristics.localsearch.IntraRouteNeighborhood;
import instance.Instance;
import instance.InstanceLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import solution.Solution;

public class HaeMain {
    public static void main(String[] args) throws Exception {
        Path dataset = args.length > 0 ? Paths.get(args[0]) : Paths.get("datasets/TSPA.csv");
        long timeLimitMs = args.length > 1 ? Long.parseLong(args[1]) : 1000L;
        int maxIterations = args.length > 2 ? Integer.parseInt(args[2]) : 2000;

        Instance instance = InstanceLoader.loadFromFile(dataset);
        Random rng = new Random(20260517L);

        HaeHeuristic hae = new HaeHeuristic(
                timeLimitMs,
                maxIterations,
                20,
                HaeRecombinationOperator.OP1_COMMON_EDGES_AND_VERTICES,
                true,
                IntraRouteNeighborhood.EDGE_SWAP
        );

        long start = System.currentTimeMillis();
        Solution solution = hae.solve(instance, -1, rng);
        long end = System.currentTimeMillis();

        System.out.println("=== HAE quick run ===");
        System.out.println("Dataset: " + dataset);
        System.out.println("Nodes: " + solution.getCycle().size());
        System.out.println("Reward: " + solution.getTotalReward());
        System.out.println("Distance: " + solution.getTotalDistance());
        System.out.println("Objective: " + solution.objectiveValue());
        System.out.println("Iterations: " + hae.getIterationCount());
        System.out.println("Time [ms]: " + (end - start));
    }
}

