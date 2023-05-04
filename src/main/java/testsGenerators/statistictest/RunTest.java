package testsGenerators.statistictest;

import fr.devnied.bitlib.BytesUtils;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;

/*
5) Проверка на равномерность битов(Тест подпоследовательностей)(RunTest)
 */
public class RunTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public RunTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
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
        paramsTest.getTestPval().put(getClass().getSimpleName(), false);
        if (xi2Pvalue >= paramsTest.getA()) {
            paramsTest.getTestPval().put(getClass().getSimpleName(), true);
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        if (paramsTest.getTestPval().get(getClass().getSimpleName())) {
            paramsTest.getTests().put(getClass().getSimpleName(), (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                    paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
        }else paramsTest.getTests().put(getClass().getSimpleName(), false);
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

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Тест подпоследовательностей битов:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}
