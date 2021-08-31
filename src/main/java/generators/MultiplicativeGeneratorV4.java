package generators;

public class MultiplicativeGeneratorV4 {
    private long multiplier = 214013L;
    private long increment = 2531011L;
    private long state32;

    public void initMul(long s) {
        /* начальное число должно быть нечетным, если слагаемое INCREMENT равно 0 */
        if ((increment == 0) && (s % 2 == 0)) {
            s++;
        }
        state32 = s;
    }

    public int Mul32_31(int s) {
        state32 = ((state32 * multiplier) + increment) & 0xFFFFFFFFL;
        return (int) (state32 >> (32 - s));
    }
}
