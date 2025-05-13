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

    // Constants for box drawing
    private static final int BOX_WIDTH = 60; // Standard width for all boxes
    private static final String TOP_LEFT = "╔";
    private static final String TOP_RIGHT = "╗";
    private static final String BOTTOM_LEFT = "╚";
    private static final String BOTTOM_RIGHT = "╝";
    private static final String HORIZONTAL = "═";
    private static final String VERTICAL = "║";
    private static final String T_DOWN = "╦";
    private static final String T_UP = "╩";
    private static final String T_RIGHT = "╠";
    private static final String T_LEFT = "╣";
    private static final String CROSS = "╬";

    // Fixed column widths for different types
    private static final int ID_WIDTH = 12;
    private static final int NAME_WIDTH = 15;
    private static final int CATEGORY_WIDTH = 11;
    private static final int PRICE_WIDTH = 11;
    private static final int QUANTITY_WIDTH = 11;
    
    // Define which columns are numeric (for right alignment)
    private static final String[] NUMERIC_COLUMNS = {"ID", "Cena", "Daudzums"};
    
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static String centerText(String text, int width) {
        width = Math.max(width, text.length());  // Ensure width is at least as long as text
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - text.length() - padding));
    }

    private static void printBoxLine(String leftChar, String middleChar, String rightChar, int width) {
        width = Math.max(0, width);  // Ensure width is not negative
        System.out.print(CYAN + leftChar + HORIZONTAL.repeat(width) + rightChar + RESET);
        System.out.println();
    }

    public static void printHeader(String text) {
        int contentWidth = BOX_WIDTH - 2; // Account for left and right borders
        String centeredText = centerText(text, contentWidth);
        
        printBoxLine(TOP_LEFT, HORIZONTAL, TOP_RIGHT, contentWidth);
        System.out.println(CYAN + VERTICAL + YELLOW + BOLD + centeredText + RESET + CYAN + VERTICAL + RESET);
        printBoxLine(BOTTOM_LEFT, HORIZONTAL, BOTTOM_RIGHT, contentWidth);
    }

    public static void printMenu(String title, String... options) {
        System.out.println();
        printHeader(title);
        System.out.println();
        
        int contentWidth = BOX_WIDTH - 4; // Account for borders and spacing
        for (String option : options) {
            String centeredOption = centerText(option, contentWidth);
            if (option.startsWith("0.")) {
                System.out.println(RED + "  " + centeredOption + RESET);
            } else {
                System.out.println(GREEN + "  " + centeredOption + RESET);
            }
        }
        System.out.println();
    }

    public static void printMessage(String prefix, String message, String color) {
        int contentWidth = BOX_WIDTH - 2;
        String centeredText = centerText(prefix + " " + message, contentWidth);
        
        printBoxLine(TOP_LEFT, HORIZONTAL, TOP_RIGHT, contentWidth);
        System.out.println(CYAN + VERTICAL + color + centeredText + RESET + CYAN + VERTICAL + RESET);
        printBoxLine(BOTTOM_LEFT, HORIZONTAL, BOTTOM_RIGHT, contentWidth);
    }

    public static void printError(String message) {
        printMessage("✗", message, RED);
    }

    public static void printSuccess(String message) {
        printMessage("✓", message, GREEN);
    }

    public static void printWarning(String message) {
        printMessage("⚠", message, YELLOW);
    }

    public static void printYellow(String message) {
        System.out.print(YELLOW + message + RESET);
    }

    private static String truncateText(String text, int columnIndex, boolean isHeader) {
        if (text == null || text.isEmpty()) return "";
        
        // Get the max length for this column (subtract 2 for padding spaces)
        int maxLength = switch(columnIndex) {
            case 0 -> ID_WIDTH - 2;       // ID column
            case 1 -> NAME_WIDTH - 2;     // Name column
            case 2 -> CATEGORY_WIDTH - 2; // Category column
            case 3 -> PRICE_WIDTH - 2;    // Price column
            case 4 -> QUANTITY_WIDTH - 2; // Quantity column
            default -> NAME_WIDTH - 2;    // Default to name width for any other columns
        };
        
        // Handle empty text or text that fits
        if (text.length() <= maxLength) return text;
        
        // For headers, try to fit by removing spaces
        if (isHeader) {
            String compressedText = text.replaceAll("\\s+", "");
            if (compressedText.length() <= maxLength) return compressedText;
            // If still too long, truncate even headers
        }
        
        // If text is longer than max length, truncate with ...
        return text.substring(0, maxLength - 3) + "...";
    }

    private static String padText(String text, boolean isNumeric, boolean isHeader, int columnIndex) {
        // First truncate if needed
        String processedText = truncateText(text, columnIndex, isHeader);
        
        // Get the appropriate width for this column
        int columnWidth = switch(columnIndex) {
            case 0 -> ID_WIDTH;       // ID column
            case 1 -> NAME_WIDTH;     // Name column
            case 2 -> CATEGORY_WIDTH; // Category column
            case 3 -> PRICE_WIDTH;    // Price column
            case 4 -> QUANTITY_WIDTH; // Quantity column
            default -> NAME_WIDTH;    // Default to name width for any other columns
        };
        
        // Calculate padding needed for the column
        int padding = columnWidth - processedText.length() - 2;  // -2 for spaces on both sides
        
        if (isNumeric) {
            // Right align numbers with exact spacing
            return " " + " ".repeat(Math.max(0, padding)) + processedText + " ";
        } else {
            // Left align text with exact spacing
            return " " + processedText + " ".repeat(Math.max(0, padding)) + " ";
        }
    }

    public static void printTableHeader(String... columns) {
        // Top border
        System.out.print(CYAN + TOP_LEFT);
        for (int i = 0; i < columns.length; i++) {
            int width = switch(i) {
                case 0 -> ID_WIDTH;
                case 1 -> NAME_WIDTH;
                case 2 -> CATEGORY_WIDTH;
                case 3 -> PRICE_WIDTH;
                case 4 -> QUANTITY_WIDTH;
                default -> NAME_WIDTH;
            };
            System.out.print(HORIZONTAL.repeat(width));
            System.out.print(i < columns.length - 1 ? T_DOWN : TOP_RIGHT);
        }
        System.out.println(RESET);
        
        // Column headers
        System.out.print(CYAN + VERTICAL);
        for (int i = 0; i < columns.length; i++) {
            System.out.print(WHITE + BOLD + padText(columns[i], false, true, i) + RESET);
            System.out.print(CYAN + VERTICAL);
        }
        System.out.println(RESET);
        
        // Bottom border
        System.out.print(CYAN + T_RIGHT);
        for (int i = 0; i < columns.length; i++) {
            int width = switch(i) {
                case 0 -> ID_WIDTH;
                case 1 -> NAME_WIDTH;
                case 2 -> CATEGORY_WIDTH;
                case 3 -> PRICE_WIDTH;
                case 4 -> QUANTITY_WIDTH;
                default -> NAME_WIDTH;
            };
            System.out.print(HORIZONTAL.repeat(width));
            System.out.print(i < columns.length - 1 ? CROSS : T_LEFT);
        }
        System.out.println(RESET);
    }

    public static void printTableRow(String... columns) {
        System.out.print(CYAN + VERTICAL);
        for (int i = 0; i < columns.length; i++) {
            String text = columns[i] != null ? columns[i] : "";  // Handle null values
            boolean isNumber = isNumericColumn(i, columns);
            System.out.print(WHITE + padText(text, isNumber, false, i));
            System.out.print(CYAN + VERTICAL);
        }
        System.out.println(RESET);
    }

    public static void printTableFooter(int columns) {
        System.out.print(CYAN + BOTTOM_LEFT);
        for (int i = 0; i < columns; i++) {
            int width = switch(i) {
                case 0 -> ID_WIDTH;
                case 1 -> NAME_WIDTH;
                case 2 -> CATEGORY_WIDTH;
                case 3 -> PRICE_WIDTH;
                case 4 -> QUANTITY_WIDTH;
                default -> NAME_WIDTH;
            };
            System.out.print(HORIZONTAL.repeat(width));
            System.out.print(i < columns - 1 ? T_UP : BOTTOM_RIGHT);
        }
        System.out.println(RESET);
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
            String dots = ".".repeat(30);
            for (int pos = 0; pos < dots.length() - 3; pos += 3) {
                StringBuilder frame = new StringBuilder(dots);
                frame.insert(pos, "📦");
                System.out.print("\r" + frame);
                System.out.flush();
                Thread.sleep(250);
            }
            System.out.println();
            clearScreen();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}