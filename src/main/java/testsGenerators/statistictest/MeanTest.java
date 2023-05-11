package testsGenerators.statistictest;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.List;

/*
3)	Оценка математического ожидания каждой выборки случайных чисел.
 */
public class MeanTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private double expMean;
    private final List<SynchronizedSummaryStatistics> summaryStatistics;

    public MeanTest(NumberSample numberSample, ParamsTest paramsTest, List<SynchronizedSummaryStatistics> summaryStatistics) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
        this.summaryStatistics = summaryStatistics;
    }

    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
        double[] mean = new double[numberSample.getCountSample()];
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            mean[i] = summaryStatistics.get(i).getMean();
            expMean += mean[i];
        }
        //Общее эмперическое мат. ожидание
        expMean = expMean / numberSample.getCountSample();
        //M = numberSample.getNSample(), N = n
        //теоретич. мат. ожидание
        double teorMean = 0.5;
        double otkl = (Math.abs((expMean - teorMean) / teorMean)) * 100;
        paramsTest.getDols().put(getClass().getSimpleName(), otkl);
        paramsTest.getTests().put(getClass().getSimpleName(),otkl <= 5);
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Оценка математического ожидания каждой выборки случайных чисел:\n");
        stringBuilder.append("Экспериментально измеренное математическое ожидание каждой выборки: ").append((double) Math.round(expMean * 100000.0) / 100000.0).append("\n");
        stringBuilder.append("Теоретическое математическое ожидание: " + 0.5 + "\n");
        stringBuilder
                .append("Процент отклонения рассчитанного математического ожидания от теоретического: ")
                .append((double) Math.round(paramsTest.getDols().get(getClass().getSimpleName()) * 100000.0) / 100000.0)
                .append("%").append("\n").append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}
