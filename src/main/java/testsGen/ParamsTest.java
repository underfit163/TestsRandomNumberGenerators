package testsGen;

public class ParamsTest {
    private double[] heights;
    private double[] halfIntervals;
    private double a = 0.01;
    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }
    public void initHeights(int n) {
        heights = new double[n];
    }

    public void initHalfIntervals(int n) {
        halfIntervals = new double[n];
    }

    public double[] getHeights() {
        return heights;
    }

    public void setParamHeights(int i, double val) {
        heights[i] = val;
    }

    public void setParamHalfIntervals(int i, double val) {
        halfIntervals[i] = val;
    }

    public double getParamHeights(int i) {
        return heights[i];
    }

    public double getParamHalfIntervals(int i) {
        return halfIntervals[i];
    }
}
