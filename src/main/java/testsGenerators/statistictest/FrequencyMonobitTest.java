package testsGenerators.statistictest;

import fr.devnied.bitlib.BytesUtils;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;

/*
6)	Частотный тест (Frequency Monobit test).
 */
public class FrequencyMonobitTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public FrequencyMonobitTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
        //из ряда 2^numEng
        int numEng = (int) (Math.log(numberSample.getNSample()) / Math.log(2));
        int otherN = (int) Math.pow(2, numEng);
        int[] sum = new int[numberSample.getCountSample()];
        Arrays.parallelSetAll(sum, i -> getValSum(i, otherN));
        double[] Sn = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        int count = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            Sn[i] = Math.abs(sum[i]) / Math.sqrt(2 * otherN * numberSample.getCapacity());
            pValue[i] = Erf.erfc(Sn[i]);
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
        }
        //Анализ числа появлений значений P-value
        int[] vPvalue = new int[10];
        double left;
        double right;
        for (double v : pValue) {
            for (int i = 1; i <= vPvalue.length; i++) {
                left = (double) (i - 1) / 10;
                right = (double) i / 10;
                if (i == 10 && v == right) {
                    vPvalue[i - 1]++;
                }
                if (left <= v && v < right) {
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
        if (xi2Pvalue >= paramsTest.getA()) {
            paramsTest.getTestPval().put(getClass().getSimpleName(), true);
        } else paramsTest.getTestPval().put(getClass().getSimpleName(), false);
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        if (paramsTest.getTestPval().get(getClass().getSimpleName())) {
            paramsTest.getTests().put(getClass().getSimpleName(), (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                    paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
        } else paramsTest.getTests().put(getClass().getSimpleName(), false);
    }

    public int getValSum(int i, int otherN) {
        int sumCount = 0;
        for (int j = 0; j < otherN; j++) {
            for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) sumCount++;
                else sumCount--;
            }
        }
        return sumCount;
    }

    @Override
    public StringBuilder result() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест 6. Частотный тест в подпоследовательностях:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}