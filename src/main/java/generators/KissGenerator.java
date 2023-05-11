package generators;

public class KissGenerator {
    private long i, j, k;

    public void jkiss92Srand(long seed) {
        j = seed;
        k = seed + 1;
    }

    public long kiss92Rand() {
       j = j ^ (j << 17);
       k = (k ^ (k<<18)) & 0x7FFFFFFFL;
       i = 69069 * i + 23606797 + (j ^= (j>>15)) + (k ^=(k >> 13));
       return i;
    }

    public int kiss9231(int s) {
        return (int) ((kiss92Rand()& 0xFFFFFFFFL) >> (32-s));
    }
}
