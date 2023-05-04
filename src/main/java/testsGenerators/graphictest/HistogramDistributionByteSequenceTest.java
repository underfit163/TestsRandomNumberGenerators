package testsGenerators.graphictest;

import sample.NumberSample;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HistogramDistributionByteSequenceTest implements GraphicTest {
    private final NumberSample numberSample;
    private Map<Integer, Integer> heights;

    public Map<Integer, Integer> getHeights() {
        return heights;
    }

    public HistogramDistributionByteSequenceTest(NumberSample numberSample) {
        this.numberSample = numberSample;
        heights = new HashMap<>();
        for (int i = 0; i <= Math.pow(2, 8) - 1; i++) {
            heights.put(i, 0);
        }
    }

    @Override
    public void runTest() {
        int[] input = toPositiveBytes(numberSample.getBitSetList().get(0).toByteArray());
        Arrays.stream(input).forEach(x -> heights.put(x, heights.get(x) + 1));
    }

    private int[] toPositiveBytes(byte[] bytes) {
        int[] positiveBytes = new int[bytes.length];
        int i = 0;
        for (byte b : bytes) {
            int positiveValue = b & 0xFF;
            positiveBytes[i++] = positiveValue;
        }
        return positiveBytes;
    }

    @Override
    public void run() {
        runTest();
    }
}
