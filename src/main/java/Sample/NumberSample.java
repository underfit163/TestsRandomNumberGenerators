package sample;

import fr.devnied.bitlib.BytesUtils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class NumberSample {
    private final int nSample;
    private final int countSample;
    private int capacity;
    private double maxNumber;
    private final int[][] sample;

    List<BitSet> bitSetList;

    public NumberSample(int count, int n) {
        countSample = count;
        nSample = n;
        sample = new int[count][n];
    }

    public List<BitSet> getBitSetList() {
        return bitSetList;
    }

    public int[][] getSample() {
        return sample;
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

    public double getMaxNumber() {
        return maxNumber;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        this.maxNumber = Math.pow(2, capacity) - 1;
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

    public void getBitSets() {
        bitSetList = new ArrayList<>();
        for (int i = 0; i < countSample; i++) {
            bitSetList.add(new BitSet(nSample * capacity));
            for (int j = 0; j < nSample; j++) {
                for (int k = (capacity - 1); k >= 0; k--) {
                    if (BytesUtils.matchBitByBitIndex(getItemSample(i, j), k)) {
                        bitSetList.get(i).set(j * capacity + (capacity - 1 - k));
                    }
                }
            }
        }
    }

    public byte[][] intToByteSample() {
        byte[][] byteSample = new byte[countSample][(nSample * capacity + 7) / 8];
        int t = 7;
        for (int i = 0; i < countSample; i++) {
            for (int j = 0; j < nSample; j++) {
                for (int k = (capacity - 1); k >= 0; k--) {
                    if (BytesUtils.matchBitByBitIndex(getItemSample(i, j), k)) {
                        byteSample[i][(j * capacity + (capacity - 1 - k)) / 8] = setBit(byteSample[i][(j * capacity + (capacity - 1 - k)) / 8], t);
                    }
                    t--;
                    if (t < 0) t = 7;
                }
            }
        }
        return byteSample;
    }

    public boolean getBit(byte num, int i) {
        return (num & (1 << i)) != 0;
    }

    public byte setBit(byte num, int i) {
        return (byte) (num | (1 << i));
    }
}
