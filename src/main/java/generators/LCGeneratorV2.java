package generators;

public class LCGeneratorV2 {
    private long multiplier = 2100005341L;
    private int nbit = 15;
    private int mask = ((1 << nbit) - 1);
    private int mask2 = ((1 << (2 * nbit)) - 1);
    private long multiplierLo = (multiplier & mask);
    private long multiplierHi = (multiplier >> nbit);
    private long state31;

    public void initLcong31(long s) {
        if (s == 0L) s = 19660809L; /* начальное число не должно быть 0 */
        s = s % 0x7fffffffL;
        state31 = s;
    }

    public int lcong31(int s) {
        long xlo, xhi;
        long z0, z1, z2;

        xlo = state31 & mask;
        xhi = state31 >> nbit;
        z0 = xlo * multiplierLo;
        /* 15bit* 15bit=> 30bit */
        z1 = xlo * multiplierHi + xhi * multiplierLo;
        /* 15bit* 16bit * 2 => 32bit */
        z2 = xhi * multiplierHi;
        /* 16bit* 16bit => 32bit */
        z0 += ((z1 & mask) << nbit) & 0xffffffffL;
        z2 += (z1 >> nbit) + (z0 >> (2 * nbit));
        z0 = (z0 & mask2)|((z2 & 1) << (2 * nbit));
        z2 >>= 1;
        state31 = z0 + z2;
        if (state31 >= 0x7fffffffL) state31 -= 0x7fffffffL;
        /* Это число не должно превышать 2*0x7fffffffL*/
        return (int) (state31 >> (31-s));
    }

}
