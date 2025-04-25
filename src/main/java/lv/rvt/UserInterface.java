package lv.rvt;

import java.util.Scanner;
import lv.rvt.tools.Helper;

public class UserInterface {
    private InventoryManager manager;
    private Scanner scanner;

    public UserInterface() {
        manager = new InventoryManager();
        scanner = new Scanner(System.in);
    }

    public void start() {
        FileManager.loadCategories(manager);
        FileManager.loadProducts(manager);
        showMainMenu();
    }

    private void showMainMenu() {
        System.out.println("  _    _      _          _          _____");
        System.out.println(" | |  | |    | |        | |        /     |");
        System.out.println(" | |  | | ___| |__   ___| |__     |  ***  | _ __   ___");
        System.out.println(" | |  | |/ _ \\ '_ \\ / __| '_ \\     \\  *** | | '_ \\ / _ \\");
        System.out.println(" | |__| |  __/ |_) | (__| | | |     ***  | || |_) |  __/");
        System.out.println("  \\____/ \\___|_.__/ \\___|_| |_|   |_____/ | .__/ \\___|");
        System.out.println("                                          | |");
        System.out.println("                                          |_|");
        System.out.println("     INVENTĀRA PĀRVALDĪBAS SISTĒMA");

        while (true) {
            System.out.println("\nIzvēlieties opciju:");
            System.out.println("1. Pievienot produktu");
            System.out.println("2. Rediģēt produktu");
            System.out.println("3. Dzēst produktu");
            System.out.println("4. Meklēt produktus");
            System.out.println("5. Filtrēt un kārtot inventāru");
            System.out.println("6. Aprēķināt kopējo inventāra vērtību");
            System.out.println("7. Aprēķināt vidējo cenu");
            System.out.println("8. Pievienot kategoriju");
            System.out.println("9. Saglabāt datus");
            System.out.println("10. Iziet");

            System.out.print("Ievadiet izvēli: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                addProduct();
            } else if (choice.equals("2")) {
                editProduct();
            } else if (choice.equals("3")) {
                deleteProduct();
            } else if (choice.equals("4")) {
                searchProducts();
            } else if (choice.equals("5")) {
                filterAndSortInventory();
            } else if (choice.equals("6")) {
                System.out.println("Kopējā inventāra vērtība: " + manager.calculateTotalInventoryValue());
            } else if (choice.equals("7")) {
                System.out.println("Vidējā cena: " + manager.calculateAveragePrice());
            } else if (choice.equals("8")) {
                addCategory();
            } else if (choice.equals("9")) {
                saveData();
            } else if (choice.equals("10")) {
                System.out.println("Iziet no programmas.");
                break;
            } else {
                System.out.println("Nederīga izvēle! Mēģiniet vēlreiz.");
            }
        }
    }

    // Produkta pievienošana
    private void addProduct() {
        String name;
        while (true) {
            System.out.print("Ievadiet produkta nosaukumu (vai 'cancel' lai atceltu): ");
            name = scanner.nextLine();
            if (name.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta pievienošana atcelta.");
                return;
            }
            if (Helper.validateProductName(name)) {
                break;
            }
            System.out.println("Nederīgs nosaukums! Drīkst izmantot burtus (a-z, A-Z) un '_', bet ne '_' vienu pašu. Nedrīkst lietot atstarpes, ciparus vai simbolus (piem., /, ., ;, ').");
        }

        String category;
        while (true) {
            System.out.print("Ievadiet kategoriju (vai 'list' lai redzētu visas kategorijas, 'cancel' lai atceltu): ");
            String categoryInput = scanner.nextLine();
            if (categoryInput.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta pievienošana atcelta.");
                return;
            } else if (categoryInput.equalsIgnoreCase("list")) {
                manager.showAllCategories();
                continue;
            }
            if (!Helper.validateCategory(categoryInput)) {
                System.out.println("Nederīga kategorija! Drīkst izmantot burtus (a-z, A-Z) un '_', bet ne '_' vienu pašu. Nedrīkst lietot atstarpes, ciparus vai simbolus (piem., /, ., ;, ').");
                continue;
            }
            boolean categoryExists = false;
            for (Category cat : manager.getCategories()) {
                if (cat.getName().equalsIgnoreCase(categoryInput)) {
                    categoryExists = true;
                    break;
                }
            }
            if (!categoryExists) {
                System.out.println("Kategorija nav atrasta. Lūdzu, izvēlieties esošu kategoriju.");
                continue;
            }
            category = categoryInput;
            break;
        }

        double price;
        while (true) {
            System.out.print("Ievadiet cenu (piem. 12.34, vai 'cancel' lai atceltu): ");
            String priceInput = scanner.nextLine();
            if (priceInput.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta pievienošana atcelta.");
                return;
            }
            if (Helper.validatePrice(priceInput)) {
                price = Double.parseDouble(priceInput);
                break;
            }
            System.out.println("Nederīga cena! Drīkst izmantot tikai skaitļus (piem., 12.34). Nedrīkst lietot atstarpes vai simbolus (piem., /, ;, ').");
        }

        int quantity;
        while (true) {
            System.out.print("Ievadiet daudzumu (piem. 5, vai 'cancel' lai atceltu): ");
            String quantityInput = scanner.nextLine();
            if (quantityInput.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta pievienošana atcelta.");
                return;
            }
            if (Helper.validateQuantity(quantityInput)) {
                quantity = Integer.parseInt(quantityInput);
                break;
            }
            System.out.println("Nederīgs daudzums! Drīkst izmantot tikai veselu skaitli (piem., 5). Nedrīkst lietot atstarpes vai simbolus (piem., /, ;, ').");
        }

        manager.addProduct(name, category, price, quantity);
    }

    // Produkta rediģēšana
    private void editProduct() {
        int id;
        while (true) {
            System.out.print("Ievadiet produkta ID (vai 'list' lai redzētu visus produktus, 'cancel' lai atceltu): ");
            String idInput = scanner.nextLine();
            if (idInput.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta rediģēšana atcelta.");
                return;
            } else if (idInput.equalsIgnoreCase("list")) {
                if (manager.getProducts().isEmpty()) {
                    System.out.println("Nav produktu!");
                } else {
                    for (Product product : manager.getProducts()) {
                        System.out.println("ID: " + product.getId() + ", Nosaukums: " + product.getName() + ", Kategorija: " + product.getCategory());
                    }
                }
                continue;
            }
            try {
                id = Integer.parseInt(idInput);
                boolean idExists = false;
                for (Product product : manager.getProducts()) {
                    if (product.getId() == id) {
                        idExists = true;
                        break;
                    }
                }
                if (!idExists) {
                    System.out.println("Produkts ar šādu ID nav atrasts. Lūdzu, ievadiet derīgu ID.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Nederīgs ID! Drīkst izmantot tikai veselu skaitli (piem., 123).");
            }
        }

        String name;
        while (true) {
            System.out.print("Ievadiet jauno nosaukumu (atstājiet tukšu, lai nemainītu, 'cancel' lai atceltu): ");
            name = scanner.nextLine();
            if (name.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta rediģēšana atcelta.");
                return;
            }
            if (name.isEmpty() || Helper.validateProductName(name)) {
                break;
            }
            System.out.println("Nederīgs nosaukums! Drīkst izmantot burtus (a-z, A-Z) un '_', bet ne '_' vienu pašu. Nedrīkst lietot atstarpes, ciparus vai simbolus (piem., /, ., ;, ').");
        }

        String category;
        while (true) {
            System.out.print("Ievadiet jauno kategoriju (atstājiet tukšu, lai nemainītu, 'list' lai redzētu visas kategorijas, 'cancel' lai atceltu): ");
            String categoryInput = scanner.nextLine();
            if (categoryInput.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta rediģēšana atcelta.");
                return;
            } else if (categoryInput.equalsIgnoreCase("list")) {
                manager.showAllCategories();
                continue;
            } else if (!categoryInput.isEmpty()) {
                if (!Helper.validateCategory(categoryInput)) {
                    System.out.println("Nederīga kategorija! Drīkst izmantot burtus (a-z, A-Z) un '_', bet ne '_' vienu pašu. Nedrīkst lietot atstarpes, ciparus vai simbolus (piem., /, ., ;, ').");
                    continue;
                }
                boolean categoryExists = false;
                for (Category cat : manager.getCategories()) {
                    if (cat.getName().equalsIgnoreCase(categoryInput)) {
                        categoryExists = true;
                        break;
                    }
                }
                if (!categoryExists) {
                    System.out.println("Kategorija nav atrasta. Lūdzu, izvēlieties esošu kategoriju.");
                    continue;
                }
            }
            category = categoryInput;
            break;
        }

        double price = -1;
        while (true) {
            System.out.print("Ievadiet jauno cenu (piem. 12.34, atstājiet tukšu, lai nemainītu, 'cancel' lai atceltu): ");
            String priceInput = scanner.nextLine();
            if (priceInput.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta rediģēšana atcelta.");
                return;
            }
            if (priceInput.isEmpty()) {
                break;
            }
            if (Helper.validatePrice(priceInput)) {
                price = Double.parseDouble(priceInput);
                break;
            }
            System.out.println("Nederīga cena! Drīkst izmantot tikai skaitļus (piem., 12.34). Nedrīkst lietot atstarpes vai simbolus (piem., /, ;, ').");
        }

        int quantity = -1;
        while (true) {
            System.out.print("Ievadiet jauno daudzumu (piem. 5, atstājiet tukšu, lai nemainītu, 'cancel' lai atceltu): ");
            String quantityInput = scanner.nextLine();
            if (quantityInput.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta rediģēšana atcelta.");
                return;
            }
            if (quantityInput.isEmpty()) {
                break;
            }
            if (Helper.validateQuantity(quantityInput)) {
                quantity = Integer.parseInt(quantityInput);
                break;
            }
            System.out.println("Nederīgs daudzums! Drīkst izmantot tikai veselu skaitli (piem., 5). Nedrīkst lietot atstarpes vai simbolus (piem., /, ;, ').");
        }

        manager.editProduct(id, name, category, price, quantity);
    }

    // Produkta dzēšana
    private void deleteProduct() {
        int id;
        while (true) {
            System.out.print("Ievadiet produkta ID (vai 'list' lai redzētu visus produktus, 'cancel' lai atceltu): ");
            String idInput = scanner.nextLine();
            if (idInput.equalsIgnoreCase("cancel")) {
                System.out.println("Produkta dzēšana atcelta.");
                return;
            } else if (idInput.equalsIgnoreCase("list")) {
                if (manager.getProducts().isEmpty()) {
                    System.out.println("Nav produktu!");
                } else {
                    for (Product product : manager.getProducts()) {
                        System.out.println("ID: " + product.getId() + ", Nosaukums: " + product.getName() + ", Kategorija: " + product.getCategory());
                    }
                }
                continue;
            }
            try {
                id = Integer.parseInt(idInput);
                boolean idExists = false;
                for (Product product : manager.getProducts()) {
                    if (product.getId() == id) {
                        idExists = true;
                        break;
                    }
                }
                if (!idExists) {
                    System.out.println("Produkts ar šādu ID nav atrasts. Lūdzu, ievadiet derīgu ID.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Nederīgs ID! Drīkst izmantot tikai veselu skaitli (piem., 123).");
            }
        }
        manager.deleteProduct(id);
    }

    // Produkta meklēšana
    private void searchProducts() {
        while (true) {
            System.out.println("\nIzvēlieties meklēšanas kritēriju:");
            System.out.println("1. Meklēt pēc ID");
            System.out.println("2. Meklēt pēc nosaukuma");
            System.out.println("3. Meklēt pēc kategorijas");
            System.out.println("4. Meklēt pēc cenas");
            System.out.println("5. Meklēt pēc daudzuma");
            System.out.println("6. Atcelt");
            System.out.print("Ievadiet izvēli (1-6): ");
            String choice = scanner.nextLine();

            if (choice.equals("6") || choice.equalsIgnoreCase("cancel")) {
                System.out.println("Meklēšana atcelta.");
                return;
            }

            String criterion;
            if (choice.equals("1")) {
                criterion = "id";
            } else if (choice.equals("2")) {
                criterion = "name";
            } else if (choice.equals("3")) {
                criterion = "category";
            } else if (choice.equals("4")) {
                criterion = "price";
            } else if (choice.equals("5")) {
                criterion = "quantity";
            } else {
                System.out.println("Nederīga izvēle! Lūdzu, izvēlieties skaitli no 1 līdz 6.");
                continue;
            }

            String keyword;
            while (true) {
                if (criterion.equals("id")) {
                    System.out.print("Ievadiet ID (vismaz 1 cipars, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    keyword = scanner.nextLine();
                    if (keyword.equalsIgnoreCase("cancel")) {
                        System.out.println("Meklēšana atcelta.");
                        return;
                    } else if (keyword.equalsIgnoreCase("back")) {
                        break;
                    }
                    if (Helper.validateSearchId(keyword)) {
                        manager.searchProducts(criterion, keyword);
                        break;
                    }
                    System.out.println("Nederīgs ID! Drīkst izmantot tikai ciparus (piem., 123). Nedrīkst lietot atstarpes vai simbolus (piem., /, ;, ').");
                } else if (criterion.equals("name")) {
                    System.out.print("Ievadiet nosaukumu (vismaz 2 burti, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    keyword = scanner.nextLine();
                    if (keyword.equalsIgnoreCase("cancel")) {
                        System.out.println("Meklēšana atcelta.");
                        return;
                    } else if (keyword.equalsIgnoreCase("back")) {
                        break;
                    }
                    if (Helper.validateSearchName(keyword)) {
                        manager.searchProducts(criterion, keyword);
                        break;
                    }
                    System.out.println("Nederīgs nosaukums! Drīkst izmantot burtus (a-z, A-Z) un '_', bet ne '_' vienu pašu un vismaz 2 simbolus. Nedrīkst lietot atstarpes, ciparus vai simbolus (piem., /, ., ;, ').");
                } else if (criterion.equals("category")) {
                    System.out.print("Ievadiet kategoriju (vismaz 2 burti, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    keyword = scanner.nextLine();
                    if (keyword.equalsIgnoreCase("cancel")) {
                        System.out.println("Meklēšana atcelta.");
                        return;
                    } else if (keyword.equalsIgnoreCase("back")) {
                        break;
                    }
                    if (Helper.validateSearchCategory(keyword)) {
                        manager.searchProducts(criterion, keyword);
                        break;
                    }
                    System.out.println("Nederīga kategorija! Drīkst izmantot burtus (a-z, A-Z) un '_', bet ne '_' vienu pašu un vismaz 2 simbolus. Nedrīkst lietot atstarpes, ciparus vai simbolus (piem., /, ., ;, ').");
                } else if (criterion.equals("price")) {
                    System.out.print("Ievadiet cenu (vismaz 1 cipars, piem. 12.34, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    keyword = scanner.nextLine();
                    if (keyword.equalsIgnoreCase("cancel")) {
                        System.out.println("Meklēšana atcelta.");
                        return;
                    } else if (keyword.equalsIgnoreCase("back")) {
                        break;
                    }
                    if (Helper.validateSearchPrice(keyword)) {
                        manager.searchProducts(criterion, keyword);
                        break;
                    }
                    System.out.println("Nederīga cena! Drīkst izmantot tikai skaitļus (piem., 12.34). Nedrīkst lietot atstarpes vai simbolus (piem., /, ;, ').");
                } else if (criterion.equals("quantity")) {
                    System.out.print("Ievadiet daudzumu (vismaz 1 cipars, piem. 5, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    keyword = scanner.nextLine();
                    if (keyword.equalsIgnoreCase("cancel")) {
                        System.out.println("Meklēšana atcelta.");
                        return;
                    } else if (keyword.equalsIgnoreCase("back")) {
                        break;
                    }
                    if (Helper.validateSearchQuantity(keyword)) {
                        manager.searchProducts(criterion, keyword);
                        break;
                    }
                    System.out.println("Nederīgs daudzums! Drīkst izmantot tikai veselu skaitli (piem., 5). Nedrīkst lietot atstarpes vai simbolus (piem., /, ;, ').");
                }
            }
        }
    }

    // Filtrēt un kārtot inventāru
    private void filterAndSortInventory() {
        while (true) {
            System.out.println("\nFiltrēšanas un kārtošanas opcijas:");
            System.out.println("1. Parādīt visus produktus");
            System.out.println("2. Filtrēt pēc kategorijas");
            System.out.println("3. Filtrēt un kārtot pēc cenas");
            System.out.println("4. Filtrēt un kārtot pēc daudzuma");
            System.out.println("5. Parādīt visas kategorijas");
            System.out.println("6. Atcelt");
            System.out.print("Ievadiet izvēli (1-6): ");
            String choice = scanner.nextLine();

            if (choice.equals("6") || choice.equalsIgnoreCase("cancel")) {
                System.out.println("Filtrēšana un kārtošana atcelta.");
                return;
            }

            if (choice.equals("1")) {
                manager.showAllProducts();
            } else if (choice.equals("2")) {
                String category;
                while (true) {
                    System.out.print("Ievadiet kategoriju (vai 'list' lai redzētu visas kategorijas, 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    category = scanner.nextLine();
                    if (category.equalsIgnoreCase("cancel")) {
                        System.out.println("Filtrēšana atcelta.");
                        return;
                    } else if (category.equalsIgnoreCase("back")) {
                        break;
                    } else if (category.equalsIgnoreCase("list")) {
                        manager.showAllCategories();
                        continue;
                    }
                    if (Helper.validateCategory(category)) {
                        boolean categoryExists = false;
                        for (Category cat : manager.getCategories()) {
                            if (cat.getName().equalsIgnoreCase(category)) {
                                categoryExists = true;
                                break;
                            }
                        }
                        if (!categoryExists) {
                            System.out.println("Kategorija nav atrasta. Lūdzu, izvēlieties esošu kategoriju.");
                            continue;
                        }
                        manager.filterProductsByCategory(category);
                        break;
                    }
                    System.out.println("Nederīga kategorija! Drīkst izmantot burtus (a-z, A-Z) un '_', bet ne '_' vienu pašu. Nedrīkst lietot atstarpes, ciparus vai simbolus (piem., /, ., ;, ').");
                }
            } else if (choice.equals("3")) {
                double minPrice, maxPrice;
                while (true) {
                    System.out.print("Ievadiet minimālo cenu (piem. 0.0, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    String minInput = scanner.nextLine();
                    if (minInput.equalsIgnoreCase("cancel")) {
                        System.out.println("Filtrēšana atcelta.");
                        return;
                    } else if (minInput.equalsIgnoreCase("back")) {
                        break;
                    }
                    System.out.print("Ievadiet maksimālo cenu (piem. 100.0, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    String maxInput = scanner.nextLine();
                    if (maxInput.equalsIgnoreCase("cancel")) {
                        System.out.println("Filtrēšana atcelta.");
                        return;
                    } else if (maxInput.equalsIgnoreCase("back")) {
                        break;
                    }
                    if (Helper.validatePriceRange(minInput, maxInput)) {
                        minPrice = Double.parseDouble(minInput);
                        maxPrice = Double.parseDouble(maxInput);
                        boolean ascending;
                        while (true) {
                            System.out.print("Kārtot augošā secībā? (jā/nē, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                            String sortChoice = scanner.nextLine();
                            if (sortChoice.equalsIgnoreCase("cancel")) {
                                System.out.println("Filtrēšana atcelta.");
                                return;
                            } else if (sortChoice.equalsIgnoreCase("back")) {
                                break;
                            }
                            if (sortChoice.equalsIgnoreCase("jā") || sortChoice.equalsIgnoreCase("ja")) {
                                ascending = true;
                                manager.filterAndSortByPrice(minPrice, maxPrice, ascending);
                                break;
                            } else if (sortChoice.equalsIgnoreCase("nē") || sortChoice.equalsIgnoreCase("ne")) {
                                ascending = false;
                                manager.filterAndSortByPrice(minPrice, maxPrice, ascending);
                                break;
                            }
                            System.out.println("Nederīga izvēle! Lūdzu, ievadiet 'jā' vai 'nē'.");
                        }
                        break;
                    }
                    System.out.println("Nederīgs cenu diapazons! Drīkst izmantot tikai skaitļus (piem., 12.34), un maksimālā cena nedrīkst būt mazāka par minimālo.");
                }
            } else if (choice.equals("4")) {
                int minQuantity, maxQuantity;
                while (true) {
                    System.out.print("Ievadiet minimālo daudzumu (piem. 0, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    String minInput = scanner.nextLine();
                    if (minInput.equalsIgnoreCase("cancel")) {
                        System.out.println("Filtrēšana atcelta.");
                        return;
                    } else if (minInput.equalsIgnoreCase("back")) {
                        break;
                    }
                    System.out.print("Ievadiet maksimālo daudzumu (piem. 100, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                    String maxInput = scanner.nextLine();
                    if (maxInput.equalsIgnoreCase("cancel")) {
                        System.out.println("Filtrēšana atcelta.");
                        return;
                    } else if (maxInput.equalsIgnoreCase("back")) {
                        break;
                    }
                    if (Helper.validateQuantityRange(minInput, maxInput)) {
                        minQuantity = Integer.parseInt(minInput);
                        maxQuantity = Integer.parseInt(maxInput);
                        boolean ascending;
                        while (true) {
                            System.out.print("Kārtot augošā secībā? (jā/nē, vai 'back' lai atgrieztos, 'cancel' lai atceltu): ");
                            String sortChoice = scanner.nextLine();
                            if (sortChoice.equalsIgnoreCase("cancel")) {
                                System.out.println("Filtrēšana atcelta.");
                                return;
                            } else if (sortChoice.equalsIgnoreCase("back")) {
                                break;
                            }
                            if (sortChoice.equalsIgnoreCase("jā") || sortChoice.equalsIgnoreCase("ja")) {
                                ascending = true;
                                manager.filterAndSortByQuantity(minQuantity, maxQuantity, ascending);
                                break;
                            } else if (sortChoice.equalsIgnoreCase("nē") || sortChoice.equalsIgnoreCase("ne")) {
                                ascending = false;
                                manager.filterAndSortByQuantity(minQuantity, maxQuantity, ascending);
                                break;
                            }
                            System.out.println("Nederīga izvēle! Lūdzu, ievadiet 'jā' vai 'nē'.");
                        }
                        break;
                    }
                    System.out.println("Nederīgs daudzuma diapazons! Drīkst izmantot tikai veselus skaitļus (piem., 5), un maksimālais daudzums nedrīkst būt mazāks par minimālo.");
                }
            } else if (choice.equals("5")) {
                manager.showAllCategories();
            } else {
                System.out.println("Nederīga izvēle! Lūdzu, izvēlieties skaitli no 1 līdz 6.");
            }
        }
    }

    // Kategorijas pievienošana
    private void addCategory() {
        String name;
        while (true) {
            System.out.print("Ievadiet kategorijas nosaukumu (vai 'cancel' lai atceltu): ");
            name = scanner.nextLine();
            if (name.equalsIgnoreCase("cancel")) {
                System.out.println("Kategorijas pievienošana atcelta.");
                return;
            }
            if (Helper.validateCategory(name)) {
                manager.addCategory(name);
                break;
            }
            System.out.println("Nederīgs kategorijas nosaukums! Drīkst izmantot burtus (a-z, A-Z) un '_', bet ne '_' vienu pašu. Nedrīkst lietot atstarpes, ciparus vai simbolus (piem., /, ., ;, ').");
        }
    }

    // Datu failu saglabāšana
    private void saveData() {
        FileManager.saveProducts(manager.getProducts());
        FileManager.saveCategories(manager.getCategories());
        System.out.println("Dati saglabāti!");
    }
}