package testsGenerators.graphictest;

import sample.NumberSample;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class SeriesGraphicTest implements GraphicTest {
    private final NumberSample numberSample;
    private Map<String, Integer> mapV1;
    private Map<String, Integer> mapV2;
    private Map<String, Integer> mapV3;

    public SeriesGraphicTest(NumberSample numberSample) {
        this.numberSample = numberSample;
    }

    public Map<String, Integer> getMapV1() {
        return mapV1;
    }

    public Map<String, Integer> getMapV2() {
        return mapV2;
    }

    public Map<String, Integer> getMapV3() {
        return mapV3;
    }

    @Override
    public void runTest() {
        int m = 3;
        int[] v3 = new int[(int) Math.pow(2, m)];
        int[] v2 = new int[(int) Math.pow(2, m - 1)];
        int[] v1 = new int[(int) Math.pow(2, m - 2)];
        getVMas(v3, m);
        getVMas(v2, m - 1);
        getVMas(v1, m - 2);

        mapV1 = new HashMap<>();
        mapV2 = new HashMap<>();
        mapV3 = new HashMap<>();
        for (int i = 0; i < v1.length; i++) {
            mapV1.put(String.format("%1s", Integer.toBinaryString(i)).replace(' ', '0'), v1[i]);
        }
        for (int i = 0; i < v2.length; i++) {
            mapV2.put(String.format("%2s", Integer.toBinaryString(i)).replace(' ', '0'), v2[i]);
        }
        for (int i = 0; i < v3.length; i++) {
            mapV3.put(String.format("%3s", Integer.toBinaryString(i)).replace(' ', '0'), v3[i]);
        }
    }

    private void getVMas(int[] v, int t) {
        BitSet bitSet = numberSample.getBitSetList().get(0);
        for (int i = 0; i < bitSet.length(); i++) {
            int k = 1;
            for (int j = 0; j < t; j++) {
                if (!bitSet.get((i + j) % bitSet.length())) {
                    k <<= 1;
                } else {
                    k = (k << 1) + 1;
                }
            }
            v[k - ((int) Math.pow(2, t))]++;
            i += (t - 1);
        }
    }

    @Override
    public void run() {
        runTest();
    }
}
