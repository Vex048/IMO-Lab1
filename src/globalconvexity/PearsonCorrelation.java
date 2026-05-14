package globalconvexity;

public final class PearsonCorrelation {
    private PearsonCorrelation() {
    }

    public static double calculate(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Arrays must have the same length");
        }
        if (x.length < 2) {
            return Double.NaN;
        }

        double avgX = average(x);
        double avgY = average(y);
        double numerator = 0.0;
        double sumX = 0.0;
        double sumY = 0.0;

        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - avgX;
            double dy = y[i] - avgY;
            numerator += dx * dy;
            sumX += dx * dx;
            sumY += dy * dy;
        }

        double denominator = Math.sqrt(sumX * sumY);
        if (denominator == 0.0) {
            return Double.NaN;
        }
        return numerator / denominator;
    }

    private static double average(double[] values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }
}

