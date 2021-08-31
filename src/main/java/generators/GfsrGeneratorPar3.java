package generators;

public class GfsrGeneratorPar3 {
    private int p = 1279;
    private int q = 418;
    private int w = 32;/* значение должно быть степенью 2*/

    private long[] state = new long[p];
    private int stateI;

    /**
     * Функция init_gfsr (s) выполняет инициализацию при условии, что в качестве начального числа используется 32-битовое целое число без знака [целое число из интервала от 0 до (32^2-1)].
     * @param s начальное число инициализации
     */
    public void initGfsr(long s) {
        long[] x = new long[p];
        s &= 0xffffffffL;
        for (int i = 0; i < p; i++) {
            x[i] = s >> 31;
            s = 1664525L * s + 1L;
            s &= 0xffffffffL;
        }
        for (int k = 0, i = 0; i < p; i++) {
            state[i] = 0L;
            for (int j = 0; j < w; j++) {
                state[i] <<= 1;
                state[i] |= x[k];
                x[k] ^= x[(k + q) % p];
                k++;
                if (k == p) k = 0;
            }
        }
        stateI = 0;
    }

    /**
     * При обращении к функции gfsr () происходит генерация целого числа из интервала от 0 до (2^32-1) включительно.
     * @return сгенерированное число
     */
    public long gfsr() {
        int i;
        int p0, p1;
        if (stateI >= p) {
            stateI = 0;
            p0 = 0;
            p1 = q;
            for (i = 0; i < (p - q); i++) {
                state[p0] ^= state[p1];
                p0 += 1;
                p1 += 1;
            }
            p1 = 0;
            for (; i < p; i++) {
                state[p0] ^= state[p1];
                p0 += 1;
                p1 += 1;
            }
        }
        return state[stateI++];
    }

    /**
     * При обращении к функции gfsr_31 () происходит генерация целого числа из интервала от 0 до (2^32-1) включительно.
     * @return сгенерированное число
     */
    public int gfsr31(int s) {
        return (int) (gfsr() >> (32-s));
    }
}
