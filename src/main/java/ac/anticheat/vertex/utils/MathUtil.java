package ac.anticheat.vertex.utils;

import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;

import java.util.*;

public class MathUtil {
    public static double jerk(List<Double> values) {
        if (values.size() < 4) return 0.0;

        double jerk = 0.0;
        for (int i = 3; i < values.size(); i++) {
            double j = values.get(i) - 3*values.get(i - 1) + 3*values.get(i - 2) - values.get(i - 3);
            jerk += Math.abs(j);
        }

        return jerk;
    }

    public static double autocorrelation(List<Double> data, int lag) {
        int n = data.size();
        if (n <= lag + 1) return 1.0;

        double mean = 0.0;
        for (double v : data) mean += v;
        mean /= n;

        double numerator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i < n; i++) {
            double diff = data.get(i) - mean;
            denominator += diff * diff;

            int j = i + lag;
            if (j < n) {
                double diffLag = data.get(j) - mean;
                numerator += diff * diffLag;
            }
        }

        if (denominator == 0.0) return 1.0;
        return numerator / denominator;
    }

    public static double r2Linearity(List<Double> deltas) {
        int n = deltas.size();
        if (n < 2) return 1.0;

        double sumX = 0, sumY = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += deltas.get(i);
        }
        double meanX = sumX / n;
        double meanY = sumY / n;

        double ssXX = 0, ssYY = 0, ssXY = 0;
        for (int i = 0; i < n; i++) {
            double dx = i - meanX;
            double dy = deltas.get(i) - meanY;
            ssXX += dx * dx;
            ssYY += dy * dy;
            ssXY += dx * dy;
        }
        if (ssXX == 0 || ssYY == 0) return 1.0;

        double slope = ssXY / ssXX;
        double intercept = meanY - slope * meanX;

        double ssr = 0;
        for (int i = 0; i < n; i++) {
            double pred = slope * i + intercept;
            double resid = deltas.get(i) - pred;
            ssr += resid * resid;
        }

        double r2 = 1.0 - (ssr / ssYY);
        if (Double.isNaN(r2)) r2 = 1.0;
        return Math.max(0.0, Math.min(1.0, r2));
    }

    public static int signChanges(List<Double> data) {
        if (data == null || data.size() < 2) {
            return 0;
        }

        int changes = 0;
        double prev = data.get(0);

        for (int i = 1; i < data.size(); i++) {
            double curr = data.get(i);

            double eps = 1E-4;

            if (Math.signum(prev) != Math.signum(curr) || (Math.abs(curr) < eps && curr != 0) || (Math.abs(prev) < eps && prev != 0)) {
                changes++;
            }

            prev = curr;
        }

        return changes;
    }

    public static double stddev(List<Long> data) {
        int n = data.size();
        if (n == 0) return 0.0;

        double mean = 0.0;
        for (double v : data) {
            mean += v;
        }
        mean /= n;

        double variance = 0.0;
        for (double v : data) {
            double diff = v - mean;
            variance += diff * diff;
        }
        variance /= n;

        return Math.sqrt(variance);
    }

    public static double jarqueBera(List<Double> data) {
        int n = data.size();
        if (n < 3) {
            return 0;
        }

        double mean = 0.0;
        for (double x : data) {
            mean += x;
        }
        mean /= n;

        double m2 = 0.0;
        double m3 = 0.0;
        double m4 = 0.0;

        for (double x : data) {
            double d = x - mean;
            double d2 = d * d;
            m2 += d2;
            m3 += d2 * d;
            m4 += d2 * d2;
        }

        m2 /= n;
        m3 /= n;
        m4 /= n;

        double skewness = m3 / Math.pow(m2, 1.5);
        double kurtosis = m4 / (m2 * m2);

        return (n / 6.0) *
                (skewness * skewness +
                        Math.pow(kurtosis - 3.0, 2) / 4.0);
    }

    public static double tailDeficiency(List<Double> data) {
        int n = data.size();
        if (n < 20) return 0.0;

        double mean = 0.0;
        for (double v : data) mean += v;
        mean /= n;

        double variance = 0.0;
        for (double v : data) {
            double d = v - mean;
            variance += d * d;
        }
        variance /= n;

        double std = Math.sqrt(variance);
        if (std == 0.0) return 1.0;

        double[] abs = new double[n];
        for (int i = 0; i < n; i++) {
            abs[i] = Math.abs(data.get(i));
        }

        Arrays.sort(abs);

        int idx = (int) (0.95 * n);
        double p95 = abs[Math.min(idx, n - 1)];

        return p95 / (2.0 * std);
    }

    public static double meanJerk(List<Double> data) {
        if (data.size() < 3) return 0.0;

        double sum = 0;
        for (int i = 2; i < data.size(); i++) {
            double jerk = data.get(i)
                    - 2 * data.get(i - 1)
                    + data.get(i - 2);
            sum += Math.abs(jerk);
        }
        return sum / (data.size() - 2);
    }

    public static double runsZScore(List<Double> values) {
        if (values == null || values.size() < 10) {
            return 0.0;
        }

        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        double median = sorted.get(sorted.size() / 2);

        List<Integer> signs = new ArrayList<>();
        for (double v : values) {
            if (v > median) signs.add(1);
            else if (v < median) signs.add(-1);
        }

        if (signs.size() < 10) return 0.0;

        int runs = 1;
        for (int i = 1; i < signs.size(); i++) {
            if (!signs.get(i).equals(signs.get(i - 1))) {
                runs++;
            }
        }

        int n1 = 0, n2 = 0;
        for (int s : signs) {
            if (s == 1) n1++;
            else n2++;
        }

        double expectedRuns = (2.0 * n1 * n2) / (n1 + n2) + 1;
        double varianceRuns =
                (2.0 * n1 * n2 * (2.0 * n1 * n2 - n1 - n2)) /
                        (Math.pow(n1 + n2, 2) * (n1 + n2 - 1));

        if (varianceRuns <= 0) return 0.0;

        return (runs - expectedRuns) / Math.sqrt(varianceRuns);
    }

    public static double entropy(List<Double> data) {
        int bins = 10;
        double min = Collections.min(data);
        double max = Collections.max(data);
        double width = (max - min) / bins;

        if (width == 0) return 0;

        int[] hist = new int[bins];
        for (double v : data) {
            int b = Math.min(bins - 1, (int) ((v - min) / width));
            hist[b]++;
        }

        double h = 0;
        for (int c : hist) {
            if (c == 0) continue;
            double p = c / (double) data.size();
            h -= p * Math.log(p);
        }

        return h / Math.log(bins);
    }

    public static double runsTest(List<Double> data) {
        double median = data.stream().sorted().skip(data.size()/2).findFirst().orElse((double) 0);

        int runs = 1;
        boolean above = data.get(0) > median;

        for (double v : data) {
            boolean now = v > median;
            if (now != above) {
                runs++;
                above = now;
            }
        }

        return Math.abs(runs - data.size() / 2.0) / data.size();
    }

    public static double madZ(List<Double> data) {
        List<Double> sorted = new ArrayList<>(data);
        Collections.sort(sorted);

        double median = sorted.get(sorted.size() / 2);

        List<Double> dev = new ArrayList<>();
        for (double v : data) {
            dev.add(Math.abs(v - median));
        }

        Collections.sort(dev);
        double mad = dev.get(dev.size() / 2);
        if (mad == 0) return 0;

        return Math.abs(data.get(data.size() - 1) - median) / (1.4826 * mad);
    }

    public static double cusum(List<Double> data) {
        double mean = data.stream().mapToDouble(d -> d).average().orElse(0);
        double s = 0;
        double max = 0;

        for (double v : data) {
            s = Math.max(0, s + v - mean);
            max = Math.max(max, s);
        }
        return max;
    }

    public static double peakEnergyRatio(List<Double> data, int k) {
        List<Double> copy = new ArrayList<>(data);
        copy.sort(Collections.reverseOrder());

        double top = 0.0, sum = 0.0;
        for (double d : copy) {
            sum += d;
        }
        for (int i = 0; i < Math.min(k, copy.size()); i++) {
            top += copy.get(i);
        }

        return top / (sum + 1e-6);
    }

    public static double spectralFlatness(List<Double> data) {
        int n = data.size();
        if (n < 8) return 1.0;

        double[] power = new double[n];

        for (int k = 0; k < n; k++) {
            double real = 0.0;
            double imag = 0.0;
            for (int t = 0; t < n; t++) {
                double angle = 2.0 * Math.PI * k * t / n;
                real += data.get(t) * Math.cos(angle);
                imag -= data.get(t) * Math.sin(angle);
            }
            power[k] = real * real + imag * imag + 1e-12;
        }

        double geoMean = 0.0;
        double arithMean = 0.0;

        for (double p : power) {
            geoMean += Math.log(p);
            arithMean += p;
        }

        geoMean = Math.exp(geoMean / n);
        arithMean /= n;

        if (arithMean == 0.0) return 1.0;

        return geoMean / arithMean;
    }

    public static double permutationEntropy(List<Double> data, int m) {
        int n = data.size();
        if (n < m + 1) return 0.0;

        Map<String, Integer> counts = new HashMap<>();
        int total = 0;

        for (int i = 0; i <= n - m; i++) {
            double[] window = new double[m];
            for (int j = 0; j < m; j++) {
                window[j] = data.get(i + j);
            }

            Integer[] idx = new Integer[m];
            for (int j = 0; j < m; j++) idx[j] = j;

            Arrays.sort(idx, Comparator.comparingDouble(j -> window[j]));

            StringBuilder key = new StringBuilder();
            for (int j : idx) key.append(j);

            counts.merge(key.toString(), 1, Integer::sum);
            total++;
        }

        double entropy = 0.0;
        for (int c : counts.values()) {
            double p = (double) c / total;
            entropy -= p * Math.log(p);
        }

        double maxEntropy = Math.log(factorial(m));
        return maxEntropy == 0 ? 0.0 : entropy / maxEntropy;
    }

    private static int factorial(int n) {
        int r = 1;
        for (int i = 2; i <= n; i++) r *= i;
        return r;
    }
}
