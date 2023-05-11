package testsGenerators.statistictest;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * 11)	Проверка аппроксимированной энтропии (Approximate entropy test).
 */
public class ApproximateEntropyTest implements Test {
    private final double[][] ApEn;
    private double apen;
    private final int m;
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private double[] pValue;

    /**
     * @param m длина каждого блока (пересекающиеся серии длиной m)
     */
    public ApproximateEntropyTest(NumberSample numberSample, ParamsTest paramsTest, int m) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
        this.m = m;
        this.ApEn = new double[numberSample.getCountSample()][2];
    }

    @Override
    public void runTest() {
        int seqLength = numberSample.getBitSetList().get(0).length();
        double[] chiSquared = new double[numberSample.getCountSample()];
        pValue = new double[numberSample.getCountSample()];

        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(t -> {
            int r = 0;
            for (int blockSize = m; blockSize <= m + 1; blockSize++) {
                if (blockSize == 0) {
                    ApEn[t][0] = 0;
                    r++;
                } else {
                    int powLen = (int) Math.pow(2, blockSize);
                    int[] P = new int[powLen];

                    for (int i = 0; i < (double) seqLength; i++) {
                        /* COMPUTE FREQUENCY */
                        int k = 1;
                        for (int j = 0; j < blockSize; j++) {
                            k <<= 1;
                            if (numberSample.getBitSetList().get(t).get((i + j) % seqLength)) {
                                k++;
                            }
                        }
                        P[(int) (k - Math.pow(2, blockSize))]++;
                    }
                    /* DISPLAY FREQUENCY */
                    double sum = 0;
                    for (int i = 0; i < (int) Math.pow(2, blockSize); i++) {
                        if (P[i] > 0) {
                            sum += P[i] * Math.log(P[i] / (double) seqLength);
                        }
                    }
                    sum /= seqLength;
                    ApEn[t][r] = sum;
                    r++;
                }
            }
            apen = ApEn[t][0] - ApEn[t][1];
            chiSquared[t] = 2 * seqLength * (Math.log(2) - apen);
            pValue[t] = Gamma.regularizedGammaQ(Math.pow(2, m - 1), chiSquared[t] / 2);
        });

        int count = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
        }
        KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
        Arrays.sort(pValue);
        double xi2Pvalue = ksTest.kolmogorovSmirnovTest(new UniformRealDistribution(0, 1), pValue);
//        //Анализ числа появлений значений P-value
//        if(xi2Pvalue) {
//            int[] vPvalue = new int[10];
//            double left;
//            double right;
//            for (double value : pValue) {
//                for (int i = 1; i <= vPvalue.length; i++) {
//                    left = (double) (i - 1) / 10;
//                    right = (double) i / 10;
//                    if (i == 10 && value == right) {
//                        vPvalue[i - 1]++;
//                    }
//                    if (left <= value && value < right) {
//                        vPvalue[i - 1]++;
//                        break;
//                    }
//                }
//            }
//            for (int j : vPvalue) {
//                xi2Pvalue += Math.pow(j - (double) numberSample.getCountSample() / 10, 2);
//            }
//            xi2Pvalue = xi2Pvalue / ((double) numberSample.getCountSample() / 10);
//            xi2Pvalue = Gamma.regularizedGammaQ((double) (10 - 1) / 2, xi2Pvalue / 2);
//        }
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

    public StringBuilder resultTest() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Параметры теста: ").append("\n")
                .append("   Длина последовательности бит: ").append(numberSample.getBitSetList().get(0).length()).append("\n")
                .append("   Длина блока: ").append(m).append("\n");
        stringBuilder
                .append("Значения p-value последовательностей: ").append("\n")
                .append(Arrays.toString(Arrays.stream(pValue).sorted().mapToObj(x -> String.format("%.3f", x)).toArray())).append("\n")
                .append("должны быть больше ").append(paramsTest.getA()).append("\n");
        return stringBuilder;
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Проверка аппроксимированной энтропии:\n");
        stringBuilder.append(resultTest()).append("\n");
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
