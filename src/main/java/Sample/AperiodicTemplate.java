package sample;

import java.util.Iterator;

public class AperiodicTemplate implements Iterable<Long> {

    public static void main(String[] argv) {
        int m = 5;
        if (argv.length == 1) {
            m = Integer.parseInt(argv[0]);
        }
        AperiodicTemplate at = new AperiodicTemplate(m);

        long num = ((long) 1) << m;
        int nonPeriodic = 0;
        for (Long i : at) {
            nonPeriodic++;
            System.out.println(at.bitString(i, " "));
        }
    }

    private final long[] left;
    private final long[] right;
    private final int len;
    private final int[] shift;
    private final static int[] numOfTemplates = new int[32];

    /* { 0, 0, 2, 4, 6, 12, 20, 40, 74, 148, 284, 568, 1116,
                                                           2232, 4424, 8848, 17622, 35244, 70340, 140680, 281076, 562152,
    1123736, 2247472, 4493828, 8987656, }; */


    static {
        numOfTemplates[2] = 2;

        for (int i = 3; i < numOfTemplates.length; i++) {
            if ((i % 2) == 1) {
                numOfTemplates[i] = 2 * numOfTemplates[i - 1];
            } else {
                numOfTemplates[i] = 2 * numOfTemplates[i - 1] - numOfTemplates[i / 2];
            }
        }
    }

    public AperiodicTemplate(int len) {
        if (len < 2 || len > 31) {
            throw new IllegalArgumentException("len must be in the range [2,31]");
        }

        this.len = len;

        int half = len / 2;
        left = new long[half];
        right = new long[half];
        shift = new int[half];

        long l = -1, r = 1;
        l <<= len - 1;

        for (int i = 0; i < half; i++) {
            left[i] = l;
            right[i] = r;
            shift[i] = len - 1 - i;

            l >>= 1;
            r = (r << 1) | 1;
        }
    }

    public int getCount() {
        return numOfTemplates[len];
    }

    public long checkValue(long value) {
        for (int i = 0; i < left.length; i++) {
//             if (((value & left[i]) >> shift[i]) == (value & right[i])) return 0;
            if (((value >> shift[i]) & right[i]) == (value & right[i])) {
                return 0;
            }
        }
        return value;
    }

    public String bitString(long value, String delimiter) {
        StringBuilder sb = new StringBuilder();
        long mask = 1L << (len - 1);
        for (int i = 0; i < len; i++) {
            if (i > 0 && delimiter != null) {
                sb.append(delimiter);
            }
            sb.append((value & mask) != 0 ? '1' : '0');
            mask >>= 1;
        }

        return sb.toString();
    }

    @Override
    public Iterator<Long> iterator() {
        return new Iterator<>() {
            long n = 0;
            final long max = (1L << len) - 2;

            @Override
            public boolean hasNext() {
                return n < max;
            }

            @Override
            public Long next() {
                while (n++ < max) {
                    if (checkValue(n) > 0) {
                        return n;
                    }
                }
                return 0L;
            }
        };
    }
}
