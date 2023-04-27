package testsGenerators.statistictest;

import fr.devnied.bitlib.BytesUtils;
import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.stream.IntStream;

public class SerialTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public SerialTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
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
            fi23 = (Math.pow(2, m) / n) * sum32 - n;
            fi22 = (Math.pow(2, m - 1) / n) * sum22 - n;
            fi21 = (Math.pow(2, m - 2) / n) * sum12 - n;

            deltFi21[i] = fi23 - fi22;
            deltFi22[i] = fi23 - 2 * fi22 + fi21;

            pValue[i][0] = Gamma.regularizedGammaQ(Math.pow(2, m - 2), deltFi21[i] / 2);
            pValue[i][1] = Gamma.regularizedGammaQ(Math.pow(2, m - 3), deltFi22[i] / 2);

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
            paramsTest.getTestPval().put(getClass().getSimpleName(), true);
        } else paramsTest.getTestPval().put(getClass().getSimpleName(), false);
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        if (paramsTest.getTestPval().get(getClass().getSimpleName())) {
            paramsTest.getTests().put(getClass().getSimpleName(), (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                    paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
        }else paramsTest.getTests().put(getClass().getSimpleName(), false);
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

    @Override
    public StringBuilder result() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест 8. Тест серий битов:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}
