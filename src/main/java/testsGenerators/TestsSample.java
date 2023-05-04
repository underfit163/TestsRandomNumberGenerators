package testsGenerators;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import sample.NumberSample;
import testsGenerators.graphictest.GraphicTest;
import testsGenerators.statistictest.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TestsSample {
    List<Test> tests;
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private List<SynchronizedSummaryStatistics> summaryStatistics;

    public TestsSample(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    public List<Test> getTests() {
        return tests;
    }

    public List<SynchronizedSummaryStatistics> getSummaryStatistics() {
        return summaryStatistics;
    }

    //-------------------------------------------------------------------------------------------------------------------
    private void initSummaryStatistics() {
        summaryStatistics = new ArrayList<>();
        SynchronizedSummaryStatistics statistics;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            summaryStatistics.add(new SynchronizedSummaryStatistics());
            statistics = summaryStatistics.get(i);
            for (int j = 0; j < numberSample.getNSample(); j++) {
                statistics.addValue((double) numberSample.getItemSample(i, j) / numberSample.getMaxNumber());
            }
        }
    }

    public void initParams(){
        numberSample.getBitSets();
        initSummaryStatistics();
    }

    public void runStatisticTest(List<Test> testsList) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<?>> futures = testsList.stream().map(executorService::submit).collect(Collectors.toList());
        executorService.shutdown();
        for (var future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void runGraphicTest(List<GraphicTest> testsList) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<?>> futures = testsList.stream().map(executorService::submit).collect(Collectors.toList());
        executorService.shutdown();
        for (var future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public double getResTests() {
        int count = 0;
        for (boolean b : paramsTest.getTests().values()) {
            if (b) {
                count++;
            }
        }
        return (Math.round(((double) count / paramsTest.getTests().size()) * 1000.0) / 1000.0) * 100;
    }

}


