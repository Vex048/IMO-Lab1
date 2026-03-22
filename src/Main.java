import experiments.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            int startNode = 134;
            Path datasetPath = Paths.get("datasets/TSPA.csv");

            List<Experiment> experimentList = new ArrayList<>();

            experimentList.add(new NearestNeighbourExperiment(
                    datasetPath,
                    NearestNeighbourExperiment.Criterion.DISTANCE,
                    NearestNeighbourExperiment.Mode.HAMILTONIAN,
                    startNode,
                    Paths.get("outputs/nearest_neighbour_solution_distance.txt")
            ));

            experimentList.add(new NearestNeighbourExperiment(
                    datasetPath,
                    NearestNeighbourExperiment.Criterion.DISTANCE,
                    NearestNeighbourExperiment.Mode.REDUCTION,
                    startNode,
                    Paths.get("outputs/nearest_neighbour_distance_removal.txt")
            ));

            experimentList.add(new NearestNeighbourExperiment(
                    datasetPath,
                    NearestNeighbourExperiment.Criterion.COST,
                    NearestNeighbourExperiment.Mode.HAMILTONIAN,
                    startNode,
                    Paths.get("outputs/nearest_neighbour_solution_cost.txt")
            ));

            experimentList.add(new NearestNeighbourExperiment(
                    datasetPath,
                    NearestNeighbourExperiment.Criterion.COST,
                    NearestNeighbourExperiment.Mode.REDUCTION,
                    startNode,
                    Paths.get("outputs/nearest_neighbour_cost_removal.txt")
            ));

            experimentList.add(new GreedyCycleExperiment(
                    datasetPath,
                    GreedyCycleExperiment.Mode.DISTANCE,
                    startNode,
                    Paths.get("outputs/greedy_cycle_distance.txt")
            ));

            experimentList.add(new GreedyCycleExperiment(
                    datasetPath,
                    GreedyCycleExperiment.Mode.OBJECTIVE,
                    startNode,
                    Paths.get("outputs/greedy_cycle_objective.txt")
            ));

            experimentList.add(new RegretCycleExperiment(
                    datasetPath,
                    startNode,
                    Paths.get("outputs/regret_cycle.txt")
            ));

            experimentList.add(new WeightedTwoRegretExperiment(
                    datasetPath,
                    WeightedTwoRegretExperiment.Mode.HAMILTONIAN,
                    startNode,
                    10.0, // regret weight
                    1.0, // greedy weight
                    Paths.get("outputs/weighted_two_regret.txt")
            ));

            experimentList.add(new WeightedTwoRegretExperiment(
                    datasetPath,
                    WeightedTwoRegretExperiment.Mode.REDUCTION,
                    startNode,
                    10.0, // regret weight
                    1.0, // greedy weight
                    Paths.get("outputs/weighted_two_regret_reduction.txt")
            ));

            experimentList.add(new RandomExperiment(
                    datasetPath,
                    startNode,
                    Paths.get("outputs/random_solution.txt")
            ));

            List<String> experimentNames = List.of(
                    "Nearest Neighbour (Distance, Hamiltonian)",
                    "Nearest Neighbour (Distance, Reduction)",
                    "Nearest Neighbour (Cost, Hamiltonian)",
                    "Nearest Neighbour (Cost, Reduction)",
                    "Greedy Cycle (Distance)",
                    "Greedy Cycle (Objective)",
                    "Regret Cycle",
                    "Weighted Two Regret (Hamiltonian)",
                    "Weighted Two Regret (Reduction)",
                    "Random Heuristic"
            );

            for (int i = 0; i < experimentList.size(); i++) {
                Experiment exp = experimentList.get(i);
                System.out.println("=== Running " + experimentNames.get(i) + " ===");
                ExperimentResult res = exp.run();
                System.out.println(res);
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
