import experiments.ExperimentResult;
import experiments.RegretCycleExperiment;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            int startNode = -1;
            String savePathStr = "outputs/regret_cycle.txt";
            Path savePath = Paths.get(savePathStr);

            System.out.println("=== Regret Cycle Experiment ===");
            RegretCycleExperiment exp = new RegretCycleExperiment(
                    Paths.get("datasets/TSPA.csv"),
                    startNode,
                    savePath
            );
            ExperimentResult res = exp.run();

            System.out.println(res);
            System.out.println(res.tour());

            System.out.println("Saved to: " + savePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}