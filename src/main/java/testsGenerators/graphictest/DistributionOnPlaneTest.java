package testsGenerators.graphictest;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sample.NumberSample;

public class DistributionOnPlaneTest implements GraphicTest {

    private final NumberSample numberSample;
    private final Canvas graphTestCanvas;
    private final Canvas graphDopTestCanvas;

    public DistributionOnPlaneTest(NumberSample numberSample, Canvas graphTestCanvas, Canvas graphDopTestCanvas) {
        this.numberSample = numberSample;
        this.graphTestCanvas = graphTestCanvas;
        this.graphDopTestCanvas = graphDopTestCanvas;
    }

    @Override
    public void runTest() {
        GraphicsContext gc = graphTestCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, graphTestCanvas.getWidth(), graphTestCanvas.getHeight());
        GraphicsContext gc1 = graphDopTestCanvas.getGraphicsContext2D();
        gc1.clearRect(0, 0, graphTestCanvas.getWidth(), graphTestCanvas.getHeight());
        //int size = numberSample.getNSampleMas() - 4;
        int size = numberSample.getNSample() - 4;
        int max = (int) (Math.pow(2, numberSample.getCapacity()) - 1);
        //int[] pixelsMas = numberSample.matrToMas();
        for (int j = 0; j < numberSample.getCountSample(); j++) {
            if(j * size > 2_000_000) break;
            int[] pixelsMas = numberSample.getSample()[j];
            for (int i = 0; i < size; i++) {
                gc.getPixelWriter().setColor(
                        (int) (graphTestCanvas.getWidth() * (double) pixelsMas[i] / max),
                        (int) (graphTestCanvas.getHeight() * (double) pixelsMas[i + 1] / max),
                        new Color((double) pixelsMas[i + 4] / max,
                                (double) pixelsMas[i + 3] / max,
                                (double) pixelsMas[i + 2] / max, 1));
                if (i != size - 1)
                    gc1.getPixelWriter().setColor(
                            (int) (graphDopTestCanvas.getWidth() * 0.5 * ((double) pixelsMas[i] / max + (double) pixelsMas[i + 1] / max)),
                            (int) (graphDopTestCanvas.getHeight() * 0.5 * ((double) pixelsMas[i + 1] / max + (double) pixelsMas[i + 2] / max)),
                            new Color(0.5 * ((double) pixelsMas[i + 4] / max + (double) pixelsMas[i + 5] / max),
                                    0.5 * ((double) pixelsMas[i + 3] / max + (double) pixelsMas[i + 4] / max),
                                    0.5 * ((double) pixelsMas[i + 2] / max + (double) pixelsMas[i + 3] / max), 1));
            }
        }
    }

    @Override
    public void run() {
        runTest();
    }
}
