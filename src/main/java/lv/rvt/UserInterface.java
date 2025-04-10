package lv.rvt;

import java.util.Scanner;

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
            System.out.println("12. Iziet");

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
                System.out.println("Iziet no programmas.");
                break;
            } else {
                System.out.println("Nederīga izvēle! Mēģiniet vēlreiz.");
            }
        }
    }

    private void addProduct() {
        System.out.print("Ievadiet produkta nosaukumu: ");
        String name = scanner.nextLine();

        System.out.print("Ievadiet kategoriju: ");
        String category = scanner.nextLine();

        System.out.print("Ievadiet cenu: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("Ievadiet daudzumu: ");
        int quantity = Integer.parseInt(scanner.nextLine());
        manager.addProduct(name, category, price, quantity);
    }

    private void editProduct() {
        System.out.print("Ievadiet produkta ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Ievadiet jauno nosaukumu (vai tukšu): ");
        String name = scanner.nextLine();

        System.out.print("Ievadiet jauno kategoriju (vai tukšu): ");
        String category = scanner.nextLine();

        System.out.print("Ievadiet jauno cenu (vai -1): ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("Ievadiet jauno daudzumu (vai -1): ");
        int quantity = Integer.parseInt(scanner.nextLine());
        
        manager.editProduct(id, name, category, price, quantity);
    }

    private void deleteProduct() {
        System.out.print("Ievadiet produkta ID: ");

        int id = Integer.parseInt(scanner.nextLine());
        manager.deleteProduct(id);
    }

    private void searchProducts() {
        System.out.print("Ievadiet meklēšanas atslēgvārdu: ");

        String keyword = scanner.nextLine();
        manager.searchProducts(keyword);
    }

    private void filterProductsByCategory() {
        System.out.print("Ievadiet kategoriju: ");



        String category = scanner.nextLine();
        manager.filterProductsByCategory(category);
    }

    private void addCategory() {
        System.out.print("Ievadiet kategorijas nosaukumu: ");
        String name = scanner.nextLine();
        manager.addCategory(name);
    }
}