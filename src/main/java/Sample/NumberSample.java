package Sample;

public class NumberSample {
    private int nSample;
    private int countSample;
    private int capacity;
    private int[][] sample;

    public NumberSample(int count, int n) {
        countSample = count;
        nSample = n;
        sample = new int[count][n];
    }

    public int getItemSample(int i, int j) {
        return sample[i][j];
    }

    public void setItemSample(int i, int j, int value) {
        sample[i][j] = value;
    }

    public int getNSample() {
        return nSample;
    }

    public int getCountSample() {
        return countSample;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getNSampleMas() {
        return countSample * nSample;
    }

    public int[] matrToMas() {
        int[] sampleMas = new int[countSample * nSample];
        for (int i = 0; i < countSample; i++) {
            for (int j = 0; j < nSample; j++) {
                sampleMas[i * nSample + j] = getItemSample(i, j);
            }
        }
        return sampleMas;
    }
}
