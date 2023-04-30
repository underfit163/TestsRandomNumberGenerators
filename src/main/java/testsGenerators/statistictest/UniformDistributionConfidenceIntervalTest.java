package testsGenerators.statistictest;

import sample.NumberSample;
import testsGenerators.ParamsTest;

/*
1) Проверка гипотезы равномерного распределения случайной величины с помощью доверительный интервал.
 */
public class UniformDistributionConfidenceIntervalTest extends UniformDistributionTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public UniformDistributionConfidenceIntervalTest(NumberSample numberSample, ParamsTest paramsTest) {
        super(numberSample);
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void runTest() {
        //число интервалов
        int n = 10;
        //длина интервала
        double len = (double) 1 / n;

        initFrequency(n, len);
        // проводим первый тест:
        double p;
       /* double p90left = 1 / (n + (1 / (2 * Math.sqrt(3))) * (n - 1));
        double p90right = 1 / (n - (1 / (2 * Math.sqrt(3))) * (n - 1));*/
        double p90left = (numberSample.getNSample() - 2.58 * Math.sqrt(numberSample.getNSample() * (n - 1))) / n;
        double p90right = (numberSample.getNSample() + 2.58 * Math.sqrt(numberSample.getNSample() * (n - 1))) / n;

        boolean[] masTest1 = new boolean[numberSample.getCountSample()];
        int count = 0;
        int count1 = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < n; j++) {
                p = getFrequencyCountDouble()[i][j];
                if (p90left <= p && p <= p90right) {
                    count1++;
                }
            }
            if ((double) count1 / n >= 0.9) masTest1[i] = true;
            if (masTest1[i]) {
                count++;
            }
            count1 = 0;
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        paramsTest.getTests().put(getClass().getSimpleName(),
                (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                        paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Проверка гипотезы равномерного распределения случайной величины с помощью интервалов:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ")
                .append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
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
