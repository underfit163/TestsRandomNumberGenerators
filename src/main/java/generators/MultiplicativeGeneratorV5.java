package generators;

public class MultiplicativeGeneratorV5 {
    private long m = 0x7FFFFFFFL;
    private int a = 16807;
    private int q = (int) (m/a);
    private int p = 16807;
    long next = 1;

    public MultiplicativeGeneratorV5() {
        this.q = (int) (m/a);
        this.p = (int) (m%a);
    }

    public int pmRand(int s) {
        next = a * (next % q) - p * (next / q);
        if(next < 0) next = next + m;
        return (int) (next >> (31 - s));
    }

    public void pmSrand(long seed) {
        next = seed;
    }
}
