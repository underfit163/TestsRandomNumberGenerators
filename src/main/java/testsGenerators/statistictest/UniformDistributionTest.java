package testsGenerators.statistictest;

import sample.NumberSample;

import java.util.Arrays;

public abstract class UniformDistributionTest {
    private volatile int[][] frequencyCountDouble;
    private final NumberSample numberSample;

    public int[][] getFrequencyCountDouble() {
        return frequencyCountDouble;
    }

    public UniformDistributionTest(NumberSample numberSample) {
        this.numberSample = numberSample;
    }

    public synchronized void initFrequency(int n, double len) {
        if (frequencyCountDouble == null) {
            double[] intervals = new double[n + 1];
            intervals[0] = 0;
            for (int i = 1; i < intervals.length; i++) {
                intervals[i] = intervals[i - 1] + len;
            }
            frequencyCountDouble = new int[numberSample.getCountSample()][n];
            for (int k = 0; k < numberSample.getCountSample(); k++) {
                int finalK = k;
                Arrays.parallelSetAll(frequencyCountDouble[k], i -> getValFreqCountDouble(i, finalK, intervals, n, numberSample.getMaxNumber()));
            }
        }
    }

    public int getValFreqCountDouble(int i, int j, double[] intervals, int n, double max) {
        int ch = 0;
        for (int k = 0; k < numberSample.getNSample(); k++) {
            double item = (double) numberSample.getItemSample(j, k) / max;
            if (intervals[i] <= item && item < intervals[i + 1]) {
                ch++;
            } else if (i == (n - 1) && intervals[i + 1] <= item && item <= 1) {
                ch++;
            }
        }
        return ch;
    }
}
