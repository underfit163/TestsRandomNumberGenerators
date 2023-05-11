package testsGenerators.statistictest;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;

/*
2)	Проверка гипотезы равномерного распределения случайной величины с помощью доверительного интервала.
 */
public class UniformDistributionChiSquareTest extends UniformDistributionTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private int n;
    private double len;
    private double[] pValue;

    public UniformDistributionChiSquareTest(NumberSample numberSample, ParamsTest paramsTest) {
        super(numberSample);
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void runTest() {
        //число интервалов
        n = 10;
        double[] xi2 = new double[numberSample.getCountSample()];
        pValue = new double[numberSample.getCountSample()];
        int count = 0;
        //длина интервала
        len = (double) 1 / n;
        initFrequency(n, len);

        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < n; j++) {
                xi2[i] += Math.pow(getFrequencyCountDouble()[i][j] - (double) numberSample.getNSample() / n, 2) / ((double) numberSample.getNSample() / n);
            }
            pValue[i] = Gamma.regularizedGammaQ((double) (n - 1) / 2, xi2[i] / 2);
            if (paramsTest.getA() <= pValue[i] && pValue[i] <= 1 - paramsTest.getA()) {
                count++;
            }
        }
        KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
        Arrays.sort(pValue);
        double xi2Pvalue = ksTest.kolmogorovSmirnovTest(new UniformRealDistribution(0, 1), pValue);
//        //Анализ числа появлений значений P-value
//        if(xi2Pvalue) {
//            int[] vPvalue = new int[10];
//            double left;
//            double right;
//            for (double value : pValue) {
//                for (int i = 1; i <= vPvalue.length; i++) {
//                    left = (double) (i - 1) / 10;
//                    right = (double) i / 10;
//                    if (i == 10 && value == right) {
//                        vPvalue[i - 1]++;
//                    }
//                    if (left <= value && value < right) {
//                        vPvalue[i - 1]++;
//                        break;
//                    }
//                }
//            }
//            for (int j : vPvalue) {
//                xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
//            }
//            xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
//            xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
//        }
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

    public StringBuilder resultTest() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Параметры теста: ").append("\n")
                .append("   Длина последовательности чисел: ").append(numberSample.getCountSample()).append("\n")
                .append("   Длина интервала: ").append(len).append("\n")
                .append("   Количество интервалов: ").append(n).append("\n");
        stringBuilder
                .append("Значения p-value последовательностей: ").append("\n")
                .append(Arrays.toString(Arrays.stream(pValue).sorted().mapToObj(x -> String.format("%.3f", x)).toArray())).append("\n")
                .append("должны быть больше ").append(paramsTest.getA()).append("\n");
        return stringBuilder;
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Проверка гипотезы равномерного распределения случайной величины с помощью критерия хи-квадрат:\n");
        stringBuilder.append(resultTest()).append("\n");
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
