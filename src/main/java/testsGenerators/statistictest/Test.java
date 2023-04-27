package testsGenerators.statistictest;

import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

public interface Test extends Runnable {
    void runTest();
    StringBuilder result();
}
