/*package primorska.mandlbrotset.parallel;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelRenderer {

    public static void render(GraphicsContext gc, int width, int height,
                              double minX, double maxX, double minY, double maxY,
                              double zoomFactor) throws InterruptedException {

        long startTime = System.nanoTime();

        double rangeX = (maxX - minX) / zoomFactor;
        double rangeY = (maxY - minY) / zoomFactor;

        // Number of threads - usually number of CPU cores
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        // Divide image horizontally among threads
        int rowsPerThread = height / cores;

        for (int i = 0; i < cores; i++) {
            final int startY = i * rowsPerThread;
            final int endY = (i == cores - 1) ? height : startY + rowsPerThread;

            executor.submit(() -> {
                for (int py = startY; py < endY; py++) {
                    for (int px = 0; px < width; px++) {
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

                        synchronized (gc) {
                            gc.getPixelWriter().setColor(px, py, color);
                        }
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.nanoTime();
        System.out.printf("Parallel Mandelbrot drawn in %.2f ms%n", (endTime - startTime) / 1_000_000.0);
    }
}
*/
package primorska.mandlbrotset.parallel;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import primorska.mandlbrotset.renderer.MandelbrotRenderer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelRenderer implements MandelbrotRenderer {

    @Override
    public void render(GraphicsContext gc, int width, int height,
                       double minX, double maxX, double minY, double maxY,
                       double zoomFactor) throws InterruptedException {

        long startTime = System.nanoTime();

        double rangeX = (maxX - minX) / zoomFactor;
        double rangeY = (maxY - minY) / zoomFactor;

        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        int rowsPerThread = height / cores;

        for (int i = 0; i < cores; i++) {
            final int startY = i * rowsPerThread;
            final int endY = (i == cores - 1) ? height : startY + rowsPerThread;

            executor.submit(() -> {
                for (int py = startY; py < endY; py++) {
                    for (int px = 0; px < width; px++) {
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

                        synchronized (gc) {
                            gc.getPixelWriter().setColor(px, py, color);
                        }
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.nanoTime();
        System.out.printf("Parallel Mandelbrot drawn in %.2f ms%n", (endTime - startTime) / 1_000_000.0);
    }
}
