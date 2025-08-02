package primorska.mandlbrotset.renderer;

import javafx.scene.canvas.GraphicsContext;

public interface MandelbrotRenderer {
    void render(GraphicsContext gc, int width, int height,
                double minX, double maxX, double minY, double maxY,
                double zoomFactor) throws InterruptedException;
}
