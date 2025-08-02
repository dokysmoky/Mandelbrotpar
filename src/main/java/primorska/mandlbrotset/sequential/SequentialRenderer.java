/*package primorska.mandlbrotset.sequential;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SequentialRenderer {

    public static void render(GraphicsContext gc, int width, int height,
                              double minX, double maxX, double minY, double maxY,
                              double zoomFactor) {

        gc.clearRect(0, 0, width, height);

        double rangeX = (maxX - minX) / zoomFactor;
        double rangeY = (maxY - minY) / zoomFactor;

        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                double x0 = minX + px * rangeX / width;
                double y0 = minY + py * rangeY / height;
                double x = 0.0, y = 0.0;
                int iteration = 0;
                int maxIter = 1000;

                while (x * x + y * y <= 4 && iteration < maxIter) {
                    double xtemp = x * x - y * y + x0;
                    y = 2 * x * y + y0;
                    x = xtemp;
                    iteration++;
                }

                Color color;
                if (iteration < maxIter) {
                    double t = (double) iteration / maxIter;
                    color = Color.hsb(280 - t * 280, 0.8, 1.0 - t * 0.8);
                } else {
                    color = Color.BLACK;
                }

                gc.getPixelWriter().setColor(px, py, color);
            }
        }
    }
}
*/
package primorska.mandlbrotset.sequential;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import primorska.mandlbrotset.renderer.MandelbrotRenderer;

public class SequentialRenderer implements MandelbrotRenderer {

    @Override
    public void render(GraphicsContext gc, int width, int height,
                       double minX, double maxX, double minY, double maxY,
                       double zoomFactor) {

        long startTime = System.nanoTime();

        gc.clearRect(0, 0, width, height);

        double rangeX = (maxX - minX) / zoomFactor;
        double rangeY = (maxY - minY) / zoomFactor;

        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                double x0 = minX + px * rangeX / width;
                double y0 = minY + py * rangeY / height;
                double x = 0.0, y = 0.0;
                int iteration = 0;
                int maxIter = 1000;

                while (x * x + y * y <= 4 && iteration < maxIter) {
                    double xtemp = x * x - y * y + x0;
                    y = 2 * x * y + y0;
                    x = xtemp;
                    iteration++;
                }

                Color color = (iteration < maxIter)
                        ? Color.hsb(280 - ((double) iteration / maxIter) * 280, 0.8, 1.0 - ((double) iteration / maxIter) * 0.8)
                        : Color.BLACK;

                gc.getPixelWriter().setColor(px, py, color);
            }
        }

        long endTime = System.nanoTime();
        System.out.printf("Sequential Mandelbrot drawn in %.2f ms%n", (endTime - startTime) / 1_000_000.0);
    }
}
