package testsGenerators.statistictest;

public interface Test extends Runnable {
    void runTest();
    StringBuilder result(int count);
}
