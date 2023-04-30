package testsGenerators.statistictest;

import fr.devnied.bitlib.BytesUtils;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;

/*
7) Частотный тест на длинные последовательности
 */
public class FrequencyLongSequencesTest implements Test {

    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public FrequencyLongSequencesTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }


    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
        int[][] ret = new int[2][numberSample.getCountSample()];
        if (numberSample.getCountSample() > 1) {
            for (int k = 0; k < 2; k++) {
                int finalK = k;
                Arrays.parallelSetAll(ret[k], i -> getMaxCountBit(i, finalK));
            }
        } else {
            int len1 = 0;
            int len0 = 0;
            for (int i = 0; i < numberSample.getCountSample(); i++) {
                for (int j = 0; j < numberSample.getNSample(); j++) {
                    for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                        if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) {
                            len1++;
                            len0 = 0;
                        } else {
                            len1 = 0;
                            len0++;
                        }
                        ret[1][i] = Math.max(ret[1][i], len1);
                        ret[0][i] = Math.max(ret[0][i], len0);
                    }
                }
                len1 = 0;
                len0 = 0;
            }
        }
        int maxCountBit = 34;
        int countTrue = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            if (ret[1][i] < maxCountBit && ret[0][i] < maxCountBit) {
                countTrue++;
            }
        }
        paramsTest.getDols().put(getClass().getSimpleName(), (double) countTrue / numberSample.getCountSample());
        paramsTest.getTests().put(getClass().getSimpleName(),(1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
    }

    public int getMaxCountBit(int i, int t) {
        int ret = 0;
        int len = 0;
        for (int j = 0; j < numberSample.getNSample(); j++) {
            for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) {
                    if (t == 0) {
                        len = 0;
                    } else len++;
                } else {
                    if (t == 0) {
                        len++;
                    } else len = 0;
                }
                ret = Math.max(ret, len);
            }
        }
        return ret;
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Частотный тест на длинные последовательности:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}
