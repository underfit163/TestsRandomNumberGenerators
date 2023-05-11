package testsGenerators.statistictest;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import sample.AperiodicTemplate;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * 18)	Проверка непересекающихся шаблонов (Non-overlapping template matching test).
 */
public class NonOverlappingTemplateMatchingTest implements Test {

    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private int N;
    private final int m;
    private int M;
    double[] arrPVal;
    private static final int MAXNUMOFTEMPLATES = 148;


    /**
     * @param templateLength размер шаблона в битах
     * @param N              количество не пересекающихся битовых последовательностей
     */
    public NonOverlappingTemplateMatchingTest(NumberSample numberSample, ParamsTest paramsTest, int templateLength, int N) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
        this.m = templateLength;
        this.N = N;
    }

    @Override
    public void runTest() {
        int length = numberSample.getBitSetList().get(0).length();
        M = length / N;//количество бит в одной последовательности

        double lambda = (M - m + 1) / Math.pow(2, m);//мат. ожидание
        double varWj = M * (1.0 / Math.pow(2.0, m) - (2.0 * m - 1.0) / Math.pow(2.0, 2.0 * m));//gamma
        AperiodicTemplate at = new AperiodicTemplate(m);
        int numOfTemplates = at.getCount();
        if ((lambda < 0) || (lambda == 0)) {
            throw new IllegalArgumentException("lambda not positive");
        }
        int SKIP;
        if (numOfTemplates < MAXNUMOFTEMPLATES) {
            SKIP = 1;
        } else {
            SKIP = numOfTemplates / MAXNUMOFTEMPLATES;
        }
        numOfTemplates = numOfTemplates / SKIP;

        int numberOfTests = Math.min(MAXNUMOFTEMPLATES, numOfTemplates);
        double[][] chiSquared = new double[numberSample.getCountSample()][numberOfTests];
        double[][] pValue = new double[numberSample.getCountSample()][numberOfTests];

        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(t -> {
            Iterator<Long> I = at.iterator();
            int[] Wj = new int[N];
            for (int jj = 0; jj < numberOfTests; jj++) {
                Long seq = I.next();
                for (int i = 0; i < N; i++) {
                    int W_obs = 0;
                    for (int j = 0; j < M - m + 1; j++) {
                        int match = 1;
                        for (int k = 0; k < m; k++) {
                            if ((int) ((seq >> (m - k - 1)) & 0x1) != (numberSample.getBitSetList().get(t).get(i * M + j + k) ? 1 : 0)) {
                                match = 0;
                                break;
                            }
                        }
                        if (match == 1) {
                            W_obs++;
                            j += m - 1;
                        }
                    }
                    Wj[i] = W_obs;
                }
                chiSquared[t][jj] = 0;
                for (int i = 0; i < N; i++) {
                    chiSquared[t][jj] += Math.pow(((double) Wj[i] - lambda) / Math.pow(varWj, 0.5), 2);
                }
                pValue[t][jj] = Gamma.regularizedGammaQ(N / 2.0, chiSquared[t][jj] / 2.0);

                if (SKIP > 1) {
                    for (int k = 1; k < SKIP && I.hasNext(); k++) {
                        I.next();
                    }
                }
            }
        });
        int count = 0;
        int otherCount;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            otherCount = 0;
            for (int j = 0; j < numberOfTests; j++) {
                if (paramsTest.getA() <= pValue[i][j]) {
                    otherCount++;
                }
            }
            if (otherCount >= numberOfTests * 0.95) count++;
        }
        KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
        double xi2Pvalue;
        arrPVal = new double[numberSample.getCountSample() * pValue[0].length];
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < pValue[i].length; j++) {
                arrPVal[i * pValue[i].length + j] = pValue[i][j];
            }
        }
        arrPVal = Arrays.stream(arrPVal).sorted().toArray();
        xi2Pvalue = ksTest.kolmogorovSmirnovTest(new UniformRealDistribution(0, 1), arrPVal);

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
                .append("   Длина последовательности бит: ")
                .append(numberSample.getBitSetList().get(0).length()).append("\n")
                .append("   Длина блока: ").append(M).append("\n")
                .append("   Количество блоков: ").append(N).append("\n")
                .append("   Длина шаблонов в битах: ").append(m).append("\n");
        stringBuilder
                .append("Значения p-value последовательностей: ").append("\n")
                .append(Arrays.toString(Arrays.stream(arrPVal).sorted().mapToObj(x -> String.format("%.3f", x)).toArray())).append("\n")
                .append("должны быть больше ").append(paramsTest.getA()).append("\n");
        return stringBuilder;
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Проверка непересекающихся шаблонов:\n");
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
