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
        displayWelcome();
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

    private void displayWelcome() {
        String title = messages.getString("app.title");
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader(title);
    }

    private void displayMainMenu() {
        ConsoleUI.printMenu("GALVENĀ IZVĒLNE",
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
        System.out.print(ConsoleUI.YELLOW + messages.getString("prompt.choice") + ConsoleUI.RESET);
    }

    private void filterInventory() {
        fileManager.checkAndClearIfFilesDeleted();
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printMenu("FILTRĒT INVENTĀRU",
                "1. Parādīt visus produktus",
                "2. Parādīt visas kategorijas",
                "3. Filtrēt produktus pēc cenas",
                "4. Filtrēt produktus pēc kategorijas",
                "5. Filtrēt produktus pēc daudzuma",
                "0. Atcelt");
            
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
        while (true) {
            System.out.print(ConsoleUI.YELLOW + prompt + ConsoleUI.RESET);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("Atcelt")) return "Atcelt";
            if (allowEmpty && input.isEmpty()) return "";
            
            if (Helper.validateInput(input)) {
                return input;
            } else {
                ConsoleUI.printError("Ievadē atļauti tikai burti, cipari un _ simbols");
            }
        }
    }

    private String getValidatedNumericInput(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(ConsoleUI.YELLOW + prompt + ConsoleUI.RESET);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("Atcelt")) return "Atcelt";
            if (allowEmpty && input.isEmpty()) return "";
            
            if (Helper.validateNumericInput(input)) {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    ConsoleUI.printError("Vērtībai jābūt pozitīvai");
                    continue;
                }
                return input;
            } else {
                ConsoleUI.printError("Lūdzu ievadiet derīgu skaitli");
            }
        }
    }

    private void addProduct() {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader(messages.getString("product.add.header"));
        
        String name = getValidatedInput(messages.getString("product.name.prompt"), false);
        if (name.equalsIgnoreCase("Atcelt")) return;
        
        displayCategories();
        String category;
        while (true) {
            category = getValidatedInput(messages.getString("product.category.prompt"), false);
            if (category.equalsIgnoreCase("Atcelt")) return;
            
            if (!manager.categoryExists(category)) {
                ConsoleUI.printError(messages.getString("product.category.invalid"));
                continue;
            }
            break;
        }
        
        String priceStr = getValidatedNumericInput(messages.getString("product.price.prompt"), false);
        if (priceStr.equalsIgnoreCase("Atcelt")) return;
        double price = Double.parseDouble(priceStr);
        
        String quantityStr = getValidatedNumericInput(messages.getString("product.quantity.prompt"), false);
        if (quantityStr.equalsIgnoreCase("Atcelt")) return;
        int quantity = Integer.parseInt(quantityStr);
        
        try {
            manager.addProduct(name, category, price, quantity);
            ConsoleUI.printSuccess(messages.getString("product.add.success"));
        } catch (IllegalArgumentException e) {
            ConsoleUI.printError(e.getMessage());
        }
    }

    private void addCategory() {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader(messages.getString("categories.header"));
        
        String name = getValidatedInput(messages.getString("categories.name.prompt"), false);
        if (name.equalsIgnoreCase("Atcelt")) return;
        
        if (manager.categoryExists(name)) {
            ConsoleUI.printError(messages.getString("categories.exists"));
            return;
        }
        
        try {
            manager.addCategory(name);
            ConsoleUI.printSuccess(messages.getString("categories.add.success"));
        } catch (IllegalArgumentException e) {
            ConsoleUI.printError(e.getMessage());
        }
    }

    private void editProduct() {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader(messages.getString("menu.edit.product"));
        
        displayFilteredProducts(manager.getProducts(), "id", "asc");
        System.out.println();
        
        String idStr = getValidatedNumericInput(messages.getString("product.id.prompt"), false);
        if (idStr.equalsIgnoreCase("Atcelt")) return;
        
        int id = Integer.parseInt(idStr);
        Product product = manager.findProductById(id);
        if (product == null) {
            ConsoleUI.printError(messages.getString("product.not.found"));
            return;
        }
        
        System.out.println("\n" + messages.getString("product.current") + product);
        System.out.println(messages.getString("product.edit.instructions"));
        
        String name = getValidatedInput(messages.getString("product.name.prompt"), true);
        if (name.equalsIgnoreCase("Atcelt")) return;
        
        displayCategories();
        String category = getValidatedInput(messages.getString("product.category.prompt"), true);
        if (category.equalsIgnoreCase("Atcelt")) return;
        if (!category.isEmpty() && !manager.categoryExists(category)) {
            ConsoleUI.printError(messages.getString("product.category.invalid"));
            return;
        }
        
        String priceStr = getValidatedNumericInput(messages.getString("product.price.prompt"), true);
        if (priceStr.equalsIgnoreCase("Atcelt")) return;
        double price = priceStr.isEmpty() ? -1 : Double.parseDouble(priceStr);
        
        String quantityStr = getValidatedNumericInput(messages.getString("product.quantity.prompt"), true);
        if (quantityStr.equalsIgnoreCase("Atcelt")) return;
        int quantity = quantityStr.isEmpty() ? -1 : Integer.parseInt(quantityStr);
        
        try {
            manager.editProduct(id, name.isEmpty() ? null : name, 
                           category.isEmpty() ? null : category, 
                           price, quantity);
            ConsoleUI.printSuccess(messages.getString("product.edit.success"));
        } catch (IllegalArgumentException e) {
            ConsoleUI.printError(e.getMessage());
        }
    }

    private void deleteProduct() {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader(messages.getString("menu.delete.product"));
        
        displayFilteredProducts(manager.getProducts(), "id", "asc");
        System.out.println();
        
        String idStr = getValidatedNumericInput(messages.getString("product.delete.prompt"), false);
        if (idStr.equalsIgnoreCase("Atcelt")) return;
        
        try {
            int id = Integer.parseInt(idStr);
            Product product = manager.findProductById(id);
            if (product == null) {
                ConsoleUI.printError(messages.getString("product.not.found"));
                return;
            }
            
            manager.deleteProduct(id);
            ConsoleUI.printSuccess(messages.getString("product.delete.success"));
        } catch (IllegalArgumentException e) {
            ConsoleUI.printError(e.getMessage());
        }
    }

    private void dataExportImport() {
        ConsoleUI.clearScreen();
        ConsoleUI.printMenu(messages.getString("data.header"),
            "1. " + messages.getString("data.export"),
            "2. " + messages.getString("data.import"),
            "0. " + messages.getString("menu.back"));
        
        String choice = scanner.nextLine().trim();
        if (choice.equals("0")) return;
        
        try {
            if (choice.equals("1")) {
                fileManager.exportData("csv");
                ConsoleUI.printSuccess(messages.getString("export.success"));
            } else if (choice.equals("2")) {
                fileManager.importData("csv");
                ConsoleUI.printSuccess(messages.getString("import.success"));
            } else {
                ConsoleUI.printError(messages.getString("error.invalid.choice"));
            }
        } catch (Exception e) {
            ConsoleUI.printError(e.getMessage());
        }
    }

    private void searchInventory() {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printMenu(messages.getString("search.header"),
                "1. " + messages.getString("search.by.name"),
                "2. " + messages.getString("search.by.category"),
                "3. " + messages.getString("search.by.price.range"),
                "0. " + messages.getString("menu.back"));
            
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;
            
            try {
                switch (choice) {
                    case "1" -> searchByName();
                    case "2" -> searchByCategory();
                    case "3" -> searchByPriceRange();
                    default -> ConsoleUI.printError(messages.getString("error.invalid.choice"));
                }
            } catch (Exception e) {
                ConsoleUI.printError(e.getMessage());
            }
        }
    }

    private void searchByName() {
        String name = getValidatedInput(messages.getString("search.name.prompt"), false);
        if (name.equalsIgnoreCase("Atcelt")) return;
        
        List<Product> results = manager.searchByName(name);
        if (results.isEmpty()) {
            ConsoleUI.printWarning(messages.getString("search.no.results"));
        } else {
            ConsoleUI.printHeader(messages.getString("search.results"));
            displayFilteredProducts(results, "name", "asc");
        }
    }

    private void searchByCategory() {
        displayCategories();
        String category = getValidatedInput(messages.getString("search.category.prompt"), false);
        if (category.equalsIgnoreCase("Atcelt")) return;
        
        if (!manager.categoryExists(category)) {
            ConsoleUI.printError(messages.getString("product.category.invalid"));
            return;
        }
        
        List<Product> results = manager.searchByCategory(category);
        if (results.isEmpty()) {
            ConsoleUI.printWarning(messages.getString("search.no.results"));
        } else {
            ConsoleUI.printHeader(messages.getString("search.results"));
            displayFilteredProducts(results, "name", "asc");
        }
    }

    private void searchByPriceRange() {
        String minStr = getValidatedNumericInput(messages.getString("search.price.min"), false);
        if (minStr.equalsIgnoreCase("Atcelt")) return;
        double min = Double.parseDouble(minStr);
        
        String maxStr = getValidatedNumericInput(messages.getString("search.price.max"), false);
        if (maxStr.equalsIgnoreCase("Atcelt")) return;
        double max = Double.parseDouble(maxStr);
        
        List<Product> results = manager.searchByPriceRange(min, max);
        if (results.isEmpty()) {
            ConsoleUI.printWarning(messages.getString("search.no.results"));
        } else {
            ConsoleUI.printHeader(messages.getString("search.results"));
            displayFilteredProducts(results, "price", "asc");
        }
    }

    private void filterByPrice() {
        ConsoleUI.clearScreen();
        ConsoleUI.printMenu(messages.getString("products.sort.header"),
            "1. " + messages.getString("products.sort.price.asc"),
            "2. " + messages.getString("products.sort.price.desc"),
            "0. " + messages.getString("menu.back"));
        
        String choice = scanner.nextLine().trim();
        if (choice.equals("0")) return;
        
        if (choice.equals("1")) {
            displayFilteredProducts(manager.getProducts(), "price", "asc");
        } else if (choice.equals("2")) {
            displayFilteredProducts(manager.getProducts(), "price", "desc");
        } else {
            ConsoleUI.printError(messages.getString("error.invalid.choice"));
        }
    }

    private void filterByCategory() {
        ConsoleUI.clearScreen();
        displayCategories();
        System.out.println();
        
        String category = getValidatedInput(messages.getString("search.category.prompt"), false);
        if (category.equalsIgnoreCase("Atcelt")) return;
        
        if (!manager.categoryExists(category)) {
            ConsoleUI.printError(messages.getString("product.category.invalid"));
            return;
        }
        
        List<Product> results = manager.searchByCategory(category);
        if (results.isEmpty()) {
            ConsoleUI.printWarning(messages.getString("search.no.results"));
        } else {
            displayFilteredProducts(results, "name", "asc");
        }
    }

    private void filterByQuantity() {
        ConsoleUI.clearScreen();
        ConsoleUI.printMenu(messages.getString("products.sort.header"),
            "1. " + messages.getString("products.sort.quantity") + " ↑",
            "2. " + messages.getString("products.sort.quantity") + " ↓",
            "0. " + messages.getString("menu.back"));
        
        String choice = scanner.nextLine().trim();
        if (choice.equals("0")) return;
        
        if (choice.equals("1")) {
            displayFilteredProducts(manager.getProducts(), "quantity", "asc");
        } else if (choice.equals("2")) {
            displayFilteredProducts(manager.getProducts(), "quantity", "desc");
        } else {
            ConsoleUI.printError(messages.getString("error.invalid.choice"));
        }
    }

    private void configureBackupChanges() {
        ConsoleUI.clearScreen();
        ConsoleUI.printMenu(messages.getString("settings.backup.header"),
            "1. " + messages.getString("settings.backup.every"),
            "2. " + messages.getString("settings.backup.5"),
            "3. " + messages.getString("settings.backup.10"),
            "4. " + messages.getString("settings.backup.20"),
            "0. " + messages.getString("menu.back"));
        
        String choice = scanner.nextLine().trim();
        if (choice.equals("0")) return;
        
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
            ConsoleUI.printSuccess(String.format(messages.getString("backup.changes.set"), changes));
        } else {
            ConsoleUI.printError(messages.getString("error.invalid.choice"));
        }
    }

    private void showSettings() {
        while (true) {
            ConsoleUI.clearScreen();
            ConsoleUI.printMenu("IESTATĪJUMI",
                "1. Mainīt valodu",
                "2. Izveidot rezerves kopiju",
                "3. Nomainīt rezerves kopiju veidošanu",
                "4. Lietošanas Nosacījumi",
                "0. Atcelt");
            
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;
            
            try {
                switch (choice) {
                    case "1":
                        if (messages.getCurrentLanguage().equals("lv")) {
                            messages.setLanguage("en");
                            ConsoleUI.printSuccess("Language changed to English");
                        } else {
                            messages.setLanguage("lv");
                            ConsoleUI.printSuccess("Valoda nomainīta uz latviešu");
                        }
                        break;
                    case "2":
                        RecoveryManager.createBackup();
                        BackupConfig.getInstance().resetChanges();
                        ConsoleUI.printSuccess("Rezerves kopija veiksmīgi izveidota");
                        break;
                    case "3":
                        configureBackupChanges();
                        break;
                    case "4":
                        showUsageInstructions();
                        break;
                    default:
                        ConsoleUI.printError("Nederīga izvēle");
                }
                if (!choice.equals("3") && !choice.equals("4")) {
                    System.out.println("\nNospiediet Enter, lai turpinātu...");
                    scanner.nextLine();
                }
            } catch (Exception e) {
                ConsoleUI.printError(e.getMessage());
            }
        }
    }

    private void showUsageInstructions() {
        ConsoleUI.clearScreen();
        ConsoleUI.printHeader("Lietošanas Nosacījumi un Ierobežojumi");
        System.out.println(ConsoleUI.CYAN + "\n1. Produktu un Kategoriju Nosaukumi:" + ConsoleUI.RESET);
        System.out.println("   - Atļauti: burti (a-z, A-Z)");
        System.out.println("   - Atļauti: cipari (0-9)");
        System.out.println("   - Atļauts: pasvītrojuma simbols (_)");
        System.out.println("   - NAV atļauti: atstarpes un citi speciālie simboli");
        
        System.out.println(ConsoleUI.CYAN + "\n2. Skaitliskās Vērtības:" + ConsoleUI.RESET);
        System.out.println("   - Cenām jābūt pozitīvām");
        System.out.println("   - Daudzumam jābūt veselam, pozitīvam skaitlim");
        
        System.out.println(ConsoleUI.CYAN + "\n3. Ierobežojumi:" + ConsoleUI.RESET);
        System.out.println("   - Maksimālā cena: 1,000,000");
        System.out.println("   - Maksimālais daudzums: 1,000,000");
        System.out.println("   - Kategorijas/Produkta nosaukums: 1-50 simboli");
        
        System.out.println(ConsoleUI.CYAN + "\n4. Rezerves Kopijas:" + ConsoleUI.RESET);
        System.out.println("   - Tiek veidotas automātiski pēc noteikta izmaiņu skaita");
        System.out.println("   - Var izveidot manuāli caur iestatījumiem");
        System.out.println("   - Glabājas data/backup/ mapē");
        
        System.out.println("\nNospiediet Enter, lai turpinātu...");
        scanner.nextLine();
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