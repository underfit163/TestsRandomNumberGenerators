package testsGenerators.statistictest;

import javafx.scene.control.Alert;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;

public class KolmogorovSmirnovTest implements Test {
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    public KolmogorovSmirnovTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
        try {
            double max = Math.pow(2, numberSample.getCapacity()) - 1;
            double[][] sampleDouble = new double[numberSample.getCountSample()][numberSample.getNSample()];
            UniformRealDistribution uniformRealDistribution = new UniformRealDistribution(0, 1);
            org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest smirnovTest = new org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest();
            double[] pValue = new double[numberSample.getCountSample()];
            int count = 0;
            for (int i = 0; i < numberSample.getCountSample(); i++) {
                for (int j = 0; j < numberSample.getNSample(); j++) {
                    sampleDouble[i][j] = (double) numberSample.getItemSample(i, j) / max;
                }
                pValue[i] = 1.0D - smirnovTest.cdf(kolmogorovSmirnovTest(uniformRealDistribution, sampleDouble[i]), sampleDouble[i].length);

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
        } catch (OutOfMemoryError e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Окно ошибки");
            alert.setHeaderText("Переполнение памяти");
            alert.setContentText("Тест Колмогорова-Смирнова не выполнится");
            alert.show();
        }
    }

    public double kolmogorovSmirnovTest(RealDistribution distribution, double[] data) {
        int n = data.length;
        Arrays.parallelSort(data);
        double d = 0.0D;
        for (int i = 1; i <= n; ++i) {
            double yi = distribution.cumulativeProbability(data[i - 1]);
            double currD = FastMath.max(yi - (double) (i - 1) / (double) n, (double) i / (double) n - yi);
            if (currD > d) {
                d = currD;
            }
        }
        return d;
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Тест Колмогорова-Смирнова:\n");
        stringBuilder.append("Доля последовательностей прошедших тест: ").append(paramsTest.getDols().get(getClass().getSimpleName())).append("\n");
        if (paramsTest.getTests().get(getClass().getSimpleName())) {
            stringBuilder.append("Тест пройден\n");
        } else {
            stringBuilder.append("Тест не пройден\n");
        }
        return stringBuilder;
    }
}
