package testsGenerators.statistictest;

import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.CombinatoricsUtils;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;
import java.util.stream.IntStream;

public class PokerTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public PokerTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
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
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        paramsTest.getTests().put(getClass().getSimpleName(), (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
    }

    @Override
    public StringBuilder result() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест 20. Покер-тест:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}
