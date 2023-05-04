package testsGenerators.statistictest;

import org.apache.commons.math3.special.Gamma;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.BitSet;
import java.util.stream.IntStream;

/**
 * 12)	Проверка линейной сложности (Linear complexity test).
 * Определение линейной сложности с помощью алгоритма Берле-кэмпа–Месси
 */
public class LinearComplexityTest implements Test {
    private final int M;
    private final NumberSample numberSample;
    private final ParamsTest paramsTest;

    /**
     * @param M длина каждого блока непересекающихся последовательностей
     */
    public LinearComplexityTest(NumberSample numberSample, ParamsTest paramsTest, int M) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
        if (M < 500 || M > 5000) {
            this.M = 500;
        } else this.M = M;
    }

    @Override
    public void runTest() {
        double[] chi2 = new double[numberSample.getCountSample()];
        double[] pValue = new double[numberSample.getCountSample()];
        int K = 6;
        double[] pi = new double[]{0.01047, 0.03125, 0.12500, 0.50000, 0.25000, 0.06250, 0.020833};
        int length = numberSample.getBitSetList().get(0).length();
        int N = length / M;

        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(t -> {
            int d, L, m, N_, sign;
            double T_, mean;
            BitSet C = new BitSet(M);
            BitSet T = new BitSet(M);
            BitSet P = new BitSet(M);
            BitSet B_ = new BitSet(M);
            long[] nu = new long[7];
            for (int ii = 0; ii < N; ii++) {
                C.clear();//Шаг 1.
                T.clear();
                P.clear();
                B_.clear();
                L = 0;
                m = -1;
                C.set(0);
                B_.set(0);
                N_ = 0;
                /* DETERMINE LINEAR COMPLEXITY */
                while (N_ < M) {//Шаг 2.
                    d = numberSample.getBitSetList().get(t).get(ii * M + N_) ? 1 : 0;//Шаг 2.1.
                    for (int i = 1; i <= L; i++) {
                        d += (C.get(i) ? 1 : 0) * (numberSample.getBitSetList().get(t).get(ii * M + N_ - i) ? 1 : 0);
                    }
                    d = d % 2;
                    if (d == 1) {//Шаг 2.2
                        P.clear();
                        T.or(C);
                        for (int j = 0; j < M; j++) {//Шаг 2.2.1.
                            if (B_.get(j)) {
                                P.set(j + N_ - m);
                            }
                        }
                        for (int i = 0; i < M; i++) {
                            C.set(i, ((C.get(i) ? 1 : 0) + (P.get(i) ? 1 : 0)) % 2 == 1);
                        }
                        if (L <= N_ / 2) {//Шаг 2.2.2
                            L = N_ + 1 - L;
                            m = N_;
                            B_.clear();
                            B_.or(T);
                        }
                    }
                    N_++;//Шаг 2.3
                }
                if ((M + 1) % 2 == 0) {
                    sign = -1;
                } else {
                    sign = 1;
                }
                mean = M / 2.0 + (9.0 + sign) / 36.0 - 1.0 / Math.pow(2, M) * (M / 3.0 + 2.0 / 9.0);
                if (M % 2 == 0) {
                    sign = 1;
                } else {
                    sign = -1;
                }
                T_ = sign * (L - mean) + 2.0 / 9.0;
                if (T_ <= -2.5) {
                    nu[0]++;
                } else if (T_ > -2.5 && T_ <= -1.5) {
                    nu[1]++;
                } else if (T_ > -1.5 && T_ <= -0.5) {
                    nu[2]++;
                } else if (T_ > -0.5 && T_ <= 0.5) {
                    nu[3]++;
                } else if (T_ > 0.5 && T_ <= 1.5) {
                    nu[4]++;
                } else if (T_ > 1.5 && T_ <= 2.5) {
                    nu[5]++;
                } else {
                    nu[6]++;
                }
            }
            for (int i = 0; i < K + 1; i++) {
                chi2[t] += Math.pow(nu[i] - N * pi[i], 2) / (N * pi[i]);
            }
            pValue[t] = Gamma.regularizedGammaQ(K / 2.0, chi2[t] / 2.0);
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

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Проверка линейной сложности:\n");
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
