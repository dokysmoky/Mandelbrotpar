module primorska.mandelbrotset {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;       // Needed for SwingFXUtils
    requires java.desktop;       // Needed for AWT and ImageIO

    opens primorska.mandlbrotset to javafx.fxml;
    exports primorska.mandlbrotset;
    exports primorska.mandlbrotset.sequential;
    opens primorska.mandlbrotset.sequential to javafx.fxml;
}
