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
        showMainMenu();
    }

    private void showMainMenu() {
        while (true) {


            System.out.println("Izvēlieties opciju:");
            System.out.println("1. P produktu");
            System.out.println("2. P visus produktus");
            System.out.println("3. I");






            System.out.print("Ievadiet i ");
            String choice = scanner.nextLine();



            if (choice.equals("1")) {

                addProduct();
            } else if (choice.equals("2")) {

                manager.showAllProducts();
            } else if (choice.equals("3")) {


                System.out.println("Iziet no programmas");
                break;
            } else {
                System.out.println("Nederīga izvē meginiet");
            }
        }
    }

    private void addProduct() {


        System.out.print("Ievadiet nosaukumu: ");
        String name = scanner.nextLine();

        System.out.print("kategoriju: ");
        String category = scanner.nextLine();
        System.out.print("cenu: ");

        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("daudzumu: ");
        int quantity = Integer.parseInt(scanner.nextLine());
        manager.addProduct(name, category, price, quantity);
    }
}