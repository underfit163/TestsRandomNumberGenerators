package testsGenerators.statistictest;

import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

/**
 * 15)	Проверка случайных отклонений (Random excursion test).
 */
public class RandomExcursionsTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private static final int[] stateX = {-9, -8, -7, -6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public RandomExcursionsTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void runTest() {
        double[][] pValue = new double[numberSample.getCountSample()][stateX.length];
        int length = numberSample.getBitSetList().get(0).length();
        int[] S_k = new int[length];

        for (int j = 0; j < numberSample.getCountSample(); j++) {
            int J = 0;
            S_k[0] = 2 * (numberSample.getBitSetList().get(j).get(0) ? 1 : 0) - 1;
            for (int i = 1; i < length; i++) {
                S_k[i] = S_k[i - 1] + 2 * (numberSample.getBitSetList().get(j).get(i) ? 1 : 0) - 1;
                if (S_k[i] == 0) {
                    J++;
                }
            }
            if (S_k[length - 1] != 0) {
                J++;
            }

            for (int p = 0; p < stateX.length; p++) {
                int x = stateX[p];
                int count = 0;
                for (int i = 0; i < length; i++) {
                    if (S_k[i] == x) {
                        count++;
                    }
                }
                pValue[j][p] = Erf.erfc(Math.abs(count - J) / (Math.sqrt(2.0 * J * (4.0 * Math.abs(x) - 2))));
            }
        }
        int count = 0;
        int otherCount;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            otherCount = 0;
            for (int j = 0; j < stateX.length; j++) {
                if (paramsTest.getA() <= pValue[i][j]) {
                    otherCount++;
                } else break;
                if (otherCount == stateX.length) count++;
            }
        }
        //Анализ числа появлений значений P-value
        int[] vPvalue = new int[10];
        double left;
        double right;
        double value;
        for (int k = 0; k < numberSample.getCountSample(); k++) {
            value = pValue[k][0];
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
        } else paramsTest.getTests().put(getClass().getSimpleName(), false);
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Проверка случайных отклонений:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }

    @Override
    public void run() {
        runTest();
    }
}
