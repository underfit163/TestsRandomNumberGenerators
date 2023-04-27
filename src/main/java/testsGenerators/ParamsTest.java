package testsGenerators;

import java.util.HashMap;
import java.util.Map;

public class ParamsTest {
    private final Map<String,Boolean> testPval;
    private final Map<String,Double> dols;
    private final Map<String,Boolean> tests;
    private double a = 0.01;

    public ParamsTest() {
        this.testPval = new HashMap<>();
        this.dols = new HashMap<>();
        this.tests = new HashMap<>();
    }

    public Map<String, Boolean> getTestPval() {
        return testPval;
    }

    public Map<String, Double> getDols() {
        return dols;
    }

    public Map<String, Boolean> getTests() {
        return tests;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }
}
