package testsGenerators.statistictest;

import fr.devnied.bitlib.BytesUtils;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.stream.IntStream;

public class MaurerUniversalStatisticalTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public MaurerUniversalStatisticalTest(NumberSample numberSample, ParamsTest paramsTest) {
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
            paramsTest.getTestPval().put(getClass().getSimpleName(), true);
        } else paramsTest.getTestPval().put(getClass().getSimpleName(), false);
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        if (paramsTest.getTestPval().get(getClass().getSimpleName())) {
            paramsTest.getTests().put(getClass().getSimpleName(), (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                    paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
        }else paramsTest.getTests().put(getClass().getSimpleName(), false);
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Универсальный статистический тест Маурера:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}
