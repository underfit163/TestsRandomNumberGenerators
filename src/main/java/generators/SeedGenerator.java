package generators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SeedGenerator {
    private static int s2k;
    private static final int m2 = 2147483399, a2 = 40692, q2 = 52774, r2 = 3791;

    public static int getS2k() {
        return s2k;
    }

    //Генератор случайного начального числа
    public static int SeedGen() throws ParseException {
        //Установление даты и времени комп. системы
        int i, j, k;
        Date t;
        // время отсчета: 2000-01-01 00:00:00
        t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2000-01-01 00:00:00");
        long diff = System.currentTimeMillis() - t.getTime();
        //количество дней до компьютерной даты с 2000-01-01 00:00:00
        //int diffDays = (int) TimeUnit.MILLISECONDS.toDays(diff);
        //количество секунд с 2000-01-01 00:00:00
        s2k = (int) TimeUnit.MILLISECONDS.toSeconds(diff) & 0x7FFFFFFF;

        //генератор квазисинхронного начального числа
        int seed = s2k;//инициализация начального числа
        j = s2k - (s2k / 100) * 100 + 1;//пробное значение
        for (i = 1; i < j; i++) {
            k = seed / q2;
            seed = a2 * (seed - k * q2) - k * r2;
            if (seed < 0) seed += m2;
        }
        return seed;
    }

}
