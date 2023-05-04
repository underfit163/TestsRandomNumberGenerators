package testsGenerators.statistictest;

import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;

/**
 * 19)	Тест «стопка книг».
 */
public class StackBooksTest implements Test {

    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private int k;
    private final int N;

    public StackBooksTest(NumberSample numberSample, ParamsTest paramsTest, int N) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
        this.N = N;
    }

    @Override
    public void runTest() {
        k = (int) (Math.pow(2, 8) / N);
        double[] chiSquared = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            int[] A = new int[N];
            int[] list = new int[(int) Math.pow(2, 8)];
            for (int j = 0; j < list.length; j++) {
                list[j] = j;
            }
            int[] input = toPositiveBytes(numberSample.getBitSetList().get(i).toByteArray());
            mtfEncode(input, A, list);

            for (int j = 0; j < N; j++) {
                chiSquared[i] += Math.pow(A[j] - (double) input.length / N, 2) / ((double) input.length / N);
            }
            pValue[i] = Gamma.regularizedGammaQ((double) (N - 1) / 2, chiSquared[i] / 2);
        }
        int count = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
        }
        //Анализ числа появлений значений P-value
        int[] vPvalue = new int[10];
        double left;
        double right;
        for (double value : pValue) {
            for (int i = 1; i <= vPvalue.length; i++) {
                left = (double) (i - 1) / 10;
                right = (double) i / 10;
                if (i == 10 && value == right) {
                    vPvalue[i - 1]++;
                }
                if (left <= value && value < right) {
                    vPvalue[i - 1]++;
                    break;
                }
            }
        }
        double xi2Pvalue = 0;
        for (int j : vPvalue) {
            xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
        }
        xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
        xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
        paramsTest.getTestPval().put(getClass().getSimpleName(), false);
        if (xi2Pvalue >= paramsTest.getA()) {
            paramsTest.getTestPval().put(getClass().getSimpleName(), true);
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        if (paramsTest.getTestPval().get(getClass().getSimpleName())) {
            paramsTest.getTests().put(getClass().getSimpleName(), (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                    paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
        } else paramsTest.getTests().put(getClass().getSimpleName(), false);
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

    private void mtfEncode(int[] input, int[] A, int[] list) {
        int outputIndex;
        for (int j : input) {
            outputIndex = search(j, list);
            insertToInterval(A, outputIndex);
            moveToFront(outputIndex, list);
        }
    }

    static void moveToFront(int currIndex, int[] list) {
        int val = list[currIndex];
        int[] record = Arrays.copyOf(list, currIndex);
        System.arraycopy(record, 0, list, 1, currIndex);
        list[0] = val;
    }

    private int search(int inputVal, int[] list) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == inputVal) {
                return i;
            }
        }
        return -1;
    }

    private void insertToInterval(int[] A, int val) {
        for (int i = 0; i < N; i++) {
            if ((i * k <= val) && (val < (i + 1) * k)) {
                A[i]++;
            }
        }
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Стопка книг:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }

    @Override
    public void run() {
        runTest();
    }
}
