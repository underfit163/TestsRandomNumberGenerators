package testsGenerators.graphictest;

import sample.NumberSample;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class AutocorrelationTest implements GraphicTest {
    private final NumberSample numberSample;
    private final Map<Integer, Double> autocorrelationSeries;

    public AutocorrelationTest(NumberSample numberSample) {
        this.numberSample = numberSample;
        this.autocorrelationSeries = new HashMap<>();
    }

    public Map<Integer, Double> getAutocorrelationSeries() {
        return autocorrelationSeries;
    }

    @Override
    public void runTest() {
        BitSet bitSet = numberSample.getBitSetList().get(0);
        int len = Math.min(bitSet.length(), 1000);
        IntStream.range(0, len + 1).parallel().forEach(j -> {
            int cj = 0;
            for (int i = 0; i < bitSet.length(); i++) {
                cj += (bitSet.get(i) ? 1 : -1) * (bitSet.get((i + j) % bitSet.length()) ? 1 : -1);
            }
            autocorrelationSeries.put(j + 1, ((double) cj / bitSet.length()));
        });
    }

    @Override
    public void run() {
        runTest();
    }
}
