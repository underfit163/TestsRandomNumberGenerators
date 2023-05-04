package testsGenerators.graphictest;

import sample.NumberSample;

import java.util.HashMap;
import java.util.Map;

public class MonotonyTest implements GraphicTest {

    private final NumberSample numberSample;
    private final Map<Integer, Integer> monotonySeries;

    public MonotonyTest(NumberSample numberSample) {
        this.numberSample = numberSample;
        this.monotonySeries = new HashMap<>();
    }

    public Map<Integer, Integer> getMonotonySeries() {
        return monotonySeries;
    }

    @Override
    public void runTest() {
        boolean increasing;
        int t = 1;
        increasing = numberSample.getItemSample(0, 0) <= numberSample.getItemSample(0, 1);
        monotonySeries.put(t, 2);
        for (int i = 2; i < numberSample.getNSample(); i++) {
            if (numberSample.getItemSample(0, i - 1) < numberSample.getItemSample(0, i)) {
                if (increasing) {
                    monotonySeries.put(t, monotonySeries.get(t) + 1);
                } else {
                    increasing = true;
                    monotonySeries.put(++t, 1);
                }
            } else if (numberSample.getItemSample(0, i - 1) > numberSample.getItemSample(0, i)) {
                if (!increasing) {
                    monotonySeries.put(t, monotonySeries.get(t) + 1);
                } else {
                    increasing = false;
                    monotonySeries.put(++t, 1);
                }
            } else {
                monotonySeries.put(t, monotonySeries.get(t) + 1);
            }
        }
    }

    @Override
    public void run() {
        runTest();
    }
}
