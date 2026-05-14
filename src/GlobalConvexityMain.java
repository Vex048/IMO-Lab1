import globalconvexity.GlobalConvexityRunner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GlobalConvexityMain {
    public static void main(String[] args) {
        try {
            GlobalConvexityRunner.Config config = new GlobalConvexityRunner.Config(
                    List.of(
                            Paths.get("datasets/TSPA.csv"),
                            Paths.get("datasets/TSPB.csv")
                    ),
                    Path.of("outputs/global_convexity"),
                    1000,
                    2_000L,
                    20260514L
            );

            GlobalConvexityRunner.Artifacts artifacts = new GlobalConvexityRunner(config).run();

            System.out.println("=== Global convexity experiment finished ===");
            System.out.println("Details CSV: " + artifacts.detailsCsv());
            System.out.println("Correlations CSV: " + artifacts.correlationsCsv());
            System.out.println("Best solutions CSV: " + artifacts.bestSolutionsCsv());
            System.out.println("Summary Markdown: " + artifacts.summaryMarkdown());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

