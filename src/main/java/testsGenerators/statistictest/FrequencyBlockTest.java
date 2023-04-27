package testsGenerators.statistictest;

import fr.devnied.bitlib.BytesUtils;
import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;

/*
14)	Частотный тест в подпоследовательностях (Frequency test with in a block).
 */
public class FrequencyBlockTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public FrequencyBlockTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
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
            paramsTest.getTestPval().put(getClass().getSimpleName(), true);
        } else paramsTest.getTestPval().put(getClass().getSimpleName(), false);
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        if (paramsTest.getTestPval().get(getClass().getSimpleName())) {
            paramsTest.getTests().put(getClass().getSimpleName(), (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                    paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
        } else paramsTest.getTests().put(getClass().getSimpleName(), false);
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

    @Override
    public StringBuilder result() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест 14. Частотный тест в подпоследовательностях:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}
