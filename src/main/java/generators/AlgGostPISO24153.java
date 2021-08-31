package generators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class AlgGostPISO24153 {
    private int ij, seed, seed2, s2k;
    private String str;
    private final int m1 = 2147483563, m2 = 2147483399, mm1 = 2147483562,
            a1 = 40014, a2 = 40692, q1 = 53668, q2 = 52774, r1 = 12211, r2 = 3791;
    private final double ufac = 4.6566130573917691e-10;

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public void setIJ(int ij) {
        this.ij = ij;
    }

    public void setSeed2(int seed2) {
        this.seed2 = seed2;
    }

    public void viewAlgGostP() throws ParseException {
        int nn, n, seed1, a[];
        byte yn;
        Date tnow = new Date();
        /*Алгоритмы генерации случайного числа и начального числа*/
        Scanner in = new Scanner(System.in);
        //Объем партии:
        // System.out.println("Объем партии:");
        nn = Integer.MAX_VALUE;
        //nn = in.nextInt();
        //Объем выборки:
        System.out.println("Объем выборки:");
        n = in.nextInt();
        //Ручной ввод
        System.out.println("Ручной ввод(1|0):");
        yn = in.nextByte();
        if (yn == 1)// ручной ввод начального числа
        {
            System.out.println("Введите целое из интервала" +
                    "от 1 до 2147483397, 2147483647 включительно");
            seed = in.nextInt();
            seed1 = seed; //Сохранение начального числа
            s2k = 0;
        } else {
            s2k = 0;
            str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tnow);
            seed = SeedGenerator.SeedGen();//функция вызова начального числа
            seed1 = seed; //сохранение начального числа
            s2k = SeedGenerator.getS2k();
        }
        seed2 = seed;//rng параметры функции начального числа
        ij = -1;//rng парамметры функциии инициализации
        //создание массива для выборочных значений
        a = new int[n];
        //отбор случайной выборки с возвращением
        for (int i = 0; i < n; i++) {
            a[i] = 1 + (int) Math.floor(U() * nn);//масштабированный выход(1:nn)
        }
        // выход с проверкой деталей
        System.out.printf("Объем партии: %d\n", nn);
        System.out.printf("Объем выборки: %d\n", n);
        System.out.printf("Дата и время: %s\n", str);
        System.out.printf("Количество прошедших секунд: %d\n", s2k);
        System.out.printf("Начальное число: %d\n", seed1);
        System.out.printf("Отбор выборки: \n");
        for (int i = 0; i < n; i++) {
            System.out.printf("%8d\n", a[i]);
        }
    }

    //функция генерации случайных чисел
    public double U() {
        //функция генерации случайного числа
        int j, k, i1;
        int k1;
        int[] shuffle = new int[32];
        if (ij < 0) //создание массива
        {
            for (j = 39; j >= 0; j--) {//заполнение массива перестановок
                k = seed / q1;
                seed = a1 * (seed - k * q1) - k * r1;
                if (seed < 0) seed += m1;
                if (j <= 31) shuffle[j] = seed;
            }
            ij = 0;//дальнейшая инициализация невозможна
            k1 = shuffle[0];
        }
        //генератор комбинированного случайного числа(CRNG)
        k = seed / q1;
        seed = a1 * (seed - k * q1) - k * r1;
        if (seed < 0) seed += m1;
        k = seed2 / q2;
        seed2 = a2 * (seed2 - k * q2) - k * r2;
        if (seed2 < 0) seed2 += m2;
        i1 = (int) Math.floor(32.0 * k / m1);//вычисление индекса массива
        k1 = shuffle[i1] - seed2;//не шкалированные выходные данные
        shuffle[i1] = seed;//измененный массив перестановок
        if (k1 < 1) k1 += mm1;//нешкалированные выходные данные
        return (k1 * ufac);// действительное число из интервала (0;1)
    }
}
