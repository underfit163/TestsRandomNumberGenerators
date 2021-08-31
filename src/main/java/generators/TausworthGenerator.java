package generators;

/**
 * Ниже приведен текст программы комбинированного метода Таусворта, генерирующего целые
 * числа из интервала от 0 до (2^31 - 1) включительно, на основе комбинации трех последовательностей параметров
 * Таусворта (31, 13, 12), (29, 2, 4) и (28, 3, 17).
 */
public class TausworthGenerator {
    private long s1;
    private long s2;
    private long s3;

    public void taus88(long s) {
        int i = 0;
        long[] x = new long[3];
        while (i < 3) {
            if ((s & 0xfffffff0L) != 0) {
                s &= 0xffffffffL;
                x[i] = s;
                i++;
            }
            s = 1664525L * s + 1L;
        }
        s1 = x[0];
        s2 = x[1];
        s3 = x[2];
    }
    /* 31-битовое целое */

    /**
     * Выбор трех параметров (р, q, t) позволяет получить лучшее многомерное равномерное распределение
     * последовательности псевдослучайных чисел после операции объединения. Значения параметров необходимо
     * сохранять. Чтобы получить другую последовательность псевдослучайных чисел, необходимо изменить начальное число.
     *
     * @return Генерируемое число
     */
    public int taus8831() {
        //операторы генерируют числа в последовательности Таусворта с параметрами (31, 13, 12) в s1
        long b = ((((s1 << 13) & 0xffffffffL) ^ s1) >> 19);
        s1 = ((((s1 & 4294967294L) << 12) & 0xffffffffL) ^ b);
        //операторы генерируют числа последовательности Таусворта с параметрами (29, 2, 4) и (28, 3, 17), s2 и s3 соответственно
        b = ((((s2 << 2) & 0xffffffffL) ^ s2) >> 25);
        s2 = ((((s2 & 4294967288L) << 4) & 0xffffffffL) ^ b);
        b = ((((s3 << 3) & 0xffffffffL) ^ s3) >> 11);
        s3 = ((((s3 & 4294967280L) << 17) & 0xffffffffL) ^ b);
        return (int) ((s1 ^ s2 ^ s3) >> 1);
    }
    public int tausCapacity(int s)
    {
        return taus8831() >> (31-s);
    }

}
