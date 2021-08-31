package generators;

/**
 * В данном примере использовано р = 624 слова с параметрами (624, 397, 31, 32, 0x9908b0df, 11, 7, 15, 18, 0x9d2c5680, 0xefc60000).
 * Период равен (219937 - 1),а числа распределены равномерно в 623-размерном гиперкубе с 32-битовой точностью.
 * Кроме того последовательность соответствует равномерному распределению в 3 115-мерном пространстве с 6-битовой точностью.
 */
public class TwisterGenerator {
    private int p = 624;
    private int q = 397;
    private long matrixA = 0x9908b0dfL;/* постоянный вектор a*/
    private long upperMask = 0x80000000L;/* наиболее значимые (w-r) бит*/
    private long lowerMask = 0x7fffffffL;/* последние значимые r бит*/

    private long[] mt = new long[p];/* массив состояния вектора*/
    private int mti = p + 1;/*  mti==P+1 означает, что mt [Р] не инициализирован*/

    /* инициализация mt [P] с начальным значением a */
    public void initGenrand(long s) {
        mt[0] = s & 0xffffffffL;
        //начальное    заполнение
        for (mti = 1; mti < p; mti++) {
            mt[mti] = (1664525L * mt[mti - 1] + 1L);
            mt[mti] &= 0xffffffffL;
        }
    }

    /* генерация случайного числа из интервала [0,Oxffffffff] */
    public long genrand() {
        long y;
        long[] mag01 = {0x0L, matrixA};/*mag01 [х] = х * MATRIX_A для х=0,i*/
        if (mti >= p) {
            int kk;
            if (mti == p + 1) initGenrand(5489L);
            for (kk = 0; kk < p - q; kk++) {
                //Вычисление (xi^u | xi+1^l)
                y = (mt[kk] & upperMask) | (mt[kk + 1] & lowerMask);
                // Вычисляется значение следующего элемента последовательности по
                // рекуррентному выражению
                mt[kk] = mt[kk + q] ^ (y >> 1) ^ mag01[(int) (y & 0x1L)];
            }
            for (; kk < p - 1; kk++) {
                y = (mt[kk] & upperMask) | (mt[kk + 1] & lowerMask);
                mt[kk] = mt[kk + (q - p)] ^ (y >> 1) ^ mag01[(int) (y & 0x1L)];
            }
            y = (mt[p - 1] & upperMask) | (mt[0] & lowerMask);
            mt[p - 1] = mt[q - 1] ^ (y >> 1) ^ mag01[(int) (y & 0x1L)];
            mti = 0;
        }
        y = mt[mti++];
        /*
        Необработанные последовательности, генерируемые рекурсией (1), обладают плохим равномерным распределением на больших размерностях.
        Чтобы это исправить, используется метод закалки,на выходе которого получается итоговая псевдослучайная последовательность.
         */
        y ^= (y >> 11);
        y ^= (y << 7) & 0x9d2c5680L;
        y ^= (y << 15) & 0xefc60000L;
        y ^= (y >> 18);
        return y;
    }

    /* генерация случайного числа из интервала [0, 0x7fffffff] */
    public int genrand31(int s) {
        return (int) (genrand() >> (32-s));
    }
}

