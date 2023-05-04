package testsGenerators.statistictest;

import org.apache.commons.math3.special.Gamma;
import sample.IntMatrix;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.stream.IntStream;

/**
 * 13)	Проверка рангов матриц (Binary matrix rank test).
 */
public class RankTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private final int r;

    public RankTest(NumberSample numberSample, ParamsTest paramsTest, int r) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
        this.r = r;
    }


    @Override
    public void runTest() {
        double[] chiSquared = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        double p_r2;
        double p_r1;
        double p_r0;

        int length = numberSample.getBitSetList().get(0).length();
        if (length < r * r) {
            length = r * r;
        }
        int N = length / (r * r);

        /* COMPUTE PROBABILITIES */
        double product = 1;
        for (int i = 0; i <= r - 1; i++) {
            product *= ((1.e0 - Math.pow(2.0, i - r)) * (1.e0 - Math.pow(2.0, i - r))) / (1.e0 - Math.pow(2.0, (double) i - r));
        }
        p_r2 = Math.pow(2.0, (double) r * (r + r - r) - r * r) * product;

        int r1 = r - 1;
        product = 1;
        for (int i = 0; i <= r1 - 1; i++) {
            product *= ((1.e0 - Math.pow(2.0, i - r)) * (1.e0 - Math.pow(2.0, i - r))) / (1.e0 - Math.pow(2.0, (double) i - r1));
        }
        p_r1 = Math.pow(2.0, (double) r1 * (r + r - r1) - r * r) * product;

        p_r0 = 1 - (p_r2 + p_r1);

        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(i -> {
            double R;
            IntMatrix matrix = new IntMatrix(r, r);
            int F_r2 = 0;
            int F_r1 = 0;
            for (int k = 0; k < N; k++) {
                /* FOR EACH 32x32 MATRIX   */
                matrix.assignFromBits(numberSample.getBitSetList().get(i), k * r * r);
                R = matrix.computeRank();
                if (R == r) {
                    F_r2++;
                }
                if (R == r1) {
                    F_r1++;
                }
            }

            int F_r0 = N - (F_r2 + F_r1);
            chiSquared[i] = (Math.pow(F_r2 - N * p_r2, 2) / (N * p_r2)
                    + Math.pow(F_r1 - N * p_r1, 2) / (N * p_r1)
                    + Math.pow(F_r0 - N * p_r0, 2) / (N * p_r0));
            pValue[i] = Gamma.regularizedGammaQ(1, chiSquared[i] / 2);
        });

        int count = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
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
        paramsTest.getTestPval().put(getClass().getSimpleName(), false);
        if (xi2Pvalue >= paramsTest.getA()) {
            paramsTest.getTestPval().put(getClass().getSimpleName(), true);
        }
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
        stringBuilder.append("Тест ").append(count).append(". Тест рангов матриц:\n");
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
