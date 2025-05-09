package lv.rvt;

import lv.rvt.tools.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Handle console color support
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                // Enable ANSI escape codes on Windows
                try {
                    new ProcessBuilder("cmd", "/c", "").inheritIO().start().waitFor();
                } catch (Exception e) {
                    // Silently continue if we can't enable ANSI
                }
            }

            // Initialize configuration
            ConfigManager config = ConfigManager.getInstance();
            String language = args.length > 0 && args[0].toLowerCase().equals("en") ? "en" : "lv";
            config.setProperty("app.language", language);
            
            // Start the application
            UserInterface ui = new UserInterface();
            ui.start();
        } catch (Exception e) {
            System.err.println(ConsoleUI.RED + "Kritiska kļūda: " + e.getMessage() + ConsoleUI.RESET);
            e.printStackTrace();
            System.exit(1);
        }
    }
}