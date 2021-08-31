package generators;

public class MultiplicativeGeneratorV2 {
    private double z = 0.011;
    private double increment = Math.PI;
    private double state32;

    public void initMul(long s) {
        int st = (int) Math.ceil(Math.log10(s) / Math.log10(2));
        state32 = s / (Math.pow(2, st) - 1);
    }

    public int Mul32_31(int s) {
        z = z + Math.pow(10, -8);
        state32 = (state32 / z) + increment;
        int res = (int) state32;
        state32 = state32 - res;
        long prom = (long) Math.ceil((state32 * 10000000000L));
        long state32Long = prom & 0xFFFFFFFFL;
        return (int) (state32Long >> (32 - s));
    }
}
