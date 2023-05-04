package testsGenerators.statistictest;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.stream.IntStream;

/**
 * 4)	Проверка кумулятивных сумм (Cumulative sums (Cusum) test).
 */
public class CumulativeSumsTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private final NormalDistribution nd = new NormalDistribution();

    public CumulativeSumsTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void runTest() {
        long n = numberSample.getBitSetList().get(0).length();
        //if (n > 1_000_000) n = 1_000_000;
        int[] z = new int[numberSample.getCountSample()];
        int[] zrev = new int[numberSample.getCountSample()];
        double sqrtN = Math.sqrt(n);

        int[] S = new int[numberSample.getCountSample()];
        int[] sup = new int[numberSample.getCountSample()];
        int[] inf = new int[numberSample.getCountSample()];
        int[] k = new int[numberSample.getCountSample()];
        double[] sum1 = new double[numberSample.getCountSample()];
        double[] sum2 = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(i -> {
            for (int j = 0; j < n; j++) {
                if (numberSample.getBitSetList().get(i).get(j)) S[i]++;
                else S[i]--;
                if (S[i] > sup[i]) sup[i]++;
                if (S[i] < inf[i]) inf[i]--;
                z[i] = Math.max(sup[i], -inf[i]);
                zrev[i] = Math.max((sup[i] - S[i]), (S[i] - inf[i]));
            }
            for (k[i] = (int) ((-n / z[i] + 1) / 4); k[i] <= (n / z[i] - 1) / 4; k[i]++) {
                sum1[i] += nd.cumulativeProbability(((4 * k[i] + 1) * z[i]) / sqrtN);
                sum1[i] -= nd.cumulativeProbability(((4 * k[i] - 1) * z[i]) / sqrtN);
            }
            for (k[i] = (int) ((-n / zrev[i] - 3) / 4); k[i] <= (n / zrev[i] - 1) / 4; k[i]++) {
                sum2[i] += nd.cumulativeProbability(((4 * k[i] + 3) * zrev[i]) / sqrtN);
                sum2[i] -= nd.cumulativeProbability(((4 * k[i] + 1) * zrev[i]) / sqrtN);
            }
            pValue[i] = 1.0 - sum1[i] + sum2[i];
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
        stringBuilder.append("Тест ").append(count).append(". Проверка кумулятивных сумм:\n");
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
