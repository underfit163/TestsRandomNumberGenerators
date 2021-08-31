package generators;

public class LCGeneratorV1 {
    private long multiplier = 1664525L;
    private long increment = 1L;
    private long state32;

    public void initLcong32(long s) {
        /* начальное число должно быть нечетным, если слагаемое INCREMENT равно 0 */
        if ((increment == 0) && (s % 2 == 0)) {
            s++;
        }
        state32 = s;
    }

    public int lcong32_31(int s) {
        state32 = ((state32 * multiplier) + increment) & 0xFFFFFFFFL;
        return (int) (state32 >> (32-s));
    }
}

