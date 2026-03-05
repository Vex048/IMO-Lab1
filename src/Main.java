import experiments.ExperimentResult;
import experiments.NearestNeighbourExperiment;
import heuristics.NearestNeighbourHeuristic.Mode;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            int startNode = 134;
            String savePathStr = "outputs/nearest_neighbour.txt";

            Path savePath = (savePathStr == null || savePathStr.trim().isEmpty()) ? null : Paths.get(savePathStr);

            System.out.println("=== Nearest Neighbour Experiment ===");
            NearestNeighbourExperiment exp = new NearestNeighbourExperiment(
                    Paths.get("datasets/TSPA.csv"),
                    Mode.DISTANCE,
                    startNode,
                    savePath
            );
            ExperimentResult res = exp.run();

            System.out.println(res);
            System.out.println(res.tour());

            if (savePath != null) {
                System.out.println("Saved to: " + savePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}