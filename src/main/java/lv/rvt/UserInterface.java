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
                }
                // Invalid choice - silently continue
            } catch (Exception e) {
                ConsoleUI.printError(e.getMessage());
            }
        }
    }

    private void displayMainMenu() {
        String[] options = {
            "1. " + ConsoleUI.BLUE + "Filtrēt inventāru" + ConsoleUI.RESET,
            "2. " + ConsoleUI.BLUE + messages.getString("menu.add.product") + ConsoleUI.RESET,
            "3. " + ConsoleUI.BLUE + messages.getString("categories.add") + ConsoleUI.RESET,
            "4. " + ConsoleUI.BLUE + messages.getString("menu.edit.product") + ConsoleUI.RESET,
            "5. " + ConsoleUI.BLUE + messages.getString("menu.delete.product") + ConsoleUI.RESET,
            "6. " + ConsoleUI.BLUE + messages.getString("menu.data") + ConsoleUI.RESET,
            "7. " + ConsoleUI.BLUE + messages.getString("menu.settings") + ConsoleUI.RESET,
            "8. " + ConsoleUI.BLUE + "Meklēt inventāru" + ConsoleUI.RESET,
            "9. " + ConsoleUI.BLUE + "Aprēķināt inventāru" + ConsoleUI.RESET,
            "0. " + ConsoleUI.RED + messages.getString("menu.exit") + ConsoleUI.RESET
        };
        ConsoleUI.printMenu(messages.getString("app.title"), options);
    }

    private void filterInventory() {
        fileManager.checkAndClearIfFilesDeleted();
        String[] options = {
            "1. " + ConsoleUI.BLUE + "Parādīt visus produktus" + ConsoleUI.RESET,
            "2. " + ConsoleUI.BLUE + "Parādīt visas kategorijas" + ConsoleUI.RESET,
            "3. " + ConsoleUI.BLUE + "Filtrēt produktus pēc cenas" + ConsoleUI.RESET,
            "4. " + ConsoleUI.BLUE + "Filtrēt produktus pēc kategorijas" + ConsoleUI.RESET,
            "5. " + ConsoleUI.BLUE + "Filtrēt produktus pēc daudzuma" + ConsoleUI.RESET,
            "0. " + ConsoleUI.RED + "Atcelt" + ConsoleUI.RESET
        };
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen("FILTRĒT INVENTĀRU", options);
            ConsoleUI.printMenu(currentScreen, options);
            
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) {
                setCurrentScreen(""); // Reset screen before returning
                break;
            }

            if (choice.equals("1")) {
                displayFilteredProducts(manager.getProducts(), "id", "asc");
            } else if (choice.equals("2")) {
                displayCategories();
                System.out.print("\n" + messages.getString("prompt.continue"));
                scanner.nextLine();
            } else if (choice.equals("3")) {
                filterByPrice();
            } else if (choice.equals("4")) {
                filterByCategory();
            } else if (choice.equals("5")) {
                filterByQuantity();
            }
        }
    }

    private void displayFilteredProducts(List<Product> products, String sortBy, String order) {
        if (products.isEmpty()) {
            ConsoleUI.printError(messages.getString("products.none"));
            return;
        }

        List<Product> sorted = manager.sortProducts(sortBy, order);
        ConsoleUI.printTableHeader(
            messages.getString("product.id.label"),
            messages.getString("product.name.label"),
            messages.getString("product.category.label"),
            messages.getString("product.price.label"),
            messages.getString("product.quantity.label")
        );
        
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
        
        System.out.print("\n" + ConsoleUI.BLUE + messages.getString("prompt.continue") + ConsoleUI.RESET);
        scanner.nextLine();
    }

    private void displayCategories() {
        ConsoleUI.printTableHeader("Kategorija");
        for (Category category : manager.getCategories()) {
            ConsoleUI.printTableRow(category.getName());
        }
        ConsoleUI.printTableFooter(1);
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
            ConsoleUI.printHeader(messages.getString("product.add.header"));
            System.out.print("\n" + ConsoleUI.BLUE + messages.getString("product.name.prompt") + ConsoleUI.RESET);
            
            String name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("Atcelt")) return null;
            
            if (!name.matches("^[a-zA-Z_]+$")) {
                ConsoleUI.printError("Nederīgs produkta nosaukums (atļauti tikai burti un _ simbols)");
                scanner.nextLine();
                continue;
            }
            
            if (manager.getProducts().stream().anyMatch(p -> p.getName().equalsIgnoreCase(name))) {
                ConsoleUI.printError("Produkts ar šādu nosaukumu jau eksistē");
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
            
            System.out.print(ConsoleUI.BLUE + messages.getString("product.category.prompt") + ConsoleUI.RESET);
            String category = scanner.nextLine().trim();
            if (category.equalsIgnoreCase("Atcelt")) return null;
            
            if (!category.matches("^[a-zA-Z_]+$")) {
                ConsoleUI.printError(messages.getString("validation.category.invalid"));
                scanner.nextLine();
                continue;
            }
            
            if (!manager.categoryExists(category)) {
                ConsoleUI.printError(messages.getString("product.category.invalid"));
                scanner.nextLine();
                continue;
            }
            
            return category;
        }
    }

    private Double getProductPrice() {
        while (true) {
            ConsoleUI.clearScreen();
            displayCurrentScreen();
            System.out.print(ConsoleUI.BLUE + messages.getString("product.price.prompt") + ConsoleUI.RESET);
            
            String priceStr = scanner.nextLine().trim();
            if (priceStr.equalsIgnoreCase("Atcelt")) return null;
            
            if (!priceStr.matches("^\\d{1,8}(\\.\\d{0,2})?$") || 
                Double.parseDouble(priceStr) < 0 || 
                Double.parseDouble(priceStr) > 99999999.99) {
                ConsoleUI.printError(Helper.getStandardNumberFormatError());
                scanner.nextLine();
                continue;
            }
            
            return Double.parseDouble(priceStr);
        }
    }

    private Integer getProductQuantity() {
        while (true) {
            ConsoleUI.clearScreen();
            displayCurrentScreen();
            System.out.print(ConsoleUI.BLUE + messages.getString("product.quantity.prompt") + ConsoleUI.RESET);
            
            String quantityStr = scanner.nextLine().trim();
            if (quantityStr.equalsIgnoreCase("Atcelt")) return null;
            
            if (!quantityStr.matches("^\\d{1,8}$") || 
                Integer.parseInt(quantityStr) < 0 || 
                Integer.parseInt(quantityStr) > 99999999) {
                ConsoleUI.printError(Helper.getStandardNumberFormatError());
                scanner.nextLine();
                continue;
            }
            
            return Integer.parseInt(quantityStr);
        }
    }

    private void addProduct() {
        while (true) {
            // Set up the screen state
            setCurrentScreen(messages.getString("product.add.header"));
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader(messages.getString("product.add.header"));
            
            final String name = getProductName();
            if (name == null) {
                setCurrentScreen(""); // Reset screen before returning
                return;
            }
            
            ConsoleUI.clearScreen();
            displayCategories();
            System.out.print(ConsoleUI.BLUE + messages.getString("product.category.prompt") + ConsoleUI.RESET + "\n\n");
            ConsoleUI.printHeader(messages.getString("product.add.header"));
            final String category = getProductCategory(); // This will show categories as part of its flow
            if (category == null) {
                setCurrentScreen(""); // Reset screen before returning
                return;
            }
            
            ConsoleUI.clearScreen();
            System.out.print(ConsoleUI.BLUE + messages.getString("product.price.prompt") + ConsoleUI.RESET + "\n\n");
            ConsoleUI.printHeader(messages.getString("product.add.header"));
            final Double price = getProductPrice();
            if (price == null) {
                setCurrentScreen(""); // Reset screen before returning
                return;
            }
            
            ConsoleUI.clearScreen();
            System.out.print(ConsoleUI.BLUE + messages.getString("product.quantity.prompt") + ConsoleUI.RESET + "\n\n");
            ConsoleUI.printHeader(messages.getString("product.add.header"));
            final Integer quantity = getProductQuantity();
            if (quantity == null) return;
            
            try {
                manager.addProduct(name, category, price, quantity);
                ConsoleUI.printSuccess(messages.getString("product.add.success"));
                System.out.println("\n" + ConsoleUI.BLUE + messages.getString("prompt.continue") + ConsoleUI.RESET);
                scanner.nextLine();
                setCurrentScreen(""); // Reset screen before returning
                return;
            } catch (IllegalArgumentException e) {
                setCurrentScreen(""); // Reset screen before returning
                return;
            }
        }
    }

    private void addCategory() {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader(messages.getString("categories.add"));
            displayCategories();
            
            System.out.print("\n" + ConsoleUI.BLUE + messages.getString("categories.name.prompt") + ConsoleUI.RESET);
            String name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("Atcelt")) {
                return;
            }
            
            if (name.isEmpty()) {
                continue;
            }
            
            if (!name.matches("^[a-zA-Z_]+$")) {
                ConsoleUI.printError(messages.getString("validation.category.invalid"));
                scanner.nextLine();
                continue;
            }
            
            if (manager.categoryExists(name)) {
                ConsoleUI.printError(messages.getString("categories.exists"));
                scanner.nextLine();
                continue;
            }
            
            try {
                manager.addCategory(name);
                ConsoleUI.printSuccess(messages.getString("categories.add.success"));
                scanner.nextLine();
                return;
            } catch (IllegalArgumentException e) {
                ConsoleUI.printError(e.getMessage());
                scanner.nextLine();
                continue;
            }
        }
    }

    private void editProduct() {
        Product product = null;
        
        // Get product ID
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader(messages.getString("menu.edit.product"));
            displayFilteredProducts(manager.getProducts(), "id", "asc");
            System.out.print("\n" + ConsoleUI.BLUE + messages.getString("product.id.prompt") + ConsoleUI.RESET);
            
            String idStr = scanner.nextLine().trim();
            if (idStr.equalsIgnoreCase("Atcelt")) {
                return;
            }
            
            if (idStr.isEmpty()) {
                continue;
            }
            
            try {
                if (!idStr.matches("^\\d+$")) {
                    ConsoleUI.printError(Helper.getStandardNumberFormatError());
                    continue;
                }
                
                int id = Integer.parseInt(idStr);
                product = manager.findProductById(id);
                if (product == null) {
                    ConsoleUI.printError(messages.getString("product.not.found"));
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                ConsoleUI.printError(Helper.getStandardNumberFormatError());
                continue;
            }
        }

        // Get updates for each field
        String name = null;
        String category = null;
        double price = -1;
        int quantity = -1;
        
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader(messages.getString("menu.edit.product"));
            System.out.println("\n" + ConsoleUI.BLUE + messages.getString("product.edit.instructions") + ConsoleUI.RESET);
            System.out.println("\n" + ConsoleUI.BLUE + "Esošais produkts:" + ConsoleUI.RESET);
            System.out.println("  Nosaukums: " + product.getName());
            System.out.println("  Kategorija: " + product.getCategory());
            System.out.println("  Cena: " + String.format("%.2f", product.getPrice()));
            System.out.println("  Daudzums: " + product.getQuantity());
            
            // Get name
            System.out.print("\n" + ConsoleUI.BLUE + messages.getString("product.name.prompt") + ConsoleUI.RESET);
            String newName = scanner.nextLine().trim();
            if (newName.equalsIgnoreCase("Atcelt")) return;
            if (!newName.isEmpty()) {
                if (!newName.matches("^[a-zA-Z_]+$")) {
                    ConsoleUI.printError("Nederīgs produkta nosaukums (atļauti tikai burti un _ simbols)");
                    continue;
                }
                name = newName;
            }
            
            // Get category
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader(messages.getString("menu.edit.product"));
            displayCategories();
            System.out.print("\n" + ConsoleUI.BLUE + messages.getString("product.category.prompt") + ConsoleUI.RESET);
            String newCategory = scanner.nextLine().trim();
            if (newCategory.equalsIgnoreCase("Atcelt")) return;
            if (!newCategory.isEmpty()) {
                if (!newCategory.matches("^[a-zA-Z_]+$")) {
                    ConsoleUI.printError(messages.getString("validation.category.invalid"));
                    continue;
                }
                if (!manager.categoryExists(newCategory)) {
                    ConsoleUI.printError(messages.getString("product.category.invalid"));
                    continue;
                }
                category = newCategory;
            }
            
            // Get price
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader(messages.getString("menu.edit.product"));
            System.out.print("\n" + ConsoleUI.BLUE + messages.getString("product.price.prompt") + ConsoleUI.RESET);
            String priceStr = scanner.nextLine().trim();
            if (priceStr.equalsIgnoreCase("Atcelt")) return;
            if (!priceStr.isEmpty()) {
                try {
                    if (!priceStr.matches("^\\d{1,8}(\\.\\d{0,2})?$")) {
                        ConsoleUI.printError(Helper.getStandardNumberFormatError());
                        continue;
                    }
                    double newPrice = Double.parseDouble(priceStr);
                    if (newPrice < 0 || newPrice > 99999999.99) {
                        ConsoleUI.printError(Helper.getStandardNumberFormatError());
                        continue;
                    }
                    price = newPrice;
                } catch (NumberFormatException e) {
                    ConsoleUI.printError(Helper.getStandardNumberFormatError());
                    continue;
                }
            }
            
            // Get quantity
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader(messages.getString("menu.edit.product"));
            System.out.print("\n" + ConsoleUI.BLUE + messages.getString("product.quantity.prompt") + ConsoleUI.RESET);
            String quantityStr = scanner.nextLine().trim();
            if (quantityStr.equalsIgnoreCase("Atcelt")) return;
            if (!quantityStr.isEmpty()) {
                try {
                    if (!quantityStr.matches("^\\d{1,8}$")) {
                        ConsoleUI.printError(Helper.getStandardNumberFormatError());
                        continue;
                    }
                    int newQuantity = Integer.parseInt(quantityStr);
                    if (newQuantity < 0 || newQuantity > 99999999) {
                        ConsoleUI.printError(Helper.getStandardNumberFormatError());
                        continue;
                    }
                    quantity = newQuantity;
                } catch (NumberFormatException e) {
                    ConsoleUI.printError(Helper.getStandardNumberFormatError());
                    continue;
                }
            }

            try {
                manager.editProduct(product.getId(), 
                                  name != null ? name : null,
                                  category != null ? category : null,
                                  price >= 0 ? price : -1,
                                  quantity >= 0 ? quantity : -1);
                ConsoleUI.printSuccess(messages.getString("product.edit.success"));
                System.out.println("\n" + ConsoleUI.BLUE + messages.getString("prompt.continue") + ConsoleUI.RESET);
                scanner.nextLine();
                return;
            } catch (IllegalArgumentException e) {
                ConsoleUI.printError(e.getMessage());
                continue;
            }
        }
    }

    private void deleteProduct() {
        Product product = null;
        
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader(messages.getString("menu.delete.product"));
            displayFilteredProducts(manager.getProducts(), "id", "asc");
            
            System.out.print("\n" + ConsoleUI.BLUE + messages.getString("product.id.prompt") + ConsoleUI.RESET);
            String idStr = scanner.nextLine().trim();
            if (idStr.equalsIgnoreCase("Atcelt")) {
                return;
            }
            
            if (idStr.isEmpty()) {
                continue;
            }
            
            try {
                if (!idStr.matches("^\\d+$")) {
                    ConsoleUI.printError(Helper.getStandardNumberFormatError());
                    scanner.nextLine();
                    continue;
                }
                
                int id = Integer.parseInt(idStr);
                product = manager.findProductById(id);
                if (product == null) {
                    ConsoleUI.printError(messages.getString("product.not.found"));
                    scanner.nextLine();
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                ConsoleUI.printError(Helper.getStandardNumberFormatError());
                scanner.nextLine();
                continue;
            }
        }
        
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printHeader(messages.getString("menu.delete.product"));
            System.out.println("\n" + ConsoleUI.BLUE + "Dzēst produktu:" + ConsoleUI.RESET);
            System.out.println("  Nosaukums: " + product.getName());
            System.out.println("  Kategorija: " + product.getCategory());
            System.out.println("  Cena: " + String.format("%.2f", product.getPrice()));
            System.out.println("  Daudzums: " + product.getQuantity());
            
            System.out.print("\n" + ConsoleUI.BLUE + messages.getString("product.delete.confirm") + " (j/n): " + ConsoleUI.RESET);
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("n")) {
                return;
            }
            
            if (confirm.equals("j")) {
                try {
                    manager.deleteProduct(product.getId());
                    ConsoleUI.printSuccess(messages.getString("product.delete.success"));
                    scanner.nextLine();
                    return;
                } catch (IllegalArgumentException e) {
                    ConsoleUI.printError(e.getMessage());
                    scanner.nextLine();
                    continue;
                }
            }
            
            // Invalid input, continue loop
            if (!confirm.equals("j") && !confirm.equals("n")) {
                ConsoleUI.printError("Lūdzu ievadiet j vai n");
                scanner.nextLine();
                continue;
            }
        }
    }

    private void dataExportImport() {
        String[] options = {
            "1. " + ConsoleUI.BLUE + messages.getString("data.export") + ConsoleUI.RESET,
            "2. " + ConsoleUI.BLUE + messages.getString("data.import") + ConsoleUI.RESET,
            "0. " + ConsoleUI.RED + messages.getString("menu.back") + ConsoleUI.RESET
        };
        
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("data.header").toUpperCase(), options);
            System.out.println();
            
            System.out.print(ConsoleUI.BLUE + messages.getString("menu.choice.prompt") + ConsoleUI.RESET);
            String choice = scanner.nextLine().trim();
            
            if (choice.isEmpty() || !choice.matches("[0-2]")) {
                continue;
            }
            
            if (choice.equals("0")) {
                return;
            }
            
            try {
                if (choice.equals("1")) {
                    fileManager.exportData("csv");
                    ConsoleUI.printSuccess(messages.getString("export.success"));
                    System.out.println("\nNospiediet Enter, lai turpinātu...");
                    scanner.nextLine();
                    return;
                }
                
                if (choice.equals("2")) {
                    fileManager.importData("csv");
                    ConsoleUI.printSuccess(messages.getString("import.success"));
                    System.out.println("\nNospiediet Enter, lai turpinātu...");
                    scanner.nextLine();
                    return;
                }
            } catch (Exception e) {
                ConsoleUI.printError(e.getMessage());
                System.out.println("\nNospiediet Enter, lai turpinātu...");
                scanner.nextLine();
                continue;
            }
        }
    }

    private void searchInventory() {
        String[] options = {
            "1. " + ConsoleUI.BLUE + messages.getString("search.by.name") + ConsoleUI.RESET,
            "2. " + ConsoleUI.BLUE + messages.getString("search.by.category") + ConsoleUI.RESET,
            "3. " + ConsoleUI.BLUE + messages.getString("search.by.price.range") + ConsoleUI.RESET,
            "0. " + ConsoleUI.RED + messages.getString("menu.back") + ConsoleUI.RESET
        };

        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("search.header"), options);
            ConsoleUI.printMenu(currentScreen, options);
            
            String choice = scanner.nextLine().trim();
            
            if (choice.equals("0")) {
                return;
            }
            
            switch (choice) {
                case "1" -> searchByName();
                case "2" -> searchByCategory();
                case "3" -> searchByPriceRange();
                default -> {
                    continue;
                }
            }
        }
    }

    private void searchByName() {
        while (true) {
            ConsoleUI.clearScreen();
            System.out.print(ConsoleUI.BLUE + messages.getString("search.name.prompt") + ConsoleUI.RESET);
            String name = scanner.nextLine().trim();
            
            if (name.isEmpty() || name.equalsIgnoreCase("0")) {
                return;
            }
            
            List<Product> results = manager.searchByName(name);
            if (results.isEmpty()) {
                ConsoleUI.printError(messages.getString("search.no.results"));
                scanner.nextLine();
                continue;
            }
            
            displayFilteredProducts(results, "name", "asc");
            return;
        }
    }

    private void searchByCategory() {
        while (true) {
            ConsoleUI.clearScreen();
            displayCategories();
            System.out.print(ConsoleUI.BLUE + messages.getString("search.category.prompt") + ConsoleUI.RESET);
            String category = scanner.nextLine().trim();
            
            if (category.isEmpty() || category.equalsIgnoreCase("0")) {
                return;
            }
            
            if (!manager.categoryExists(category)) {
                ConsoleUI.printError(messages.getString("product.category.invalid"));
                scanner.nextLine();
                continue;
            }
            
            List<Product> results = manager.searchByCategory(category);
            if (results.isEmpty()) {
                ConsoleUI.printError(messages.getString("search.no.results"));
                scanner.nextLine();
                continue;
            }
            
            displayFilteredProducts(results, "name", "asc");
            return;
        }
    }

    private void searchByPriceRange() {
        while (true) {
            ConsoleUI.clearScreen();
            System.out.print(ConsoleUI.BLUE + messages.getString("search.price.min") + ": " + ConsoleUI.RESET);
            String minStr = scanner.nextLine().trim();
            
            if (minStr.isEmpty() || minStr.equalsIgnoreCase("0")) {
                return;
            }
            
            if (!minStr.matches("\\d{1,8}(\\.\\d{0,2})?")) {
                ConsoleUI.printError(Helper.getStandardNumberFormatError());
                scanner.nextLine();
                continue;
            }
            
            double min;
            try {
                min = Double.parseDouble(minStr);
                if (min < 0 || min > 99999999.99) {
                    ConsoleUI.printError(Helper.getStandardNumberFormatError());
                    scanner.nextLine();
                    continue;
                }
            } catch (NumberFormatException e) {
                continue;
            }

            System.out.print(ConsoleUI.BLUE + messages.getString("search.price.max") + ": " + ConsoleUI.RESET);
            String maxStr = scanner.nextLine().trim();
            
            if (maxStr.isEmpty() || maxStr.equalsIgnoreCase("0")) {
                return;
            }
            
            if (!maxStr.matches("\\d{1,8}(\\.\\d{0,2})?")) {
                ConsoleUI.printError(Helper.getStandardNumberFormatError());
                scanner.nextLine();
                continue;
            }
            
            double max;
            try {
                max = Double.parseDouble(maxStr);
                if (max < 0 || max > 99999999.99) {
                    ConsoleUI.printError(Helper.getStandardNumberFormatError());
                    scanner.nextLine();
                    continue;
                }
            } catch (NumberFormatException e) {
                continue;
            }
            
            if (max < min) {
                ConsoleUI.printError(Helper.getStandardNumberFormatError());
                scanner.nextLine();
                continue;
            }
            
            List<Product> results = manager.searchByPriceRange(min, max);
            if (results.isEmpty()) {
                ConsoleUI.printError(messages.getString("search.no.results"));
                scanner.nextLine();
                continue;
            }
            
            displayFilteredProducts(results, "price", "asc");
            return;
        }
    }

    private void filterByPrice() {
        String[] options = {
            "1. " + ConsoleUI.BLUE + messages.getString("products.sort.price.asc") + ConsoleUI.RESET,
            "2. " + ConsoleUI.BLUE + messages.getString("products.sort.price.desc") + ConsoleUI.RESET,
            "0. " + ConsoleUI.RED + messages.getString("menu.back") + ConsoleUI.RESET
        };

        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("products.sort.header"), options);
            ConsoleUI.printMenu(currentScreen, options);
            
            String choice = scanner.nextLine().trim();
            
            if (choice.equals("0")) {
                return;
            }
            
            if (choice.equals("1") || choice.equals("2")) {
                ConsoleUI.clearScreen();
                displayFilteredProducts(
                    manager.getProducts(), 
                    "price", 
                    choice.equals("1") ? "asc" : "desc"
                );
                return;
            }
        }
    }

    private void filterByCategory() {
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen(messages.getString("products.filter.category"));
            
            displayCategories();
            System.out.println();
            
            System.out.print(ConsoleUI.BLUE + messages.getString("search.category.prompt") + ": " + ConsoleUI.RESET);
            String category = scanner.nextLine().trim();
            
            if (category.equalsIgnoreCase("Atcelt")) {
                return;
            }
            
            // If empty input, just continue the loop
            if (category.isEmpty()) {
                continue;
            }
            
            if (!category.matches("^[a-zA-Z_]+$")) {
                ConsoleUI.printError(messages.getString("validation.category.invalid"));
                scanner.nextLine();
                continue;
            }
            
            if (!manager.categoryExists(category)) {
                ConsoleUI.printError(messages.getString("product.category.invalid"));
                scanner.nextLine();
                continue;
            }
            
            List<Product> results = manager.searchByCategory(category);
            if (results.isEmpty()) {
                ConsoleUI.printError(messages.getString("search.no.results"));
                scanner.nextLine();
                continue;
            }
            
            ConsoleUI.clearScreen();
            displayFilteredProducts(results, "name", "asc");
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
            
            if (choice.equals("0")) {
                return;
            }
            
            if (choice.equals("1") || choice.equals("2")) {
                ConsoleUI.clearScreen();
                displayFilteredProducts(
                    manager.getProducts(), 
                    "quantity", 
                    choice.equals("1") ? "asc" : "desc"
                );
                return;
            }
        }
    }

    private void configureBackupChanges() {
        String[] options = {
            "1. " + ConsoleUI.BLUE + "Katru reizi" + ConsoleUI.RESET,
            "2. " + ConsoleUI.BLUE + "Ik pēc 5 izmaiņām" + ConsoleUI.RESET,
            "3. " + ConsoleUI.BLUE + "Ik pēc 10 izmaiņām" + ConsoleUI.RESET,
            "4. " + ConsoleUI.BLUE + "Ik pēc 20 izmaiņām" + ConsoleUI.RESET,
            "0. " + ConsoleUI.RED + "Atcelt" + ConsoleUI.RESET
        };
        
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen("REZERVES KOPIJU IESTATĪJUMI", options);
            System.out.println();
            
            System.out.print(ConsoleUI.BLUE + messages.getString("menu.choice.prompt") + ConsoleUI.RESET);
            String choice = scanner.nextLine().trim();
            
            if (choice.isEmpty() || !choice.matches("[0-4]")) {
                continue;
            }
            
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
                BackupConfig config = BackupConfig.getInstance();
                config.setChangesBeforeBackup(changes);
                config.resetChanges();
                
                ConfigManager.getInstance().setProperty("backup.interval", String.valueOf(changes));
                
                String message = switch (changes) {
                    case 1 -> "Rezerves kopijas tiks veidotas pēc katras izmaiņas";
                    default -> String.format("Rezerves kopijas tiks veidotas pēc %d izmaiņām", changes);
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
            "1. " + ConsoleUI.BLUE + "Izveidot rezerves kopiju" + ConsoleUI.RESET,
            "2. " + ConsoleUI.BLUE + "Nomainīt rezerves kopiju veidošanu" + ConsoleUI.RESET,
            "3. " + ConsoleUI.BLUE + "Lietošanas Nosacījumi" + ConsoleUI.RESET,
            "0. " + ConsoleUI.RED + "Atcelt" + ConsoleUI.RESET
        };
        
        while (true) {
            ConsoleUI.clearScreen();
            setCurrentScreen("IESTATĪJUMI", options);
            System.out.println();
            
            System.out.print(ConsoleUI.BLUE + messages.getString("menu.choice.prompt") + ConsoleUI.RESET);
            String choice = scanner.nextLine().trim();
            
            if (choice.isEmpty() || !choice.matches("[0-3]")) {
                continue;
            }
            
            if (choice.equals("0")) {
                return;
            }
            
            try {
                switch (choice) {
                    case "1" -> {
                        RecoveryManager.createBackup();
                        BackupConfig.getInstance().resetChanges();
                        ConsoleUI.printSuccess("Rezerves kopija veiksmīgi izveidota");
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
                ConsoleUI.printError(e.getMessage());
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
        
        System.out.println(ConsoleUI.BLUE + "\nProduktu un Kategoriju Nosaukumi:" + ConsoleUI.RESET);
        System.out.println("   - Atļauti tikai burti (a-z, A-Z)");
        System.out.println("   - Atļauts pasvītrojuma simbols (_)");
        System.out.println("   - NAV atļauti: cipari, atstarpes un citi simboli");
        
        System.out.println(ConsoleUI.BLUE + "\nSkaitliskās Vērtības:" + ConsoleUI.RESET);
        System.out.println("   - Cenai jābūt pozitīvai (max: 99999999.99)");
        System.out.println("   - Daudzumam jābūt pozitīvam (max: 99999999)");
        
        System.out.println(ConsoleUI.BLUE + "\nValidācijas Nosacījumi:" + ConsoleUI.RESET);
        System.out.println("   - Nosaukumi: tikai burti un _ simbols");
        System.out.println("   - Kategorijas: tikai burti un _ simbols");
        System.out.println("   - Cena: līdz 8 cipariem, 2 decimālie");
        System.out.println("   - Daudzums: līdz 8 cipariem");
        
        System.out.println(ConsoleUI.BLUE + "\nRezerves Kopijas:" + ConsoleUI.RESET);
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

        System.out.println(ConsoleUI.BLUE + "\nKopējā statistika:" + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BLUE + "Inventāra kopējā vērtība: " + ConsoleUI.WHITE + 
                         String.format("%.2f EUR", totalValue) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BLUE + "Inventāra vidējā vērtība: " + ConsoleUI.WHITE + 
                         String.format("%.2f EUR", avgPrice) + ConsoleUI.RESET);

        if (cheapest != null && mostExpensive != null) {
            System.out.println(ConsoleUI.BLUE + "\nProduktu statistika:" + ConsoleUI.RESET);
            System.out.println(ConsoleUI.BLUE + "Vislētākais produkts: " + ConsoleUI.WHITE + 
                           String.format("%s (%.2f EUR)", cheapest.getName(), cheapest.getPrice()) + ConsoleUI.RESET);
            System.out.println(ConsoleUI.BLUE + "Visdārgākais produkts: " + ConsoleUI.WHITE + 
                           String.format("%s (%.2f EUR)", mostExpensive.getName(), mostExpensive.getPrice()) + ConsoleUI.RESET);
        }
        
        if (cheapestCat != null && mostExpensiveCat != null) {
            System.out.println(ConsoleUI.BLUE + "\nKategoriju statistika:" + ConsoleUI.RESET);
            System.out.println(ConsoleUI.BLUE + "Vislētākā kategorija: " + ConsoleUI.WHITE + 
                           String.format("%s (%.2f EUR)", cheapestCat.getKey(), cheapestCat.getValue()) + ConsoleUI.RESET);
            System.out.println(ConsoleUI.BLUE + "Visdārgākā kategorija: " + ConsoleUI.WHITE + 
                           String.format("%s (%.2f EUR)", mostExpensiveCat.getKey(), mostExpensiveCat.getValue()) + ConsoleUI.RESET);
        }

        System.out.println("\n" + ConsoleUI.BLUE + "Nospiediet Enter, lai atgrieztos galvenajā izvēlnē..." + ConsoleUI.RESET);
        scanner.nextLine();
    }

    private void exit() {
        fileManager.saveData();
        ConsoleUI.clearScreen();
        ConsoleUI.printSuccess("Paldies, ka izmantojāt sistēmu!");
    }
}