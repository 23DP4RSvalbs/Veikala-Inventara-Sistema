package lv.rvt;

import java.util.*;
import lv.rvt.tools.*;

public class UserInterface {
    private final InventoryManager manager;
    private final FileManager fileManager;
    private final Scanner scanner;
    private final MessageManager messages;

    public UserInterface() {
        this.manager = new InventoryManager();
        this.fileManager = new FileManager(manager);
        this.scanner = new Scanner(System.in);
        this.messages = MessageManager.getInstance();
    }

    public void start() {
        ConsoleUI.playLoadingAnimation();
        while (true) {
            ConsoleUI.clearScreen();
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                if (choice.toLowerCase().equals("1")) {
                    filterInventory();
                } else if (choice.toLowerCase().equals("2")) {
                    addProduct();
                } else if (choice.toLowerCase().equals("3")) {
                    addCategory();
                } else if (choice.toLowerCase().equals("4")) {
                    editProduct();
                } else if (choice.toLowerCase().equals("5")) {
                    deleteProduct();
                } else if (choice.toLowerCase().equals("6")) {
                    dataExportImport();
                } else if (choice.toLowerCase().equals("7")) {
                    showSettings();
                } else if (choice.toLowerCase().equals("8")) {
                    searchInventory();
                } else if (choice.toLowerCase().equals("9")) {
                    calculateInventory();
                } else if (choice.toLowerCase().equals("0")) {
                    exit();
                    return;
                } else {
                    ConsoleUI.printError("Nederīga izvēle");
                }
            } catch (Exception e) {
                ConsoleUI.printError(e.getMessage());
            }
        }
    }

    private void displayMainMenu() {
        ConsoleUI.printMenu(messages.getString("app.title"),
            "1. Filtrēt inventāru",
            "2. " + messages.getString("menu.add.product"),
            "3. " + messages.getString("categories.add"),
            "4. " + messages.getString("menu.edit.product"),
            "5. " + messages.getString("menu.delete.product"),
            "6. " + messages.getString("menu.data"),
            "7. " + messages.getString("menu.settings"),
            "8. Meklēt inventāru",
            "9. Aprēķināt inventāru",
            "0. " + messages.getString("menu.exit"));
    }

    private void filterInventory() {
        fileManager.checkAndClearIfFilesDeleted();
        String[] options = {
            "1. Parādīt visus produktus",
            "2. Parādīt visas kategorijas",
            "3. Filtrēt produktus pēc cenas",
            "4. Filtrēt produktus pēc kategorijas",
            "5. Filtrēt produktus pēc daudzuma",
            "0. Atcelt"
        };
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen("FILTRĒT INVENTĀRU", options);
            ConsoleUI.printMenu(currentScreen, options);
            
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) break;

            if (choice.equals("1")) {
                displayFilteredProducts(manager.getProducts(), "id", "asc");
            } else if (choice.equals("2")) {
                displayCategories();
            } else if (choice.equals("3")) {
                filterByPrice();
            } else if (choice.equals("4")) {
                filterByCategory();
            } else if (choice.equals("5")) {
                filterByQuantity();
            } else {
                ConsoleUI.printError("Nederīga izvēle");
            }
            System.out.println("\nNospiediet Enter, lai turpinātu...");
            scanner.nextLine();
        }
    }

    private void displayFilteredProducts(List<Product> products, String sortBy, String order) {
        if (products.isEmpty()) {
            ConsoleUI.printWarning("Nav atrastu produktu");
            return;
        }

        List<Product> sorted = manager.sortProducts(sortBy, order);
        ConsoleUI.printTableHeader("ID", "Nosaukums", "Kategorija", "Cena", "Daudzums");
        
        for (Product p : sorted) {
            if (products.contains(p)) {
                ConsoleUI.printTableRow(
                    String.valueOf(p.getId()),
                    p.getName(),
                    p.getCategory(),
                    String.format("%.2f", p.getPrice()),
                    String.valueOf(p.getQuantity())
                );
            }
        }
        ConsoleUI.printTableFooter(5);
    }

    private void displayCategories() {
        ConsoleUI.printTableHeader("Kategorija");
        for (Category category : manager.getCategories()) {
            ConsoleUI.printTableRow(category.getName());
        }
        ConsoleUI.printTableFooter(1);
    }

    private String getValidatedInput(String prompt, boolean allowEmpty) {
        String lastInput = "";
        while (true) {
            ConsoleUI.clearScreen();
            // If there was a previous invalid input, redisplay the current menu/screen
            if (!lastInput.isEmpty()) {
                displayCurrentScreen();
            }
            ConsoleUI.printYellow(prompt + ": ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("0") || input.equalsIgnoreCase("Atcelt")) return "Atcelt";
            if (allowEmpty && input.isEmpty()) return "";
            
            if (Helper.validateInput(input)) {
                return input;
            }
            
            ConsoleUI.printWarning("⚠ Nosaukumā atļauti tikai burti un _ simbols");
            scanner.nextLine(); // Wait for Enter
            lastInput = input;
        }
    }

    private String getValidatedNumericInput(String prompt, boolean allowEmpty) {
        String lastInput = "";
        while (true) {
            ConsoleUI.clearScreen();
            // If there was a previous invalid input, redisplay the current menu/screen
            if (!lastInput.isEmpty()) {
                displayCurrentScreen();
            }
            System.out.print(ConsoleUI.YELLOW + prompt + ConsoleUI.RESET);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("Atcelt")) return "Atcelt";
            if (allowEmpty && input.isEmpty()) return "";
            
            if (Helper.validateNumericInput(input)) {
                double value = Double.parseDouble(input);
                if (value >= 0) {
                    return input;
                }
            }
            lastInput = input;
        }
    }

    // Track the current screen/menu being displayed
    private String currentScreen = "";
    private String[] currentOptions = null;

    private void setCurrentScreen(String screen, String... options) {
        this.currentScreen = screen;
        this.currentOptions = options;
    }

    private void displayCurrentScreen() {
        if (currentScreen != null && !currentScreen.isEmpty()) {
            if (currentOptions != null && currentOptions.length > 0) {
                ConsoleUI.printMenu(currentScreen, currentOptions);
            } else {
                ConsoleUI.printHeader(currentScreen);
            }
        }
    }

    private String getProductName() {
        while (true) {
            ConsoleUI.clearScreen();
            displayCurrentScreen();
            
            String name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("Atcelt")) return null;
            
            // Name validation: letters and underscores only
            if (!name.matches("^[a-zA-Z_]+$")) {
                continue;
            }
            
            // Check for duplicate names
            if (manager.getProducts().stream().anyMatch(p -> p.getName().equalsIgnoreCase(name))) {
                ConsoleUI.printWarning("⚠ Produkts ar šādu nosaukumu jau eksistē");
                scanner.nextLine();
                continue;
            }
            
            return name;
        }
    }

    private String getProductCategory() {
        while (true) {
            ConsoleUI.clearScreen();
            displayCurrentScreen();
            displayCategories();
            System.out.println();
            
            String category = scanner.nextLine().trim();
            if (category.equalsIgnoreCase("Atcelt")) return null;
            
            // Category validation: letters and underscores only
            if (!category.matches("^[a-zA-Z_]+$")) {
                continue;
            }
            
            // Silent continue if category doesn't exist
            if (!manager.categoryExists(category)) {
                continue;
            }
            
            return category;
        }
    }

    private Double getProductPrice() {
        while (true) {
            ConsoleUI.clearScreen();
            displayCurrentScreen();
            System.out.print(ConsoleUI.YELLOW + messages.getString("product.price.prompt") + ConsoleUI.RESET);
            
            String priceStr = scanner.nextLine().trim();
            if (priceStr.equalsIgnoreCase("Atcelt")) return null;
            
            // Price validation: format 00000000.00
            if (!priceStr.matches("^\\d{1,8}(\\.\\d{0,2})?$")) {
                ConsoleUI.printWarning("⚠ Cena jābūt formātā: 00000000.00");
                scanner.nextLine();
                continue;
            }
            
            double price = Double.parseDouble(priceStr);
            if (price < 0 || price > 99999999.99) {
                continue;
            }
            
            return price;
        }
    }

    private Integer getProductQuantity() {
        while (true) {
            ConsoleUI.clearScreen();
            displayCurrentScreen();
            System.out.print(ConsoleUI.YELLOW + messages.getString("product.quantity.prompt") + ConsoleUI.RESET);
            
            String quantityStr = scanner.nextLine().trim();
            if (quantityStr.equalsIgnoreCase("Atcelt")) return null;
            
            // Quantity validation: format 00000000
            if (!quantityStr.matches("^\\d{1,8}$")) {
                ConsoleUI.printWarning("⚠ Daudzumam jābūt formātā: 00000000");
                scanner.nextLine();
                continue;
            }
            
            int quantity = Integer.parseInt(quantityStr);
            if (quantity < 0 || quantity > 99999999) {
                continue;
            }
            
            return quantity;
        }
    }

    private void addProduct() {
        setCurrentScreen(messages.getString("product.add.header"));
        
        System.out.print(ConsoleUI.YELLOW + messages.getString("product.name.prompt") + ConsoleUI.RESET);
        final String name = getProductName();
        if (name == null) return;
        
        System.out.print(ConsoleUI.YELLOW + messages.getString("product.category.prompt") + ConsoleUI.RESET);
        final String category = getProductCategory();
        if (category == null) return;
        
        final Double price = getProductPrice();
        if (price == null) return;
        
        final Integer quantity = getProductQuantity();
        if (quantity == null) return;
        
        try {
            manager.addProduct(name, category, price, quantity);
            ConsoleUI.printSuccess(messages.getString("product.add.success"));
            System.out.println("\nNospiediet Enter, lai turpinātu...");
            scanner.nextLine();
        } catch (IllegalArgumentException e) {
            // Silently return to main menu on error
            return;
        }
    }

    private void addCategory() {
        String[] options = {messages.getString("menu.back")};
        
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("categories.header"), options);
            displayCategories();
            System.out.println();
            
            String name = getValidatedInput(messages.getString("categories.name.prompt"), true);
            
            // Handle cancel/empty input silently
            if (name.isEmpty() || name.equalsIgnoreCase("Atcelt")) {
                return;
            }
            
            // Validate category name format silently (letters and underscores only)
            if (!name.matches("^[a-zA-Z_]+$")) {
                continue;
            }
            
            // Check for duplicate categories
            if (manager.categoryExists(name)) {
                ConsoleUI.printWarning("⚠ " + messages.getString("categories.exists"));
                continue;
            }
            
            try {
                manager.addCategory(name);
                ConsoleUI.printSuccess(messages.getString("categories.add.success"));
                System.out.println("\nNospiediet Enter, lai turpinātu...");
                scanner.nextLine();
                return;
            } catch (IllegalArgumentException e) {
                ConsoleUI.printError(e.getMessage());
                continue;
            }
        }
    }

    private void editProduct() {
        String[] options = {messages.getString("menu.back")};
        Product product = null;
        
        // Product ID input loop
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("menu.edit.product"), options);
            displayFilteredProducts(manager.getProducts(), "id", "asc");
            System.out.println();
            
            String idStr = getValidatedInput(messages.getString("product.id.prompt"), true);
            
            // Handle cancel/empty input silently
            if (idStr.isEmpty() || idStr.equalsIgnoreCase("Atcelt")) {
                return;
            }
            
            // Validate numeric input silently
            if (!idStr.matches("^\\d+$")) {
                continue;
            }
            
            // Validate product exists
            try {
                int id = Integer.parseInt(idStr);
                product = manager.findProductById(id);
                if (product == null) {
                    ConsoleUI.printWarning("⚠ " + messages.getString("product.not.found"));
                    continue;
                }
                break; // Valid product found, proceed to editing
            } catch (NumberFormatException e) {
                continue;
            }
        }
        
        // Edit product details
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("menu.edit.product"), options);
            System.out.println(ConsoleUI.YELLOW + messages.getString("product.edit.instructions") + ConsoleUI.RESET);
            
            // Name input with validation
            String name = getValidatedInput(messages.getString("product.name.prompt"), true);
            if (name.equalsIgnoreCase("Atcelt")) return;
            if (!name.isEmpty() && !name.matches("^[a-zA-Z_]+$")) {
                continue;
            }
            
            // Category input with validation
            displayCategories();
            String category = getValidatedInput(messages.getString("product.category.prompt"), true);
            if (category.equalsIgnoreCase("Atcelt")) return;
            if (!category.isEmpty()) {
                if (!category.matches("^[a-zA-Z_]+$")) continue;
                if (!manager.categoryExists(category)) {
                    ConsoleUI.printWarning("⚠ " + messages.getString("product.category.invalid"));
                    continue;
                }
            }
            
            // Price input with validation
            String priceStr = getValidatedInput(messages.getString("product.price.prompt"), true);
            if (priceStr.equalsIgnoreCase("Atcelt")) return;
            double price = -1;
            if (!priceStr.isEmpty()) {
                if (!priceStr.matches("^\\d{1,8}(\\.\\d{0,2})?$")) continue;
                try {
                    price = Double.parseDouble(priceStr);
                    if (price < 0 || price > 99999999.99) continue;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            
            // Quantity input with validation
            String quantityStr = getValidatedInput(messages.getString("product.quantity.prompt"), true);
            if (quantityStr.equalsIgnoreCase("Atcelt")) return;
            int quantity = -1;
            if (!quantityStr.isEmpty()) {
                if (!quantityStr.matches("^\\d{1,8}$")) continue;
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity < 0 || quantity > 99999999) continue;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            
            // Update product
            try {
                manager.editProduct(product.getId(), 
                                  name.isEmpty() ? null : name,
                                  category.isEmpty() ? null : category,
                                  price,
                                  quantity);
                ConsoleUI.printSuccess(messages.getString("product.edit.success"));
                System.out.println("\nNospiediet Enter, lai turpinātu...");
                scanner.nextLine();
                return;
            } catch (IllegalArgumentException e) {
                ConsoleUI.printError(e.getMessage());
                continue;
            }
        }
    }

    private void deleteProduct() {
        String[] options = {messages.getString("menu.back")};
        Product product = null;
        
        // Product ID input loop
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("menu.delete.product"), options);
            displayFilteredProducts(manager.getProducts(), "id", "asc");
            System.out.println();
            
            String idStr = getValidatedInput(messages.getString("product.delete.prompt"), true);
            
            // Handle cancel/empty input silently
            if (idStr.isEmpty() || idStr.equalsIgnoreCase("Atcelt")) {
                return;
            }
            
            // Validate numeric input silently
            if (!idStr.matches("^\\d+$")) {
                continue;
            }
            
            // Validate product exists
            try {
                int id = Integer.parseInt(idStr);
                product = manager.findProductById(id);
                if (product == null) {
                    ConsoleUI.printWarning("⚠ " + messages.getString("product.not.found"));
                    continue;
                }
                break; // Valid product found, proceed to confirmation
            } catch (NumberFormatException e) {
                continue;
            }
        }
        
        // Confirmation loop
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("menu.delete.product"), options);
            displayFilteredProducts(manager.getProducts(), "id", "asc");
            System.out.println();
            
            System.out.print(ConsoleUI.YELLOW + 
                           messages.getString("product.delete.confirm") + 
                           " (j/n): " + ConsoleUI.RESET);
            
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            // Handle input
            if (confirm.isEmpty()) {
                continue;
            }
            
            if (confirm.equals("n")) {
                return;
            }
            
            if (confirm.equals("j")) {
                try {
                    manager.deleteProduct(product.getId());
                    ConsoleUI.printSuccess("✓ " + messages.getString("product.delete.success"));
                    System.out.println("\nNospiediet Enter, lai turpinātu...");
                    scanner.nextLine();
                    return;
                } catch (IllegalArgumentException e) {
                    ConsoleUI.printError(e.getMessage());
                    continue;
                }
            }
            
            // Invalid input, loop silently
            continue;
        }
    }

    private void dataExportImport() {
        String[] options = {
            "1. " + messages.getString("data.export"),
            "2. " + messages.getString("data.import"),
            "0. " + messages.getString("menu.back")
        };
        
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("data.header").toUpperCase(), options);
            System.out.println();
            
            System.out.print(ConsoleUI.YELLOW + messages.getString("menu.choice.prompt") + ConsoleUI.RESET);
            String choice = scanner.nextLine().trim();
            
            // Handle empty input or invalid numbers silently
            if (choice.isEmpty() || !choice.matches("[0-2]")) {
                continue;
            }
            
            // Handle menu exit
            if (choice.equals("0")) {
                return;
            }
            
            try {
                // Handle export
                if (choice.equals("1")) {
                    fileManager.exportData("csv");
                    ConsoleUI.printSuccess("✓ " + messages.getString("export.success"));
                    System.out.println("\nNospiediet Enter, lai turpinātu...");
                    scanner.nextLine();
                    return;
                }
                
                // Handle import
                if (choice.equals("2")) {
                    fileManager.importData("csv");
                    ConsoleUI.printSuccess("✓ " + messages.getString("import.success"));
                    System.out.println("\nNospiediet Enter, lai turpinātu...");
                    scanner.nextLine();
                    return;
                }
            } catch (Exception e) {
                ConsoleUI.printError("⚠ " + e.getMessage());
                System.out.println("\nNospiediet Enter, lai turpinātu...");
                scanner.nextLine();
                continue;
            }
        }
    }

    private void searchInventory() {
        String[] options = {
            "1. " + messages.getString("search.by.name"),
            "2. " + messages.getString("search.by.category"),
            "3. " + messages.getString("search.by.price.range"),
            "0. " + messages.getString("menu.back")
        };

        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("search.header"), options);
            ConsoleUI.printMenu(currentScreen, options);
            
            String choice = scanner.nextLine().trim();
            
            // Silent return for "0" choice
            if (choice.equals("0")) {
                return;
            }
            
            // Handle valid choices silently
            switch (choice) {
                case "1" -> searchByName();
                case "2" -> searchByCategory();
                case "3" -> searchByPriceRange();
                default -> {
                    // Invalid input - silently loop back
                    continue;
                }
            }
        }
    }

    private void searchByName() {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printYellow(messages.getString("search.name.prompt") + ": ");
            String name = scanner.nextLine().trim();
            
            if (name.isEmpty() || name.equalsIgnoreCase("0")) {
                return;
            }
            
            List<Product> results = manager.searchByName(name);
            if (results.isEmpty()) {
                ConsoleUI.printWarning("⚠ " + messages.getString("search.no.results"));
                scanner.nextLine(); // Wait for Enter
                continue;
            }
            
            displayFilteredProducts(results, "name", "asc");
            scanner.nextLine(); // Wait for Enter before returning
            return;
        }
    }

    private void searchByCategory() {
        while (true) {
            ConsoleUI.clearScreen();
            displayCategories();
            ConsoleUI.printYellow(messages.getString("search.category.prompt") + ": ");
            String category = scanner.nextLine().trim();
            
            if (category.isEmpty() || category.equalsIgnoreCase("0")) {
                return;
            }
            
            if (!manager.categoryExists(category)) {
                ConsoleUI.printWarning("⚠ " + messages.getString("product.category.invalid"));
                scanner.nextLine(); // Wait for Enter
                continue;
            }
            
            List<Product> results = manager.searchByCategory(category);
            if (results.isEmpty()) {
                ConsoleUI.printWarning("⚠ " + messages.getString("search.no.results"));
                scanner.nextLine(); // Wait for Enter
                continue;
            }
            
            displayFilteredProducts(results, "name", "asc");
            scanner.nextLine(); // Wait for Enter before returning
            return;
        }
    }

    private void searchByPriceRange() {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printYellow(messages.getString("search.price.min") + ": ");
            String minStr = scanner.nextLine().trim();
            
            if (minStr.isEmpty() || minStr.equalsIgnoreCase("0")) {
                return;
            }
            
            // Validate min price format
            if (!minStr.matches("\\d{1,8}(\\.\\d{0,2})?")) {
                ConsoleUI.printWarning("⚠ " + messages.getString("error.price.format"));
                scanner.nextLine();
                continue;
            }
            
            double min;
            try {
                min = Double.parseDouble(minStr);
            } catch (NumberFormatException e) {
                continue; // Silently retry
            }

            ConsoleUI.printYellow(messages.getString("search.price.max") + ": ");
            String maxStr = scanner.nextLine().trim();
            
            if (maxStr.isEmpty() || maxStr.equalsIgnoreCase("0")) {
                return;
            }
            
            // Validate max price format
            if (!maxStr.matches("\\d{1,8}(\\.\\d{0,2})?")) {
                ConsoleUI.printWarning("⚠ " + messages.getString("error.price.format"));
                scanner.nextLine();
                continue;
            }
            
            double max;
            try {
                max = Double.parseDouble(maxStr);
            } catch (NumberFormatException e) {
                continue; // Silently retry
            }
            
            if (max < min) {
                ConsoleUI.printWarning("⚠ " + messages.getString("error.price.range"));
                scanner.nextLine();
                continue;
            }
            
            List<Product> results = manager.searchByPriceRange(min, max);
            if (results.isEmpty()) {
                ConsoleUI.printWarning("⚠ " + messages.getString("search.no.results"));
                scanner.nextLine(); // Wait for Enter
                continue;
            }
            
            displayFilteredProducts(results, "price", "asc");
            scanner.nextLine(); // Wait for Enter before returning
            return;
        }
    }

    private void filterByPrice() {
        String[] options = {
            "1. " + messages.getString("products.sort.price.asc"),
            "2. " + messages.getString("products.sort.price.desc"),
            "0. " + messages.getString("menu.back")
        };

        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("products.sort.header"), options);
            ConsoleUI.printMenu(currentScreen, options);
            
            String choice = scanner.nextLine().trim();
            
            // Silent return for cancel option
            if (choice.equals("0")) {
                return;
            }
            
            // Handle valid choices
            if (choice.equals("1") || choice.equals("2")) {
                ConsoleUI.clearScreen();
                displayFilteredProducts(
                    manager.getProducts(), 
                    "price", 
                    choice.equals("1") ? "asc" : "desc"
                );
                System.out.println("\nNospiediet Enter, lai turpinātu...");
                scanner.nextLine();
                return;
            }
            
            // Invalid input - loop silently
        }
    }

    private void filterByCategory() {
        String[] options = {messages.getString("menu.back")};

        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("products.filter.category"), options);
            displayCategories();
            System.out.println();
            
            String category = getValidatedInput(messages.getString("search.category.prompt"), true);
            
            // Silent return for empty input or cancel option
            if (category.isEmpty() || category.equalsIgnoreCase("Atcelt")) {
                return;
            }
            
            // Silently handle invalid category names (only letters, spaces, underscores)
            if (!category.matches("^[\\p{L}\\s_]+$")) {
                continue;
            }
            
            // Silent continue if category doesn't exist
            if (!manager.categoryExists(category)) {
                continue;
            }
            
            List<Product> results = manager.searchByCategory(category);
            if (!results.isEmpty()) {
                ConsoleUI.clearScreen();
                displayFilteredProducts(results, "name", "asc");
                System.out.println("\nNospiediet Enter, lai turpinātu...");
                scanner.nextLine();
            }
            return;
        }
    }

    private void filterByQuantity() {
        String[] options = {
            "1. " + messages.getString("products.sort.quantity") + " ↑",
            "2. " + messages.getString("products.sort.quantity") + " ↓",
            "0. " + messages.getString("menu.back")
        };

        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("products.sort.header"), options);
            ConsoleUI.printMenu(currentScreen, options);
            
            String choice = scanner.nextLine().trim();
            
            // Silent return for cancel option
            if (choice.equals("0")) {
                return;
            }
            
            // Handle valid choices
            if (choice.equals("1") || choice.equals("2")) {
                ConsoleUI.clearScreen();
                displayFilteredProducts(
                    manager.getProducts(), 
                    "quantity", 
                    choice.equals("1") ? "asc" : "desc"
                );
                System.out.println("\nNospiediet Enter, lai turpinātu...");
                scanner.nextLine();
                return;
            }
            
            // Invalid or empty input - loop silently
            if (choice.isEmpty() || (!choice.equals("1") && !choice.equals("2"))) {
                continue;
            }
        }
    }

    private void configureBackupChanges() {
        String[] options = {
            "1. Katru reizi",
            "2. Ik pēc 5 izmaiņām",
            "3. Ik pēc 10 izmaiņām",
            "4. Ik pēc 20 izmaiņām",
            "0. Atcelt"
        };
        
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen("REZERVES KOPIJU IESTATĪJUMI", options);
            System.out.println();
            
            System.out.print(ConsoleUI.YELLOW + messages.getString("menu.choice.prompt") + ConsoleUI.RESET);
            String choice = scanner.nextLine().trim();
            
            // Handle empty input or invalid numbers silently
            if (choice.isEmpty() || !choice.matches("[0-4]")) {
                continue;
            }
            
            // Handle menu exit
            if (choice.equals("0")) {
                return;
            }
            
            int changes = switch (choice) {
                case "1" -> 1;
                case "2" -> 5;
                case "3" -> 10;
                case "4" -> 20;
                default -> -1;
            };
            
            if (changes > 0) {
                // Update BackupConfig
                BackupConfig config = BackupConfig.getInstance();
                config.setChangesBeforeBackup(changes);
                config.resetChanges();
                
                // Save to application.properties
                ConfigManager.getInstance().setProperty("backup.interval", String.valueOf(changes));
                
                // Show success message
                String message = switch (changes) {
                    case 1 -> "✓ Rezerves kopijas tiks veidotas pēc katras izmaiņas";
                    default -> String.format("✓ Rezerves kopijas tiks veidotas pēc %d izmaiņām", changes);
                };
                ConsoleUI.printSuccess(message);
                
                System.out.println("\nNospiediet Enter, lai turpinātu...");
                scanner.nextLine();
                return;
            }
        }
    }

    private void showSettings() {
        String[] options = {
            "1. Izveidot rezerves kopiju",
            "2. Nomainīt rezerves kopiju veidošanu",
            "3. Lietošanas Nosacījumi",
            "0. Atcelt"
        };
        
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen("IESTATĪJUMI", options);
            System.out.println();
            
            System.out.print(ConsoleUI.YELLOW + messages.getString("menu.choice.prompt") + ConsoleUI.RESET);
            String choice = scanner.nextLine().trim();
            
            // Handle empty input or invalid numbers silently
            if (choice.isEmpty() || !choice.matches("[0-3]")) {
                continue;
            }
            
            // Handle menu exit
            if (choice.equals("0")) {
                return;
            }
            
            try {
                switch (choice) {
                    case "1" -> {
                        RecoveryManager.createBackup();
                        BackupConfig.getInstance().resetChanges();
                        ConsoleUI.printSuccess("✓ Rezerves kopija veiksmīgi izveidota");
                        System.out.println("\nNospiediet Enter, lai turpinātu...");
                        scanner.nextLine();
                    }
                    case "2" -> {
                        configureBackupChanges();
                    }
                    case "3" -> {
                        showUsageInstructions();
                        System.out.println("\nNospiediet Enter, lai turpinātu...");
                        scanner.nextLine();
                    }
                }
            } catch (Exception e) {
                ConsoleUI.printError("⚠ " + e.getMessage());
                System.out.println("\nNospiediet Enter, lai turpinātu...");
                scanner.nextLine();
                continue;
            }
        }
    }

    private void showUsageInstructions() {
        String[] options = {messages.getString("menu.back")};
        
        ConsoleUI.clearScreen();
        setCurrentScreen("LIETOŠANAS NOSACĪJUMI", options);
        
        // Use yellow for section headers instead of cyan for consistency
        System.out.println(ConsoleUI.YELLOW + "\nProduktu un Kategoriju Nosaukumi:" + ConsoleUI.RESET);
        System.out.println("   - Atļauti tikai burti (a-z, A-Z)");
        System.out.println("   - Atļauts pasvītrojuma simbols (_)");
        System.out.println("   - NAV atļauti: cipari, atstarpes un citi simboli");
        
        System.out.println(ConsoleUI.YELLOW + "\nSkaitliskās Vērtības:" + ConsoleUI.RESET);
        System.out.println("   - Cenai jābūt pozitīvai (max: 99999999.99)");
        System.out.println("   - Daudzumam jābūt pozitīvam (max: 99999999)");
        
        System.out.println(ConsoleUI.YELLOW + "\nValidācijas Nosacījumi:" + ConsoleUI.RESET);
        System.out.println("   - Nosaukumi: tikai burti un _ simbols");
        System.out.println("   - Kategorijas: tikai burti un _ simbols");
        System.out.println("   - Cena: līdz 8 cipariem, 2 decimālie");
        System.out.println("   - Daudzums: līdz 8 cipariem");
        
        System.out.println(ConsoleUI.YELLOW + "\nRezerves Kopijas:" + ConsoleUI.RESET);
        System.out.println("   - Automātiska veidošana pēc noteikta skaita izmaiņu");
        System.out.println("   - Tiek saglabātas data/backup/ mapē");
        System.out.println("   - Var veidot manuāli caur iestatījumiem");
    }

    private void calculateInventory() {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("INVENTĀRA APRĒĶINI");
        
        double totalValue = manager.calculateTotalInventoryValue();
        double avgPrice = manager.calculateAveragePrice();
        
        Product cheapest = manager.getCheapestProduct();
        Product mostExpensive = manager.getMostExpensiveProduct();
        Map.Entry<String, Double> cheapestCat = manager.getCheapestCategory();
        Map.Entry<String, Double> mostExpensiveCat = manager.getMostExpensiveCategory();

        System.out.println(ConsoleUI.YELLOW + "\nKopējā statistika:" + ConsoleUI.RESET);
        System.out.printf("Inventāra kopējā vērtība: " + ConsoleUI.GREEN + "%.2f EUR%n" + ConsoleUI.RESET, totalValue);
        System.out.printf("Inventāra vidējā vērtība: " + ConsoleUI.GREEN + "%.2f EUR%n" + ConsoleUI.RESET, avgPrice);
        
        if (cheapest != null && mostExpensive != null) {
            System.out.println(ConsoleUI.YELLOW + "\nProduktu statistika:" + ConsoleUI.RESET);
            System.out.printf("Vislētākais produkts: %s (" + ConsoleUI.GREEN + "%.2f EUR" + ConsoleUI.RESET + ")%n", 
                cheapest.getName(), cheapest.getPrice());
            System.out.printf("Visdārgākais produkts: %s (" + ConsoleUI.GREEN + "%.2f EUR" + ConsoleUI.RESET + ")%n", 
                mostExpensive.getName(), mostExpensive.getPrice());
        }
        
        if (cheapestCat != null && mostExpensiveCat != null) {
            System.out.println(ConsoleUI.YELLOW + "\nKategoriju statistika:" + ConsoleUI.RESET);
            System.out.printf("Vislētākā kategorija: %s (" + ConsoleUI.GREEN + "%.2f EUR" + ConsoleUI.RESET + ")%n", 
                cheapestCat.getKey(), cheapestCat.getValue());
            System.out.printf("Visdārgākā kategorija: %s (" + ConsoleUI.GREEN + "%.2f EUR" + ConsoleUI.RESET + ")%n", 
                mostExpensiveCat.getKey(), mostExpensiveCat.getValue());
        }

        System.out.println("\nNospiediet Enter, lai atgrieztos galvenajā izvēlnē...");
        scanner.nextLine();
    }

    private void exit() {
        fileManager.saveData();
        ConsoleUI.clearScreen();
        ConsoleUI.printSuccess("Paldies, ka izmantojāt sistēmu!");
    }
}