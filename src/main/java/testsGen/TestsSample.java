package testsGen;

import Sample.NumberSample;
import fr.devnied.bitlib.BytesUtils;
import javafx.scene.control.Alert;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TestsSample {

    private final boolean[] test = new boolean[12];
    private final boolean[] testPval = new boolean[12];
    private final double[] dol = new double[12];
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private int[][] frequencyCountDouble;

    private List<SynchronizedSummaryStatistics> summaryStatistics;
    private double expMean;

    public TestsSample(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }


    public double getExpMean() {
        return expMean;
    }

    public double[] getDol() {
        return dol;
    }

    public boolean[] getTest() {
        return test;
    }

    public boolean[] getTestPval() {
        return testPval;
    }

    //-------------------------------------------------------------------------------------------------------------------
    public void testFunGraphicDouble() {
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
        paramsTest.initHeights(n);
        for (int i = 0; i < n; i++) {
            paramsTest.setParamHeights(i, (double) frequencyDouble[i] / (numberSample.getNSampleMas() * len));
        }

        //середины интервалов
        paramsTest.initHalfIntervals(n);
        paramsTest.setParamHalfIntervals(0, 0 + len / 2.0);
        for (int i = 1; i < n; i++) {
            paramsTest.setParamHalfIntervals(i, paramsTest.getParamHalfIntervals(i - 1) + len);
        }
    }

    public int getValFreqDouble(int i, double[] intervals, int n, double max) {
        int ch = 0;
        for (int j = 0; j < numberSample.getCountSample(); j++) {
            for (int k = 0; k < numberSample.getNSample(); k++) {
                double item = (double) numberSample.getItemSample(j, k) / max;
                if (intervals[i] <= item && item < intervals[i + 1]) {
                    ch++;
                } else if (i == n - 1 && intervals[i + 1] <= item && item <= 1) {
                    ch++;
                }
            }
        }
        return ch;
    }

    public void testFun1Double() {
        double max = Math.pow(2, numberSample.getCapacity()) - 1;
        summaryStatistics = new ArrayList<>();
        SynchronizedSummaryStatistics statistics;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            summaryStatistics.add(new SynchronizedSummaryStatistics());
            statistics = summaryStatistics.get(i);
            for (int j = 0; j < numberSample.getNSample(); j++) {
                statistics.addValue((double) numberSample.getItemSample(i, j) / max);
            }
        }
        //число интервалов
        int n = 10;
        //длина интервала
        double len = (double) 1 / n;

        double[] intervals = new double[n + 1];
        intervals[0] = 0;
        for (int i = 1; i < intervals.length; i++) {
            intervals[i] = intervals[i - 1] + len;
        }
        frequencyCountDouble = new int[numberSample.getCountSample()][n];
        for (int k = 0; k < numberSample.getCountSample(); k++) {
            int finalK = k;
            Arrays.parallelSetAll(frequencyCountDouble[k], i -> getValFreqCountDouble(i, finalK, intervals, n, max));
        }
        // проводим первый тест:
        double p;
       /* double p90left = 1 / (n + (1 / (2 * Math.sqrt(3))) * (n - 1));
        double p90right = 1 / (n - (1 / (2 * Math.sqrt(3))) * (n - 1));*/
        double p90left = (numberSample.getNSample() - 2.58 * Math.sqrt(numberSample.getNSample() * (n - 1))) / n;
        double p90right = (numberSample.getNSample() + 2.58 * Math.sqrt(numberSample.getNSample() * (n - 1))) / n;

        boolean[] masTest1 = new boolean[numberSample.getCountSample()];
        int count = 0;
        int count1 = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < n; j++) {
                p = frequencyCountDouble[i][j];
                if (p90left <= p && p <= p90right) {
                    count1++;
                }
            }
            if ((double) count1 / n >= 0.9) masTest1[i] = true;
            if (masTest1[i]) {
                count++;
            }
            count1 = 0;
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        dol[0] = (double) count / numberSample.getCountSample();
        test[0] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[0] &&
                dol[0] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
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

    public void testFun2Double() {
        int n = 10;
        double[] xi2 = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        int count = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < n; j++) {
                xi2[i] += Math.pow(frequencyCountDouble[i][j] - (double) numberSample.getNSample() / n, 2) / ((double) numberSample.getNSample() / n);
            }
            pValue[i] = Gamma.regularizedGammaQ((double) (n - 1) / 2, xi2[i] / 2);
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
        }
        //Анализ числа появлений значений P-value
        int[] vPvalue = new int[10];
        double left;
        double right;
        for (double v : pValue) {
            for (int i = 1; i <= vPvalue.length; i++) {
                left = (double) (i - 1) / 10;
                right = (double) i / 10;
                if (i == 10 && v == right) {
                    vPvalue[i - 1]++;
                }
                if (left <= v && v < right) {
                    vPvalue[i - 1]++;
                    break;
                }
            }
        }
        double xi2Pvalue = 0;
        for (int j : vPvalue) {
            xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
        }
        xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
        xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
        if (xi2Pvalue >= paramsTest.getA()) {
            testPval[1] = true;
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        if (testPval[1]) {
            dol[1] = (double) count / numberSample.getCountSample();
            test[1] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[1] &&
                    dol[1] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
        } else {
            test[1] = false;
        }
    }

    public void testFun3Double() {
        double[] mean = new double[numberSample.getCountSample()];
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            mean[i] = summaryStatistics.get(i).getMean();
            expMean += mean[i];
        }
        //общее эмперическое мат. ожидание
        expMean = expMean / numberSample.getCountSample();
        //M = numberSample.getNSample(), N = n
        //теоретич. мат. ожидание
        double teorMean = 0.5;
        double otkl = (Math.abs((expMean - teorMean) / teorMean)) * 100;
        dol[2] = otkl;
        test[2] = otkl <= 5;
    }

    /**
     * Частотный монобитный тест
     */
    public void testFun4() {
        //из ряда 2^numEng
        int numEng = (int) (Math.log(numberSample.getNSample()) / Math.log(2));
        int otherN = (int) Math.pow(2, numEng);
        int[] sum = new int[numberSample.getCountSample()];
        Arrays.parallelSetAll(sum, i -> getValSum(i, otherN));
        double[] Sn = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        int count = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            Sn[i] = Math.abs(sum[i]) / Math.sqrt(2 * otherN * numberSample.getCapacity());
            pValue[i] = Erf.erfc(Sn[i]);
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
        }
        //Анализ числа появлений значений P-value
        int[] vPvalue = new int[10];
        double left;
        double right;
        for (double v : pValue) {
            for (int i = 1; i <= vPvalue.length; i++) {
                left = (double) (i - 1) / 10;
                right = (double) i / 10;
                if (i == 10 && v == right) {
                    vPvalue[i - 1]++;
                }
                if (left <= v && v < right) {
                    vPvalue[i - 1]++;
                    break;
                }
            }
        }
        double xi2Pvalue = 0;
        for (int j : vPvalue) {
            xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
        }
        xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
        xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
        if (xi2Pvalue >= paramsTest.getA()) {
            testPval[3] = true;
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        if (testPval[3]) {
            dol[3] = (double) count / numberSample.getCountSample();
            test[3] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[3] &&
                    dol[3] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
        } else {
            test[3] = false;
        }
    }

    public int getValSum(int i, int otherN) {
        int sumCount = 0;
        for (int j = 0; j < otherN; j++) {
            for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) sumCount++;
                else sumCount--;
            }
        }
        return sumCount;
    }

    /*
    частотный блочный тест
    */
    public void testFun5() {
        long n = (long) numberSample.getNSample() * numberSample.getCapacity();
        int N;
        long M = 19;
        do {
            M++;
            N = (int) (n / M);
        }
        while (N >= 100 || M <= 0.01 * n);
        long nNew = (long) N * M;
        int otherNSample = (int) (nNew / numberSample.getCapacity());
        int diff = (int) (nNew % numberSample.getCapacity());
        double[][] sum = new double[numberSample.getCountSample()][N];
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            long finalM = M;
            int finalI = i;
            Arrays.parallelSetAll(sum[i], t -> getCountBit(t, finalI, otherNSample, diff, finalM));
        }

        double[] xi2 = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        int count = 0;
        double p = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < N; j++) {
                p += Math.pow(sum[i][j] - 0.5, 2);
            }
            xi2[i] = 4 * M * p;
            pValue[i] = Gamma.regularizedGammaQ((double) N / 2, xi2[i] / 2);
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
            p = 0;
        }
        //Анализ числа появлений значений P-value
        int[] vPvalue = new int[10];
        double left;
        double right;
        for (double v : pValue) {
            for (int i = 1; i <= vPvalue.length; i++) {
                left = (double) (i - 1) / 10;
                right = (double) i / 10;
                if (i == 10 && v == right) {
                    vPvalue[i - 1]++;
                }
                if (left <= v && v < right) {
                    vPvalue[i - 1]++;
                    break;
                }
            }
        }
        double xi2Pvalue = 0;
        for (int j : vPvalue) {
            xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
        }
        xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
        xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
        if (xi2Pvalue >= paramsTest.getA()) {
            testPval[4] = true;
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        if (testPval[4]) {
            dol[4] = (double) count / numberSample.getCountSample();
            test[4] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[4] &&
                    dol[4] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
        } else {
            test[4] = false;
        }
    }

    public double getCountBit(int t, int i, int otherNSample, int diff, long M) {
        int bitCount = 0;
        for (int j = (int) ((t * M) / numberSample.getCapacity()); j < (int) (((t + 1) * M) / numberSample.getCapacity()); j++) {
            for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                long p = (long) numberSample.getCapacity() * j + (numberSample.getCapacity() - 1 - k);
                if (M * t <= p && p < M * (t + 1)) {
                    if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) bitCount++;
                }
                if (diff != 0 && j == otherNSample - 1 && k == 0) {
                    for (int l = (numberSample.getCapacity() - 1); l > (numberSample.getCapacity() - 1 - diff); l--) {
                        if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j + 1), l)) bitCount++;
                        p++;
                    }
                }
                if (p == (M * (t + 1) - 1)) {
                    return (double) bitCount / M;
                }
            }
        }
        return (double) bitCount / M;
    }

    /*
    частотный тест на динные последовательности
     */
    public void testFun6() {
        int[][] ret = new int[2][numberSample.getCountSample()];
        if (numberSample.getCountSample() > 1) {
            for (int k = 0; k < 2; k++) {
                int finalK = k;
                Arrays.parallelSetAll(ret[k], i -> getMaxCountBit(i, finalK));
            }
        } else {
            int len1 = 0;
            int len0 = 0;
            for (int i = 0; i < numberSample.getCountSample(); i++) {
                for (int j = 0; j < numberSample.getNSample(); j++) {
                    for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                        if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) {
                            len1++;
                            len0 = 0;
                        } else {
                            len1 = 0;
                            len0++;
                        }
                        ret[1][i] = Math.max(ret[1][i], len1);
                        ret[0][i] = Math.max(ret[0][i], len0);
                    }
                }
                len1 = 0;
                len0 = 0;
            }
        }
        int maxCountBit = 34;
        int countTrue = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            if (ret[1][i] < maxCountBit && ret[0][i] < maxCountBit) {
                countTrue++;
            }
        }
        dol[5] = (double) countTrue / numberSample.getCountSample();
        test[5] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[5] &&
                dol[5] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
    }

    public int getMaxCountBit(int i, int t) {
        int ret = 0;
        int len = 0;
        for (int j = 0; j < numberSample.getNSample(); j++) {
            for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) {
                    if (t == 0) {
                        len = 0;
                    } else len++;
                } else {
                    if (t == 0) {
                        len++;
                    } else len = 0;
                }
                ret = Math.max(ret, len);
            }
        }
        return ret;
    }

    /*
    Тест на последовательность одинаковых битов
     */
    public void testFun7() {
        double[] sumBit = new double[numberSample.getCountSample()];
        Arrays.parallelSetAll(sumBit, this::getSum1Bit);
        double t = 2 / (Math.sqrt(numberSample.getNSample() * numberSample.getCapacity()));
        long[] series = new long[numberSample.getCountSample()];
        Arrays.parallelSetAll(series, this::getCountSeries);
        double[] pValue = new double[numberSample.getCountSample()];
        int count = 0;
        long n = (long) numberSample.getNSample() * numberSample.getCapacity();
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            if (Math.abs(sumBit[i] - 0.5) < t) {
                pValue[i] = Erf.erfc(Math.abs(series[i] - 2 * sumBit[i] * n * (1 - sumBit[i])) /
                        (2 * Math.sqrt(2 * n) * sumBit[i] * (1 - sumBit[i])));
                if (paramsTest.getA() <= pValue[i]) {
                    count++;
                }
            }
        }
        //Анализ числа появлений значений P-value
        int[] vPvalue = new int[10];
        double left;
        double right;
        for (double v : pValue) {
            for (int i = 1; i <= vPvalue.length; i++) {
                left = (double) (i - 1) / 10;
                right = (double) i / 10;
                if (i == 10 && v == right) {
                    vPvalue[i - 1]++;
                }
                if (left <= v && v < right) {
                    vPvalue[i - 1]++;
                    break;
                }
            }
        }
        double xi2Pvalue = 0;
        for (int j : vPvalue) {
            xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
        }
        xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
        xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
        if (xi2Pvalue >= paramsTest.getA()) {
            testPval[6] = true;
        }
        if (testPval[6]) {
            //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
            dol[6] = (double) count / numberSample.getCountSample();
            test[6] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[6] &&
                    dol[6] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
        } else {
            test[6] = false;
        }
    }

    public double getSum1Bit(int i) {
        double sumCount = 0;
        for (int j = 0; j < numberSample.getNSample(); j++) {
            for (int k = numberSample.getCapacity() - 1; k >= 0; k--) {
                if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) sumCount++;
            }
        }
        return sumCount / (numberSample.getNSample() * numberSample.getCapacity());
    }

    public long getCountSeries(int i) {
        long sumCount = 1;
        boolean bitLast = false;
        for (int j = 0; j < numberSample.getNSample(); j++) {
            for (int k = (numberSample.getCapacity() - 1); k > 0; k--) {
                if (j != 0 && k == (numberSample.getCapacity() - 1)) {
                    if (bitLast != BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) sumCount++;
                }
                if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k) !=
                        BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k - 1)) sumCount++;
                if (k - 1 == 0) {
                    bitLast = BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), 0);
                }
            }
        }
        return sumCount;
    }

    /**
     * Проверка на равномерность в подпоследовательностях
     */
    public void testFun8() {
        int M = 8;
        int K = 3;
        long size = (long) numberSample.getNSample() * numberSample.getCapacity();
        if (6272 <= size && size < 750000) {
            M = 128;
            K = 5;
        } else if (750000 <= size) {
            M = 10000;
            K = 6;
        }
        int N = numberSample.getNSample() / M;
        int newNSampleMas = M * N;
        int newNSample = newNSampleMas / numberSample.getCapacity();
        int diffBit = newNSampleMas % numberSample.getCapacity();

        int[][] countMax = new int[numberSample.getCountSample()][N];
        int finalM = M;
        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(i -> {
            int maxCount = 0;
            int sumCount = 0;
            int numBlock = 0;
            int countBlock = 0;
            for (int j = 0; j < newNSample; j++) {
                for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                    if (countBlock < finalM) {
                        if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) {
                            sumCount++;
                        } else {
                            if (sumCount > maxCount) {
                                maxCount = sumCount;
                            }
                            sumCount = 0;
                        }
                        countBlock++;
                    }
                    if (countBlock == finalM) {
                        if (sumCount > maxCount) {
                            maxCount = sumCount;
                        }
                        countMax[i][numBlock] = maxCount;
                        numBlock++;
                        sumCount = 0;
                        maxCount = 0;
                        countBlock = 0;
                    }
                    if (diffBit != 0 && j == (newNSample - 1) && k == 0) {
                        for (int l = (numberSample.getCapacity() - 1); l > (numberSample.getCapacity() - 1 - diffBit); l--) {
                            if (countBlock < finalM) {
                                if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j + 1), l)) {
                                    sumCount++;
                                } else {
                                    if (sumCount > maxCount) {
                                        maxCount = sumCount;
                                    }
                                    sumCount = 0;
                                }
                                countBlock++;
                            }
                            if (countBlock == finalM) {
                                if (sumCount > maxCount) {
                                    maxCount = sumCount;
                                }
                                countMax[i][numBlock] = maxCount;
                                numBlock++;
                                sumCount = 0;
                                maxCount = 0;
                                countBlock = 0;
                            }
                        }
                    }
                }
            }
        });

        int[][] v = new int[numberSample.getCountSample()][K + 1];
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < N; j++) {
                categorRasp(v, i, countMax[i][j], M);
            }
        }
        double[] xi2 = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        int count = 0;
        double x1 = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < K + 1; j++) {
                x1 += Math.pow(v[i][j] - N * getPi(M, j), 2) / (N * getPi(M, j));
            }
            xi2[i] = x1;
            pValue[i] = Gamma.regularizedGammaQ((double) K / 2, xi2[i] / 2);
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
            x1 = 0;
        }
        //Анализ числа появлений значений P-value
        int[] vPvalue = new int[10];
        double left;
        double right;
        for (double value : pValue) {
            for (int i = 1; i <= vPvalue.length; i++) {
                left = (double) (i - 1) / 10;
                right = (double) i / 10;
                if (i == 10 && value == right) {
                    vPvalue[i - 1]++;
                }
                if (left <= value && value < right) {
                    vPvalue[i - 1]++;
                    break;
                }
            }
        }
        double xi2Pvalue = 0;
        for (int j : vPvalue) {
            xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
        }
        xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
        xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
        if (xi2Pvalue >= paramsTest.getA()) {
            testPval[7] = true;
        }
        if (testPval[7]) {
            //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
            dol[7] = (double) count / numberSample.getCountSample();
            test[7] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[7] &&
                    dol[7] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
        } else {
            test[7] = false;
        }
    }

    public void categorRasp(int[][] v, int i, int l, int M) {
        if (M == 8) {
            if (l <= 1) {
                v[i][0]++;
            } else if (l == 2) {
                v[i][1]++;
            } else if (l == 3) {
                v[i][2]++;
            } else {
                v[i][3]++;
            }
        } else if (M == 128) {
            if (l <= 4) {
                v[i][0]++;
            } else if (l == 5) {
                v[i][1]++;
            } else if (l == 6) {
                v[i][2]++;
            } else if (l == 7) {
                v[i][3]++;
            } else if (l == 8) {
                v[i][4]++;
            } else {
                v[i][5]++;
            }
        } else if (M == 10000) {
            if (l <= 10) {
                v[i][0]++;
            } else if (l == 11) {
                v[i][1]++;
            } else if (l == 12) {
                v[i][2]++;
            } else if (l == 13) {
                v[i][3]++;
            } else if (l == 14) {
                v[i][4]++;
            } else if (l == 15) {
                v[i][5]++;
            } else {
                v[i][6]++;
            }
        }
    }

    public double getPi(int M, int i) {
        double v = 0;
        if (M == 8) {
            switch (i) {
                case 0 -> v = 0.2148;
                case 1 -> v = 0.3672;
                case 2 -> v = 0.2305;
                case 3 -> v = 0.1875;
            }
        } else if (M == 128) {
            v = switch (i) {
                case 0 -> 0.1174;
                case 1 -> 0.2430;
                case 2 -> 0.2493;
                case 3 -> 0.1752;
                case 4 -> 0.1027;
                case 5 -> 0.1124;
                default -> v;
            };
        } else if (M == 10000) {
            v = switch (i) {
                case 0 -> 0.0882;
                case 1 -> 0.2092;
                case 2 -> 0.2483;
                case 3 -> 0.1933;
                case 4 -> 0.1208;
                case 5 -> 0.0675;
                case 6 -> 0.0727;
                default -> v;
            };
        }
        return v;
    }

    /**
     * Покер тест
     */
    public void testFun9() {
        int t = 5;
        int nGroup = numberSample.getNSample() / t;
        int newNSample = t * nGroup;
        int d = (int) (Math.pow(2, numberSample.getCapacity()) - 1);
        double[] p = new double[t];
        int[][] v = new int[numberSample.getCountSample()][t];
        int[] countPos = new int[t];
        Arrays.fill(countPos, 1);
        boolean[] boolPos = new boolean[t];
        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(i -> {
            int countT;
            int sumVal;
            int r;
            for (int j = 0; j < newNSample; j++) {
                countT = (j + 1) % t;
                if (countT == 0) {
                    sumVal = Arrays.stream(countPos).sum();
                    if (sumVal == 5) {
                        v[i][4]++;
                    } else if (sumVal == 4) {
                        v[i][3]++;
                    } else if (sumVal == 3) {
                        v[i][2]++;
                    } else if (sumVal == 2) {
                        v[i][1]++;
                    } else if (sumVal == 1) {
                        v[i][0]++;
                    }
                    for (int k = 0; k < t; k++) {
                        boolPos[k] = false;
                        countPos[k] = 1;
                    }
                } else {
                    r = 1;
                    for (int k = countT; k < t; k++) {
                        if (numberSample.getItemSample(i, j) ==
                                numberSample.getItemSample(i, j + r)
                                && !boolPos[k]) {
                            countPos[k]--;
                            boolPos[k] = true;
                        }
                        r++;
                    }
                }
            }
        });

        double Accum = 1;
        for (int i = 0; i < t; i++) {
            Accum = Accum * (d - i);
            p[i] = Accum * CombinatoricsUtils.stirlingS2(t, i + 1) / Math.pow(d, t);
        }

        double[] xi2 = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        int count = 0;
        double x1 = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < t; j++) {
                x1 += Math.pow(v[i][j] - nGroup * p[j], 2) / (nGroup * p[j]);
            }
            xi2[i] = x1;
            pValue[i] = Gamma.regularizedGammaQ((double) (t - 1) / 2, xi2[i] / 2);
            if (paramsTest.getA() <= pValue[i] && pValue[i] != 1.0) {
                count++;
            }
            x1 = 0;
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        dol[8] = (double) count / numberSample.getCountSample();
        test[8] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[8] &&
                dol[8] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
    }

    /**
     * Тест Колмогорова-Смирнова
     */
    public void testFun10() throws OutOfMemoryError {
        try {
            double max = Math.pow(2, numberSample.getCapacity()) - 1;
            double[][] sampleDouble = new double[numberSample.getCountSample()][numberSample.getNSample()];
            UniformRealDistribution uniformRealDistribution = new UniformRealDistribution(0, 1);
            KolmogorovSmirnovTest smirnovTest = new KolmogorovSmirnovTest();
            double[] pValue = new double[numberSample.getCountSample()];
            int count = 0;
            for (int i = 0; i < numberSample.getCountSample(); i++) {
                for (int j = 0; j < numberSample.getNSample(); j++) {
                    sampleDouble[i][j] = (double) numberSample.getItemSample(i, j) / max;
                }
                pValue[i] = 1.0D - smirnovTest.cdf(kolmogorovSmirnovTest(uniformRealDistribution, sampleDouble[i]), sampleDouble[i].length);

                if (paramsTest.getA() <= pValue[i]) {
                    count++;
                }
            }
            //Анализ числа появлений значений P-value
            int[] vPvalue = new int[10];
            double left;
            double right;
            for (double v : pValue) {
                for (int i = 1; i <= vPvalue.length; i++) {
                    left = (double) (i - 1) / 10;
                    right = (double) i / 10;
                    if (i == 10 && v == right) {
                        vPvalue[i - 1]++;
                    }
                    if (left <= v && v < right) {
                        vPvalue[i - 1]++;
                        break;
                    }
                }
            }
            double xi2Pvalue = 0;
            for (int j : vPvalue) {
                xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
            }
            xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
            xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
            if (xi2Pvalue >= paramsTest.getA()) {
                testPval[9] = true;
            }

            //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
            if (testPval[9]) {
                dol[9] = (double) count / numberSample.getCountSample();
                test[9] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[9] &&
                        dol[9] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
            } else {
                test[9] = false;
            }
        } catch (OutOfMemoryError e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Окно ошибки");
            alert.setHeaderText("Переполнение памяти");
            alert.setContentText("Тест Колмогорова-Смирнова не выполнится");
            alert.show();
        }
    }

    public double kolmogorovSmirnovTest(RealDistribution distribution, double[] data) {
        int n = data.length;
        Arrays.parallelSort(data);
        double d = 0.0D;
        for (int i = 1; i <= n; ++i) {
            double yi = distribution.cumulativeProbability(data[i - 1]);
            double currD = FastMath.max(yi - (double) (i - 1) / (double) n, (double) i / (double) n - yi);
            if (currD > d) {
                d = currD;
            }
        }
        return d;
    }

    /**
     * Тест серий Nist
     */
    public void testFun11() {
        byte m = 5;
        long n = (long) numberSample.getNSample() * numberSample.getCapacity();
        long[][] v3 = new long[numberSample.getCountSample()][(int) Math.pow(2, m)];
        long[][] v2 = new long[numberSample.getCountSample()][(int) Math.pow(2, m - 1)];
        long[][] v1 = new long[numberSample.getCountSample()][(int) Math.pow(2, m - 2)];
        getVMas(m, v3);
        getVMas((byte) (m - 1), v2);
        getVMas((byte) (m - 2), v1);

        double sum32 = 0;
        double sum22 = 0;
        double sum12 = 0;
        double fi23;
        double fi22;
        double fi21;
        double[][] pValue = new double[numberSample.getCountSample()][2];
        double[] deltFi21 = new double[numberSample.getCountSample()];
        double[] deltFi22 = new double[numberSample.getCountSample()];
        int count = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < (int) Math.pow(2, m); j++) {
                sum32 += Math.pow(v3[i][j], 2);
            }
            for (int j = 0; j < (int) Math.pow(2, m - 1); j++) {
                sum22 += Math.pow(v2[i][j], 2);
            }
            for (int j = 0; j < (int) Math.pow(2, m - 2); j++) {
                sum12 += Math.pow(v1[i][j], 2);
            }
            fi23 = (Math.pow(2,m)/n) * sum32 - n;
            fi22 = (Math.pow(2,m-1)/n) * sum22 - n;
            fi21 = (Math.pow(2,m-2)/n) * sum12 - n;

            deltFi21[i] = fi23 - fi22;
            deltFi22[i] = fi23 - 2 * fi22 + fi21;

            pValue[i][0] = Gamma.regularizedGammaQ(Math.pow(2, m-2), deltFi21[i] / 2);
            pValue[i][1] = Gamma.regularizedGammaQ(Math.pow(2, m-3), deltFi22[i] / 2);

            if (paramsTest.getA() <= pValue[i][0] && paramsTest.getA() <= pValue[i][1]) {
                count++;
            }
            sum32 = 0;
            sum22 = 0;
            sum12 = 0;
        }
        int[][] vPvalue = new int[2][10];
        double left;
        double right;
        for (int j = 0; j < numberSample.getCountSample(); j++) {
            for (int i = 1; i <= 10; i++) {
                left = (double) (i - 1) / 10;
                right = (double) i / 10;
                if (i == 10 && pValue[j][0] == right) {
                    vPvalue[0][i - 1]++;
                }
                if (i == 10 && pValue[j][1] == right) {
                    vPvalue[1][i - 1]++;
                }
                if (left <= pValue[j][0] && pValue[j][0] < right) {
                    vPvalue[0][i - 1]++;
                }
                if (left <= pValue[j][1] && pValue[j][1] < right) {
                    vPvalue[1][i - 1]++;
                }
            }
        }
        double xi2Pvalue1 = 0;
        for (int j : vPvalue[0]) {
            xi2Pvalue1 += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
        }
        xi2Pvalue1 = xi2Pvalue1 / ((double) numberSample.getCountSample() / 10);
        xi2Pvalue1 = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue1 / 2);
        double xi2Pvalue2 = 0;
        for (int j : vPvalue[1]) {
            xi2Pvalue2 += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
        }
        xi2Pvalue2 = xi2Pvalue2 / ((double) numberSample.getCountSample() / 10);
        xi2Pvalue2 = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue2 / 2);
        if (xi2Pvalue1 >= paramsTest.getA() && xi2Pvalue2 >= paramsTest.getA()) {
            testPval[10] = true;
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        if (testPval[10]) {
            dol[10] = (double) count / numberSample.getCountSample();
            test[10] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[10] &&
                    dol[10] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
        } else {
            test[10] = false;
        }
    }

    public void getVMas(byte m, long[][] v) {
        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(i -> {
            byte num = 0;
            byte t = 0;
            byte saveT;
            for (int j = 0; j < numberSample.getNSample(); j++) {
                for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                    if (k >= (m - 1)) {
                        for (int l = k; l >= (k + 1 - m); l--) {
                            if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), l)) {
                                num |= 1 << (m - 1 - t);
                                t++;
                            } else {
                                t++;
                            }
                            if (t % m == 0) {
                                v[i][num]++;
                                t = 0;
                                num = 0;
                            }
                        }
                    } else {
                        for (int l = k; l >= 0; l--) {
                            if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), l)) {
                                num |= 1 << (m - 1 - t);
                                t++;
                            } else {
                                t++;
                            }
                            if (l == 0 && t % m != 0) {
                                saveT = t;
                                for (int r = (numberSample.getCapacity() - 1); r > (numberSample.getCapacity() - 1 - (m - saveT)); r--) {
                                    if (j < (numberSample.getNSample() - 1))
                                        if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j + 1), r)) {
                                            num |= 1 << (m - 1 - t);
                                            t++;
                                        } else {
                                            t++;
                                        }
                                    else {
                                        if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, 0), r)) {
                                            num |= 1 << (m - 1 - t);
                                            t++;
                                        } else {
                                            t++;
                                        }
                                    }
                                }
                            }
                            if (t % m == 0) {
                                v[i][num]++;
                                t = 0;
                                num = 0;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Универсальный статистический тест Маурера
     */
    public void testFun12() {
        long n = (long) numberSample.getNSample() * numberSample.getCapacity();
        //Инициализирующий сегмент
        int Q;
        int L;
        double expectedValue;
        double variance;
        if (n < 8080) {
            L = 1;
            Q = 20;
            expectedValue = 0.7326495;
            variance = 0.690;
        } else if (n < 24240) {
            L = 2;
            Q = 40;
            expectedValue = 1.5374383;
            variance = 1.338;
        } else if (n < 64640) {
            L = 3;
            Q = 80;
            expectedValue = 2.4016068;
            variance = 1.901;
        } else if (n < 161600) {
            L = 4;
            Q = 160;
            expectedValue = 3.3112247;
            variance = 2.358;
        } else if (n < 387840) {
            L = 5;
            Q = 320;
            expectedValue = 4.2534266;
            variance = 2.705;
        } else if (n < 904960) {
            L = 6;
            Q = 640;
            expectedValue = 5.2177052;
            variance = 2.954;
        } else if (n < 2068480) {
            L = 7;
            Q = 1280;
            expectedValue = 6.1962507;
            variance = 3.125;
        } else if (n < 4654080) {
            L = 8;
            Q = 2560;
            expectedValue = 7.1836656;
            variance = 3.238;
        } else if (n < 11342400) {
            L = 9;
            Q = 5120;
            expectedValue = 8.1764248;
            variance = 3.311;
        } else if (n < 22753280) {
            L = 10;
            Q = 10240;
            expectedValue = 9.1723243;
            variance = 3.356;
        } else if (n < 49643520) {
            L = 11;
            Q = 20480;
            expectedValue = 10.170032;
            variance = 3.384;
        } else if (n < 107560960) {
            L = 12;
            Q = 40960;
            expectedValue = 11.168765;
            variance = 3.401;
        } else if (n < 231669760) {
            L = 13;
            Q = 81920;
            expectedValue = 12.168070;
            variance = 3.410;
        } else if (n < 496435200) {
            L = 14;
            Q = 163840;
            expectedValue = 13.167693;
            variance = 3.416;
        } else if (n < 1059061760) {
            L = 15;
            Q = 327680;
            expectedValue = 14.167488;
            variance = 3.419;
        } else {
            L = 16;
            Q = 655360;
            expectedValue = 15.167379;
            variance = 3.421;
        }
        //Тестовый сегмент
        int K = (int) (n / L) - Q;
        long newNBit = Q * L + (long) K * L;
        int newNSample = (int) (newNBit / numberSample.getCapacity());
        int difBit = (int) (newNBit % numberSample.getCapacity());
        int[][] T = new int[numberSample.getCountSample()][(int) Math.pow(2, L)];
        double[] sum = new double[numberSample.getCountSample()];
        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(i -> {
            int num = 0;
            int t = 0;
            int countBlock = 0;
            for (int j = 0; j < newNSample; j++) {
                for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                    if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) {
                        num |= 1 << (L - 1 - t);
                        t++;
                    } else {
                        t++;
                    }
                    if (t % L == 0) {
                        countBlock++;
                        if (countBlock > Q) {
                            sum[i] += Math.log10(countBlock - T[i][num]) / Math.log10(2);
                        }
                        T[i][num] = countBlock;
                        t = 0;
                        num = 0;
                    }
                }
                if (difBit != 0 && j == newNSample - 1) {
                    for (int k = (numberSample.getCapacity() - 1); k > (numberSample.getCapacity() - 1 - difBit); k--) {
                        if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j + 1), k)) {
                            num |= 1 << (L - 1 - t);
                            t++;
                        } else {
                            t++;
                        }
                        if (t % L == 0) {
                            countBlock++;
                            if (countBlock > Q) {
                                sum[i] += Math.log10(countBlock - T[i][num]) / Math.log10(2);
                            }
                            T[i][num] = countBlock;
                            t = 0;
                            num = 0;
                        }
                    }
                }
            }
        });
        double[] fn = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        double z;
        double sigm = (0.7 - 0.8 / L + (4 + (double) 32 / L) * ((Math.pow(K, (double) -3 / L)) / 15)) * Math.sqrt(variance / K);
        int count = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            fn[i] = sum[i] / K;
            z = Math.abs((fn[i] - expectedValue) / (Math.sqrt(2) * sigm));
            pValue[i] = Erf.erfc(z);
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
        }
        //Анализ числа появлений значений P-value
        int[] vPvalue = new int[10];
        double left;
        double right;
        for (double value : pValue) {
            for (int i = 1; i <= vPvalue.length; i++) {
                left = (double) (i - 1) / 10;
                right = (double) i / 10;
                if (i == 10 && value == right) {
                    vPvalue[i - 1]++;
                }
                if (left <= value && value < right) {
                    vPvalue[i - 1]++;
                    break;
                }
            }
        }
        double xi2Pvalue = 0;
        for (int j : vPvalue) {
            xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
        }
        xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
        xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
        if (xi2Pvalue >= paramsTest.getA()) {
            testPval[11] = true;
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        if (testPval[11]) {
            dol[11] = (double) count / numberSample.getCountSample();
            test[11] = (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= dol[11] &&
                    dol[11] <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample());
        } else {
            test[11] = false;
        }
    }

    public double getResTests() {
        int count = 0;
        for (boolean b : test) {
            if (b) {
                count++;
            }
        }
        return (Math.round(((double) count / test.length) * 1000.0) / 1000.0) * 100;
    }

}


