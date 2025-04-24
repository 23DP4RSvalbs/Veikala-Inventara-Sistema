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
            System.out.println("4. Parādīt visus produktus");
            System.out.println("5. Meklēt produktus");
            System.out.println("6. Filtrēt produktus pēc kategorijas");
            System.out.println("7. Kārtot produktus pēc cenas");
            System.out.println("8. Aprēķināt kopējo inventāra vērtību");
            System.out.println("9. Aprēķināt vidējo cenu");
            System.out.println("10. Pievienot kategoriju");
            System.out.println("11. Parādīt visas kategorijas");
            System.out.println("12. Saglabāt datus");
            System.out.println("13. Iziet");

            System.out.print("Ievadiet izvēli: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                addProduct();
            } else if (choice.equals("2")) {
                editProduct();
            } else if (choice.equals("3")) {
                deleteProduct();
            } else if (choice.equals("4")) {
                manager.showAllProducts();
            } else if (choice.equals("5")) {
                searchProducts();
            } else if (choice.equals("6")) {
                filterProductsByCategory();
            } else if (choice.equals("7")) {
                manager.sortProductsByPrice();
            } else if (choice.equals("8")) {
                System.out.println("Kopējā inventāra vērtība: " + manager.calculateTotalInventoryValue());
            } else if (choice.equals("9")) {
                System.out.println("Vidējā cena: " + manager.calculateAveragePrice());
            } else if (choice.equals("10")) {
                addCategory();
            } else if (choice.equals("11")) {
                manager.showAllCategories();
            } else if (choice.equals("12")) {
                saveData();
            } else if (choice.equals("13")) {
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
        System.out.print("Ievadiet produkta ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        manager.deleteProduct(id);
    }

    // Produkta meklēšana
    private void searchProducts() {
        System.out.print("Ievadiet preces nosaukumu: ");
        String keyword = scanner.nextLine();
        manager.searchProducts(keyword);
    }

    // Produktu filtrēšana pēc kategorijas
    private void filterProductsByCategory() {
        System.out.print("Ievadiet kategoriju: ");
        String category = scanner.nextLine();
        manager.filterProductsByCategory(category);
    }

    // Kategorijas pievienošana
    private void addCategory() {
        System.out.print("Ievadiet kategorijas nosaukumu: ");
        String name = scanner.nextLine();
        manager.addCategory(name);
    }

    // Datu failu saglabāšana
    private void saveData() {
        FileManager.saveProducts(manager.getProducts());
        FileManager.saveCategories(manager.getCategories());
        System.out.println("Dati saglabāti!");
    }
}