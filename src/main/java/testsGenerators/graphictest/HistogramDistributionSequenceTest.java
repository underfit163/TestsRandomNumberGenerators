package testsGenerators.graphictest;

import sample.NumberSample;

import java.util.Arrays;

/*
20)	Гистограмма распределения элементов последовательности.
 */
public class HistogramDistributionSequenceTest implements GraphicTest {
    private final NumberSample numberSample;
    private double[] heights;
    private double[] halfIntervals;

    public void initHeights(int n) {
        heights = new double[n];
    }

    public void initHalfIntervals(int n) {
        halfIntervals = new double[n];
    }

    public double[] getHeights() {
        return heights;
    }

    public void setParamHeights(int i, double val) {
        heights[i] = val;
    }

    public void setParamHalfIntervals(int i, double val) {
        halfIntervals[i] = val;
    }

    public double getParamHeights(int i) {
        return heights[i];
    }

    public double getParamHalfIntervals(int i) {
        return halfIntervals[i];
    }

    public HistogramDistributionSequenceTest(NumberSample numberSample) {
        this.numberSample = numberSample;
    }

    @Override
    public void runTest() {
        //число интервалов
        int n = 10;
        //длина интервала
        double len = (double) 1 / n;

        double[] intervals = new double[n + 1];
        intervals[0] = 0;
        for (int i = 1; i < intervals.length; i++) {
            intervals[i] = intervals[i - 1] + len;
        }

        int[] frequencyDouble = new int[n];
        double max = Math.pow(2, numberSample.getCapacity()) - 1;

        Arrays.parallelSetAll(frequencyDouble, i -> getValFreqDouble(i, intervals, n, max));

        //высоты столбцов гист.
        initHeights(n);
        for (int i = 0; i < n; i++) {
            setParamHeights(i, (double) frequencyDouble[i] / (numberSample.getNSample() * len));
        }

        //середины интервалов
        initHalfIntervals(n);
        setParamHalfIntervals(0, 0 + len / 2.0);
        for (int i = 1; i < n; i++) {
            setParamHalfIntervals(i, getParamHalfIntervals(i - 1) + len);
        }
    }

    public int getValFreqDouble(int i, double[] intervals, int n, double max) {
        int ch = 0;
        //for (int j = 0; j < numberSample.getCountSample(); j++) {
            for (int k = 0; k < numberSample.getNSample(); k++) {
                double item = (double) numberSample.getItemSample(0, k) / max;
                if (intervals[i] <= item && item < intervals[i + 1]) {
                    ch++;
                } else if (i == n - 1 && intervals[i + 1] <= item && item <= 1) {
                    ch++;
                }
            }
        //}
        return ch;
    }

    @Override
    public void run() {
        runTest();
    }
}
