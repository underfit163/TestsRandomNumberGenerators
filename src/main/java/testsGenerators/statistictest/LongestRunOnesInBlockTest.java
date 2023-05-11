package testsGenerators.statistictest;

import fr.devnied.bitlib.BytesUtils;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import sample.NumberSample;
import testsGenerators.ParamsTest;

import java.util.Arrays;
import java.util.stream.IntStream;

/*
17)	Тест на равномерность в подпоследовательностях (Test for longest run of ones in a block).
 */
public class LongestRunOnesInBlockTest implements Test {

    private final NumberSample numberSample;
    private final ParamsTest paramsTest;
    private int M;
    private int K;
    private int N;
    private double[] pValue;

    public LongestRunOnesInBlockTest(NumberSample numberSample, ParamsTest paramsTest) {
        this.numberSample = numberSample;
        this.paramsTest = paramsTest;
    }

    @Override
    public void run() {
        runTest();
    }

    @Override
    public void runTest() {
        M = 8;
        K = 3;
        long size = (long) numberSample.getNSample() * numberSample.getCapacity();
        if (6272 <= size && size < 750000) {
            M = 128;
            K = 5;
        } else if (750000 <= size) {
            M = 10000;
            K = 6;
        }
        N = numberSample.getNSample() / M;
        int newNSampleMas = M * N;
        int newNSample = newNSampleMas / numberSample.getCapacity();
        int diffBit = newNSampleMas % numberSample.getCapacity();

        int[][] countMax = new int[numberSample.getCountSample()][N];
        int finalM = M;
        IntStream.range(0, numberSample.getCountSample()).parallel().forEach(i -> {
            int maxCount = 0;
            int sumCount = 0;
            int numBlock = 0;
            int countBlock = 0;
            for (int j = 0; j < newNSample; j++) {
                for (int k = (numberSample.getCapacity() - 1); k >= 0; k--) {
                    if (countBlock < finalM) {
                        if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j), k)) {
                            sumCount++;
                        } else {
                            if (sumCount > maxCount) {
                                maxCount = sumCount;
                            }
                            sumCount = 0;
                        }
                        countBlock++;
                    }
                    if (countBlock == finalM) {
                        if (sumCount > maxCount) {
                            maxCount = sumCount;
                        }
                        countMax[i][numBlock] = maxCount;
                        numBlock++;
                        sumCount = 0;
                        maxCount = 0;
                        countBlock = 0;
                    }
                    if (diffBit != 0 && j == (newNSample - 1) && k == 0) {
                        for (int l = (numberSample.getCapacity() - 1); l > (numberSample.getCapacity() - 1 - diffBit); l--) {
                            if (countBlock < finalM) {
                                if (BytesUtils.matchBitByBitIndex(numberSample.getItemSample(i, j + 1), l)) {
                                    sumCount++;
                                } else {
                                    if (sumCount > maxCount) {
                                        maxCount = sumCount;
                                    }
                                    sumCount = 0;
                                }
                                countBlock++;
                            }
                            if (countBlock == finalM) {
                                if (sumCount > maxCount) {
                                    maxCount = sumCount;
                                }
                                countMax[i][numBlock] = maxCount;
                                numBlock++;
                                sumCount = 0;
                                maxCount = 0;
                                countBlock = 0;
                            }
                        }
                    }
                }
            }
        });

        int[][] v = new int[numberSample.getCountSample()][K + 1];
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < N; j++) {
                categorRasp(v, i, countMax[i][j], M);
            }
        }
        double[] xi2 = new double[numberSample.getCountSample()];
        pValue = new double[numberSample.getCountSample()];
        int count = 0;
        double x1 = 0;
        for (int i = 0; i < numberSample.getCountSample(); i++) {
            for (int j = 0; j < K + 1; j++) {
                x1 += Math.pow(v[i][j] - N * getPi(M, j), 2) / (N * getPi(M, j));
            }
            xi2[i] = x1;
            pValue[i] = Gamma.regularizedGammaQ((double) K / 2, xi2[i] / 2);
            if (paramsTest.getA() <= pValue[i]) {
                count++;
            }
            x1 = 0;
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
        }
        //доля последовательностей прошедших тест с 1-a вероятностью должна попасть в этот интервал.
        paramsTest.getDols().put(getClass().getSimpleName(), (double) count / numberSample.getCountSample());
        if (paramsTest.getTestPval().get(getClass().getSimpleName())) {
            paramsTest.getTests().put(getClass().getSimpleName(), (1 - paramsTest.getA()) - 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()) <= paramsTest.getDols().get(getClass().getSimpleName()) &&
                    paramsTest.getDols().get(getClass().getSimpleName()) <= (1 - paramsTest.getA()) + 3 * Math.sqrt(paramsTest.getA() * (1 - paramsTest.getA()) / numberSample.getCountSample()));
        }else paramsTest.getTests().put(getClass().getSimpleName(), false);
    }

    public void categorRasp(int[][] v, int i, int l, int M) {
        if (M == 8) {
            if (l <= 1) {
                v[i][0]++;
            } else if (l == 2) {
                v[i][1]++;
            } else if (l == 3) {
                v[i][2]++;
            } else {
                v[i][3]++;
            }
        } else if (M == 128) {
            if (l <= 4) {
                v[i][0]++;
            } else if (l == 5) {
                v[i][1]++;
            } else if (l == 6) {
                v[i][2]++;
            } else if (l == 7) {
                v[i][3]++;
            } else if (l == 8) {
                v[i][4]++;
            } else {
                v[i][5]++;
            }
        } else if (M == 10000) {
            if (l <= 10) {
                v[i][0]++;
            } else if (l == 11) {
                v[i][1]++;
            } else if (l == 12) {
                v[i][2]++;
            } else if (l == 13) {
                v[i][3]++;
            } else if (l == 14) {
                v[i][4]++;
            } else if (l == 15) {
                v[i][5]++;
            } else {
                v[i][6]++;
            }
        }
    }

    public double getPi(int M, int i) {
        double v = 0;
        if (M == 8) {
            switch (i) {
                case 0:
                    v = 0.2148;
                    break;
                case 1:
                    v = 0.3672;
                    break;
                case 2:
                    v = 0.2305;
                    break;
                case 3:
                    v = 0.1875;
            }
        } else if (M == 128) {
            switch (i) {
                case 0:
                    v = 0.1174;
                    break;
                case 1:
                    v = 0.2430;
                    break;
                case 2:
                    v = 0.2493;
                    break;
                case 3:
                    v = 0.1752;
                    break;
                case 4:
                    v = 0.1027;
                    break;
                case 5:
                    v = 0.1124;
            }
        } else if (M == 10000) {
            switch (i) {
                case 0: v = 0.0882;
                    break;
                case 1: v = 0.2092;
                    break;
                case 2: v = 0.2483;
                    break;
                case 3: v = 0.1933;
                    break;
                case 4: v = 0.1208;
                    break;
                case 5: v = 0.0675;
                    break;
                case 6: v = 0.0727;
                    break;
            }
        }
        return v;
    }

    public StringBuilder resultTest() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Параметры теста: ").append("\n")
                .append("   Длина последовательности бит: ").append(numberSample.getBitSetList().get(0).length()).append("\n")
                .append("   Длина блока: ").append(M).append("\n")
                .append("   Количество блоков: ").append(N).append("\n")
                .append("   Количество категорий: ").append(K).append("\n");
        stringBuilder
                .append("Значения p-value последовательностей: ").append("\n")
                .append(Arrays.toString(Arrays.stream(pValue).sorted().mapToObj(x -> String.format("%.3f", x)).toArray())).append("\n")
                .append("должны быть больше ").append(paramsTest.getA()).append("\n");
        return stringBuilder;
    }

    @Override
    public StringBuilder result(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Тест ").append(count).append(". Тест «блоков» в подпоследовательностях:\n");
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
