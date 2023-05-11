package testsGenerators.statistictest;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;

/*
14)	Частотный тест в подпоследовательностях (Frequency test with in a block).
 */
public class FrequencyBlockTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private final int blockLen;
    private double[] pValue;
    private int blockCount;

    public FrequencyBlockTest(NumberSample numberSample, ParamsTest paramsTest, int blockLen) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
        this.blockLen = blockLen;
    }

    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
        long n = numberSample.getBitSetList().get(0).length();
        double[] xi2 = new double[numberSample.getCountSample()];
        pValue = new double[numberSample.getCountSample()];
        blockCount = (int) (n / blockLen);
        double[][] sum = new double[numberSample.getCountSample()][blockCount];

        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(i -> Arrays.parallelSetAll(sum[i], t -> getCountBit(t, numberSample.getBitSetList().get(i))));
        int count = 0;
        double p = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < blockCount; j++) {
                p += Math.pow(sum[i][j] - 0.5, 2);
            }
            xi2[i] = 4 * blockLen * p;
            pValue[i] = Gamma.regularizedGammaQ((double) blockCount / 2, xi2[i] / 2);
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
            p = 0;
        }
        KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
        Arrays.sort(pValue);
        double xi2Pvalue = ksTest.kolmogorovSmirnovTest(new UniformRealDistribution(0, 1), pValue);
//        //Анализ числа появлений значений P-value
//        int[] vPvalue = new int[10];
//        double left;
//        double right;
//        for (double value : pValue) {
//            for (int i = 1; i <= vPvalue.length; i++) {
//                left = (double) (i - 1) / 10;
//                right = (double) i / 10;
//                if (i == 10 && value == right) {
//                    vPvalue[i - 1]++;
//                }
//                if (left <= value && value < right) {
//                    vPvalue[i - 1]++;
//                    break;
//                }
//            }
//        }
//        double xi2Pvalue = 0;
//        for (int j : vPvalue) {
//            xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
//        }
//        xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
//        xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
        paramsTest.getTestPval().put(getClass().getSimpleName(), false);
        if (xi2Pvalue >= paramsTest.getA()) {
            paramsTest.getTestPval().put(getClass().getSimpleName(), true);
        }        paramsTest.getTestPval().put(getClass().getSimpleName(), false);
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

    public double getCountBit(int t, BitSet bitSet) {
        int blockSum = 0;
        int offset = t * blockLen;
            for (int j = 0; j < blockLen; j++) {
                blockSum += bitSet.get(offset + j)? 1 : 0;
            }
        return (double) blockSum / (double) blockLen;
    }

    public StringBuilder resultTest() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Параметры теста: ").append("\n")
                .append("   Длина последовательности бит: ")
                .append(numberSample.getBitSetList().get(0).length()).append("\n")
                .append("   Длина блока: ").append(blockLen).append("\n")
                .append("   Количество блоков: ").append(blockCount).append("\n");
        stringBuilder
                .append("Значения p-value последовательностей: ").append("\n")
                .append(Arrays.toString(Arrays.stream(pValue).sorted().mapToObj(x -> String.format("%.3f", x)).toArray())).append("\n")
                .append("должны быть больше ").append(paramsTest.getA()).append("\n");
        return stringBuilder;
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Частотный тест в подпоследовательностях:\n");
        stringBuilder.append(resultTest()).append("\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}
