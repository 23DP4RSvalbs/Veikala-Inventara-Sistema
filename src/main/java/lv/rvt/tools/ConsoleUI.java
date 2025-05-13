package lv.rvt.tools;

public class ConsoleUI {
    // ANSI color codes
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String WHITE = "\u001B[37m";

    private static final int MIN_TABLE_WIDTH = 80;
    private static final int DEFAULT_COLUMN_WIDTH = 20;
    private static final String TABLE_BORDER = "═";
    private static final String TABLE_VERTICAL = "║";
    private static final String TABLE_TOP_LEFT = "╔";
    private static final String TABLE_TOP_RIGHT = "╗";
    private static final String TABLE_BOTTOM_LEFT = "╚";
    private static final String TABLE_BOTTOM_RIGHT = "╝";
    private static final String TABLE_CROSS = "╬";
    private static final String TABLE_T_DOWN = "╦";
    private static final String TABLE_T_UP = "╩";
    private static final String TABLE_T_RIGHT = "╠";
    private static final String TABLE_T_LEFT = "╣";

    public static void printTableHeader(String... headers) {
        int[] columnWidths = new int[headers.length];
        
        // Calculate required column widths
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = Math.max(DEFAULT_COLUMN_WIDTH, headers[i].length() + 2);
        }
        
        // Print top border
        System.out.print(BLUE + TABLE_TOP_LEFT);
        for (int i = 0; i < headers.length; i++) {
            System.out.print(TABLE_BORDER.repeat(columnWidths[i]));
            System.out.print(i < headers.length - 1 ? TABLE_T_DOWN : TABLE_TOP_RIGHT);
        }
        System.out.print(RESET);
        System.out.println();
        
        // Print headers
        System.out.print(BLUE + TABLE_VERTICAL + RESET);
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            int padding = (columnWidths[i] - header.length()) / 2;
            System.out.print(" ".repeat(padding) + BLUE + header + RESET + " ".repeat(columnWidths[i] - header.length() - padding));
            System.out.print(BLUE + TABLE_VERTICAL + RESET);
        }
        System.out.println();
        
        // Print separator
        System.out.print(BLUE + TABLE_T_RIGHT);
        for (int i = 0; i < headers.length; i++) {
            System.out.print(TABLE_BORDER.repeat(columnWidths[i]));
            System.out.print(i < headers.length - 1 ? TABLE_CROSS : TABLE_T_LEFT);
        }
        System.out.print(RESET);
        System.out.println();
    }

    public static void printTableRow(String... columns) {
        System.out.print(BLUE + TABLE_VERTICAL + RESET);
        for (int i = 0; i < columns.length; i++) {
            String text = columns[i];
            if (text.length() > DEFAULT_COLUMN_WIDTH - 2) {
                text = text.substring(0, DEFAULT_COLUMN_WIDTH - 5) + "...";
            }
            System.out.print(" " + WHITE + text + RESET);
            System.out.print(" ".repeat(Math.max(0, DEFAULT_COLUMN_WIDTH - text.length() - 1)));
            System.out.print(BLUE + TABLE_VERTICAL + RESET);
        }
        System.out.println();
    }

    public static void printTableFooter(int columnCount) {
        System.out.print(BLUE + TABLE_BOTTOM_LEFT);
        for (int i = 0; i < columnCount; i++) {
            System.out.print(TABLE_BORDER.repeat(DEFAULT_COLUMN_WIDTH));
            System.out.print(i < columnCount - 1 ? TABLE_T_UP : TABLE_BOTTOM_RIGHT);
        }
        System.out.print(RESET);
        System.out.println();
    }

    public static void printHeader(String text) {
        int padding = (MIN_TABLE_WIDTH - text.length()) / 2;
        System.out.println("\n" + TABLE_TOP_LEFT + TABLE_BORDER.repeat(MIN_TABLE_WIDTH) + TABLE_TOP_RIGHT);
        System.out.println(TABLE_VERTICAL + " ".repeat(padding) + BLUE + text + RESET + " ".repeat(MIN_TABLE_WIDTH - text.length() - padding) + TABLE_VERTICAL);
        System.out.println(TABLE_BOTTOM_LEFT + TABLE_BORDER.repeat(MIN_TABLE_WIDTH) + TABLE_BOTTOM_RIGHT + "\n");
    }

    public static void printMenu(String header, String[] options) {
        printHeader(header);
        for (String option : options) {
            if (option.startsWith("0.") || option.contains("Atcelt") || option.contains("Iziet")) {
                System.out.println("  " + option.replace(BLUE, RED));
            } else {
                System.out.println("  " + option);
            }
        }
        System.out.println();
    }

    public static void printError(String message) {
        int padding = (MIN_TABLE_WIDTH - message.length()) / 2;
        System.out.println("\n" + RED + TABLE_TOP_LEFT + TABLE_BORDER.repeat(MIN_TABLE_WIDTH) + TABLE_TOP_RIGHT + RESET);
        System.out.println(RED + TABLE_VERTICAL + RESET + " ".repeat(padding) + RED + message + RESET + " ".repeat(MIN_TABLE_WIDTH - message.length() - padding) + RED + TABLE_VERTICAL + RESET);
        System.out.println(RED + TABLE_BOTTOM_LEFT + TABLE_BORDER.repeat(MIN_TABLE_WIDTH) + TABLE_BOTTOM_RIGHT + RESET + "\n");
    }

    public static void printSuccess(String message) {
        int padding = (MIN_TABLE_WIDTH - message.length()) / 2;
        System.out.println("\n" + BLUE + TABLE_TOP_LEFT + TABLE_BORDER.repeat(MIN_TABLE_WIDTH) + TABLE_TOP_RIGHT + RESET);
        System.out.println(BLUE + TABLE_VERTICAL + RESET + " ".repeat(padding) + BLUE + message + RESET + " ".repeat(MIN_TABLE_WIDTH - message.length() - padding) + BLUE + TABLE_VERTICAL + RESET);
        System.out.println(BLUE + TABLE_BOTTOM_LEFT + TABLE_BORDER.repeat(MIN_TABLE_WIDTH) + TABLE_BOTTOM_RIGHT + RESET + "\n");
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}