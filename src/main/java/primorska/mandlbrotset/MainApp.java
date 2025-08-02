package primorska.mandlbrotset;

import primorska.mandlbrotset.sequential.SequentialRenderer;

public class MainApp {
    public static void main(String[] args) {
        String mode = "sequential"; // default

        for (String arg : args) {
            if (arg.startsWith("--mode=")) {
                mode = arg.substring("--mode=".length()).toLowerCase();
            }
        }

        switch (mode) {
            case "sequential":
                SequentialRenderer.launch(SequentialRenderer.class, args);
                break;
            case "parallel":
                System.out.println("Parallel mode not yet implemented.");
                break;
            case "distributed":
                System.out.println("Distributed mode not yet implemented.");
                break;
            default:
                System.out.println("Invalid mode. Use --mode=sequential|parallel|distributed");
        }
    }
}
