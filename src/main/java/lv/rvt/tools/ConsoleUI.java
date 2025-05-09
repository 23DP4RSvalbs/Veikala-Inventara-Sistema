package lv.rvt.tools;

public class ConsoleUI {
    // Color codes
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // Background colors
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_PURPLE = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_WHITE = "\u001B[47m";

    // Text styles
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";

    private static final int MIN_COLUMN_WIDTH = 12;
    private static final int MAX_COLUMN_WIDTH = 30;
    private static final String[] NUMERIC_COLUMNS = {"ID", "Cena", "Daudzums"};
    
    private static String[] loadingFrames = {
        "╔════╤╤╤╤════╗\n" +
        "║    │││ \\   ║\n" +
        "║    │││  O  ║  Ielādē\n" +
        "║    OOO     ║  sistēmu...\n" +
        "╚════════════╝",
        
        "╔════╤╤╤╤════╗\n" +
        "║    ││││    ║\n" +
        "║    ││││    ║  Lūdzu\n" +
        "║    OOOO    ║  uzgaidiet...\n" +
        "╚════════════╝",
        
        "╔════╤╤╤╤════╗\n" +
        "║   /│││     ║\n" +
        "║  O │││     ║  Gandrīz\n" +
        "║     OOO    ║  gatavs...\n" +
        "╚════════════╝"
    };

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printHeader(String text) {
        String line = "═".repeat(text.length() + 4);
        System.out.println(CYAN + "╔" + line + "╗" + RESET);
        System.out.println(CYAN + "║ " + YELLOW + BOLD + text + RESET + CYAN + " ║" + RESET);
        System.out.println(CYAN + "╚" + line + "╝" + RESET);
    }

    public static void printMenu(String title, String... options) {
        System.out.println();
        printHeader(title);
        System.out.println();
        for (int i = 0; i < options.length; i++) {
            if (options[i].startsWith("0.")) {
                System.out.println(RED + options[i] + RESET);
            } else {
                System.out.println(GREEN + options[i] + RESET);
            }
        }
        System.out.println();
    }

    public static void printError(String message) {
        System.out.println(RED + "✗ Kļūda: " + message + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "✓ " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }

    public static void printTableHeader(String... columns) {
        int[] widths = calculateColumnWidths(columns);
        
        // Top border
        System.out.print(CYAN + "╔");
        for (int i = 0; i < columns.length; i++) {
            System.out.print("═".repeat(widths[i]));
            System.out.print(i < columns.length - 1 ? "╦" : "╗");
        }
        System.out.println(RESET);
        
        // Column headers
        System.out.print(CYAN + "║");
        for (int i = 0; i < columns.length; i++) {
            String text = " " + columns[i];
            int padding = widths[i] - columns[i].length() - 1;
            System.out.print(WHITE + BOLD + text + " ".repeat(padding));
            System.out.print(CYAN + (i < columns.length - 1 ? "║" : "║"));
        }
        System.out.println(RESET);
        
        // Bottom border
        System.out.print(CYAN + "╠");
        for (int i = 0; i < columns.length; i++) {
            System.out.print("═".repeat(widths[i]));
            System.out.print(i < columns.length - 1 ? "╬" : "╣");
        }
        System.out.println(RESET);
    }

    public static void printTableRow(String... columns) {
        int[] widths = calculateColumnWidths(columns);
        
        System.out.print(CYAN + "║");
        for (int i = 0; i < columns.length; i++) {
            String text = columns[i];
            int padding = widths[i] - text.length();
            
            if (isNumericColumn(i, columns)) {
                // Right align numeric values
                System.out.print(WHITE + " ".repeat(padding - 1) + text + " ");
            } else {
                // Left align text values
                System.out.print(WHITE + " " + text + " ".repeat(padding - 1));
            }
            
            System.out.print(CYAN + (i < columns.length - 1 ? "║" : "║"));
        }
        System.out.println(RESET);
    }

    public static void printTableFooter(int columns) {
        int[] widths = new int[columns];
        for (int i = 0; i < columns; i++) {
            widths[i] = MIN_COLUMN_WIDTH;
        }
        
        System.out.print(CYAN + "╚");
        for (int i = 0; i < columns; i++) {
            System.out.print("═".repeat(widths[i]));
            System.out.print(i < columns - 1 ? "╩" : "╝");
        }
        System.out.println(RESET);
    }

    private static int[] calculateColumnWidths(String... columns) {
        int[] widths = new int[columns.length];
        for (int i = 0; i < columns.length; i++) {
            // Calculate width based on content length, with min/max constraints
            int width = columns[i].length() + 2; // Add padding
            width = Math.max(width, MIN_COLUMN_WIDTH);
            width = Math.min(width, MAX_COLUMN_WIDTH);
            widths[i] = width;
        }
        return widths;
    }

    private static boolean isNumericColumn(int index, String... columns) {
        // Check if the column should be right-aligned based on its header
        if (index >= columns.length) return false;
        for (String numericHeader : NUMERIC_COLUMNS) {
            if (numericHeader.equals(columns[index])) {
                return true;
            }
        }
        return false;
    }

    public static void playLoadingAnimation() {
        try {
            for (int i = 0; i < 3; i++) {
                for (String frame : loadingFrames) {
                    clearScreen();
                    System.out.println(CYAN + frame + RESET);
                    Thread.sleep(300); // Slightly faster animation
                }
            }
            clearScreen();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}