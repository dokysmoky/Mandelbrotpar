/*package primorska.mandlbrotset;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import primorska.mandlbrotset.parallel.ParallelRenderer;
import primorska.mandlbrotset.sequential.SequentialRenderer;

public class MandelbrotApp extends Application {

    private static String mode = "sequential";

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--mode=")) {
                mode = arg.substring("--mode=".length()).toLowerCase();
            }
        }
        launch(args);
    }

    /*@Override
    public void start(Stage stage) throws Exception {
        int width = 800;
        int height = 600;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        AnchorPane root = new AnchorPane(canvas);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Mandelbrot Set - " + mode);
        stage.show();

        switch (mode) {
            case "parallel":
                ParallelRenderer.render(gc, width, height, -2.5, 1.5, -1.5, 1.5, 1.0);
                break;
            case "sequential":
            default:
                SequentialRenderer.render(gc, width, height, -2.5, 1.5, -1.5, 1.5, 1.0);
                break;
        }
    }
    @Override
    public void start(Stage stage) throws Exception {
        int width = 800;
        int height = 600;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        AnchorPane root = new AnchorPane(canvas);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Mandelbrot Set - " + mode);
        stage.show();

        try {
            switch (mode) {
                case "parallel":
                    ParallelRenderer.render(gc, width, height, -2.5, 1.5, -1.5, 1.5, 1.0);
                    break;
                case "sequential":
                default:
                    SequentialRenderer.render(gc, width, height, -2.5, 1.5, -1.5, 1.5, 1.0);
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}*/

package primorska.mandlbrotset;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import primorska.mandlbrotset.renderer.MandelbrotRenderer;
import primorska.mandlbrotset.parallel.ParallelRenderer;
import primorska.mandlbrotset.sequential.SequentialRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MandelbrotApp extends Application {

    private MandelbrotRenderer renderer;

    private double minX = -2.5, maxX = 1.5;
    private double minY = -1.5, maxY = 1.5;
    private double zoomFactor = 1.0;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField widthField;
    private TextField heightField;
    private int imageWidth = 800;
    private int imageHeight = 600;

    @Override
    public void start(Stage primaryStage) {
        // Determine mode from args
        String mode = "sequential";
        Parameters params = getParameters();
        for (String arg : params.getRaw()) {
            if (arg.startsWith("--mode=")) {
                mode = arg.substring("--mode=".length()).toLowerCase();
            }
        }

        switch (mode) {
            case "parallel":
                renderer = new ParallelRenderer();
                break;
            case "sequential":
            default:
                renderer = new SequentialRenderer();
                break;
        }

        canvas = new Canvas(imageWidth, imageHeight);
        gc = canvas.getGraphicsContext2D();

        widthField = new TextField(String.valueOf(imageWidth));
        heightField = new TextField(String.valueOf(imageHeight));
        Button resizeButton = new Button("Resize");
        Button saveButton = new Button("Save");

        resizeButton.setOnAction(e -> handleResize());
        saveButton.setOnAction(e -> handleSave(primaryStage));

        HBox controls = new HBox(10, widthField, heightField, resizeButton, saveButton);
        AnchorPane root = new AnchorPane(canvas, controls);
        AnchorPane.setBottomAnchor(controls, 10.0);
        AnchorPane.setLeftAnchor(controls, 10.0);
        AnchorPane.setRightAnchor(controls, 10.0);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (canvas.isFocused()) {
                switch (event.getCode()) {
                    case ADD:
                    case PLUS:
                        zoomFactor *= 1.5;
                        break;
                    case SUBTRACT:
                    case MINUS:
                        zoomFactor /= 1.5;
                        break;
                    case UP:
                        minY -= 0.1 * (maxY - minY) / zoomFactor;
                        maxY -= 0.1 * (maxY - minY) / zoomFactor;
                        break;
                    case DOWN:
                        minY += 0.1 * (maxY - minY) / zoomFactor;
                        maxY += 0.1 * (maxY - minY) / zoomFactor;
                        break;
                    case LEFT:
                        minX -= 0.1 * (maxX - minX) / zoomFactor;
                        maxX -= 0.1 * (maxX - minX) / zoomFactor;
                        break;
                    case RIGHT:
                        minX += 0.1 * (maxX - minX) / zoomFactor;
                        maxX += 0.1 * (maxX - minX) / zoomFactor;
                        break;
                    default:
                        break;
                }
                drawMandelbrot();
            }
        });

        primaryStage.setOnShown(event -> Platform.runLater(() -> canvas.requestFocus()));
        primaryStage.setTitle("Mandelbrot Set Explorer - " + mode);
        primaryStage.setScene(scene);
        primaryStage.show();

        drawMandelbrot();
    }

    private void drawMandelbrot() {
        try {
            renderer.render(gc, imageWidth, imageHeight, minX, maxX, minY, maxY, zoomFactor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleResize() {
        try {
            imageWidth = Integer.parseInt(widthField.getText());
            imageHeight = Integer.parseInt(heightField.getText());
            canvas.setWidth(imageWidth);
            canvas.setHeight(imageHeight);
            drawMandelbrot();
            canvas.requestFocus();
        } catch (NumberFormatException e) {
            System.out.println("Invalid width or height.");
        }
    }

    private void handleSave(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"),
                new FileChooser.ExtensionFilter("JPG Files", "*.jpg")
        );
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            saveImage(file);
        }
        canvas.requestFocus();
    }

    private void saveImage(File file) {
        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        canvas.snapshot(null, writableImage);
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
            String ext = getExtension(file.getName());
            if (ext == null) ext = "png";
            ImageIO.write(bufferedImage, ext, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        if (i > 0 && i < filename.length() - 1) {
            return filename.substring(i + 1).toLowerCase();
        }
        return null;
    }
}
