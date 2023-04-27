package testsGenerators.statistictest;

import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

/*
2)	Проверка гипотезы равномерного распределения случайной величины с помощью доверительного интервала.
 */
public class UniformDistributionChiSquareTest extends UniformDistributionTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public UniformDistributionChiSquareTest(NumberSample numberSample, ParamsTest paramsTest) {
        super(numberSample);
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void runTest() {
        //число интервалов
        int n = 10;
        double[] xi2 = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        int count = 0;
        //длина интервала
        double len = (double) 1 / n;
        initFrequency(n, len);

        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < n; j++) {
                xi2[i] += Math.pow(getFrequencyCountDouble()[i][j] - (double) numberSample.getNSample() / n, 2) / ((double) numberSample.getNSample() / n);
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
            paramsTest.getTestPval().put(getClass().getSimpleName(), true);
        } else paramsTest.getTestPval().put(getClass().getSimpleName(), false);
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        if (paramsTest.getTestPval().get(getClass().getSimpleName())) {
            paramsTest.getTests().put(getClass().getSimpleName(), (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                    paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
        }
    }

    @Override
    public StringBuilder result() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест 2. Проверка гипотезы равномерного распределения случайной величины с помощью критерия хи-квадрат:\n");
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
