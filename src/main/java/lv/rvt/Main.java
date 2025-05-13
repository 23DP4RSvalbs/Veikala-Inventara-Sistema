package lv.rvt;

import lv.rvt.tools.*;

// Galvenā klase lietojumprogrammas palaišanai
public class Main {
    public static void main(String[] args) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                try {
                    // Iespējo ANSI escape kodus Windows vidē
                    new ProcessBuilder("cmd", "/c", "").inheritIO().start().waitFor();
                } catch (Exception e) {
                    System.err.println("Neizdevās iespējot ANSI krāsu atbalstu");
                }
            }

            ConfigManager config = ConfigManager.getInstance();
            String language = args.length > 0 && args[0].toLowerCase().equals("en") ? "en" : "lv";
            config.setProperty("app.language", language);
            
            System.out.print("\033[?25l"); // Paslēpj kursoru
            String[] frames = {
                "📦..................",
                "......📦............",
                ".............📦.....",
                "..................📦"
            };
            for (int i = 0; i < 2; i++) {
                for (String frame : frames) {
                    System.out.print("\r" + frame);
                    Thread.sleep(150);
                }
            }
            System.out.print("\r" + " ".repeat(40) + "\r");
            System.out.print("\033[?25h"); // Parāda kursoru

            // Palaiž lietojumprogrammu
            UserInterface ui = new UserInterface();
            ui.start();
        } catch (Exception e) {
            System.err.println(ConsoleUI.RED + "Kritiska kļūda: " + e.getMessage() + ConsoleUI.RESET);
            e.printStackTrace();
            System.exit(1);
        }
    }
}