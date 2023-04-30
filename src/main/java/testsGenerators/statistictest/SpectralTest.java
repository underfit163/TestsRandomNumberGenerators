package testsGenerators.statistictest;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.stream.IntStream;

/**
 * 9)	Спектральный тест (Spectral test).
 */
public class SpectralTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public SpectralTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void runTest() {
        int n = numberSample.getBitSetList().get(0).length();
        if (n > 500_000) n = 500_000;
        //double[] percentile = new double[numberSample.getCountSample()];
        double[] N_l = new double[numberSample.getCountSample()];
        double N_o = 0.95 * n / 2.0;
        double[] d = new double[numberSample.getCountSample()];

        double upperBound = Math.sqrt(3 * n);
        double[][] X = new double[numberSample.getCountSample()][n];
        double[][] m = new double[numberSample.getCountSample()][n / 2 + 1];
        int[] counts = new int[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];

        int finalN = n;
        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(i -> {
            for (int j = 0; j < finalN; j++) {
                if (numberSample.getBitSetList().get(i).get(j)) X[i][j] = 1;
                else X[i][j] = -1;
            }
            DoubleFFT_1D fft = new DoubleFFT_1D(finalN);
            fft.realForward(X[i]);
            m[i][0] = Math.sqrt(X[i][0] * X[i][0]);
            m[i][finalN / 2] = Math.sqrt(X[i][1] * X[i][1]);

            for (int k = 0; k < finalN / 2 - 1; k++) {
                m[i][k + 1] = Math.hypot(X[i][2 * k + 2], X[i][2 * k + 3]);
            }

            for (int k = 0; k < finalN / 2; k++) {
                if (m[i][k] < upperBound) {
                    counts[i]++;
                }
            }
            //percentile[i] = (double) counts[i] / ((double) finalN / 2) * 100;
            N_l[i] = counts[i];
            d[i] = (N_l[i] - N_o) / Math.sqrt(finalN / 2.0 * 0.95 * 0.05);
            pValue[i] = Erf.erfc(Math.abs(d[i]) / Math.sqrt(2.0));
        });

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

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Спектральный тест:\n");
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
