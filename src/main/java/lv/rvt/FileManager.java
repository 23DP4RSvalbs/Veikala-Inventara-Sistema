package lv.rvt;

import java.io.*;
import java.util.List;


public class FileManager {
    private static final String PRODUCTS_FILE = "data/products.csv";
    private static final String CATEGORIES_FILE = "data/categories.csv"; 

    //produkta saglabāšana
    public static void saveProducts(List<Product> products) {
        try (FileWriter writer = new FileWriter(PRODUCTS_FILE)) {
            writer.write("ID,ProductName,Price,Quantity\n");
            for (Product product : products) {
                writer.write(String.format("%d,%s,%.2f,%d\n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity()));
            }
            System.out.println("Produkti saglabāti!");
        } catch (IOException e) {
            System.out.println("Kļūda saglabājot produktus: " + e.getMessage());
        }
    }

    //produktu ielāde no faila
    public static void loadProducts(InventoryManager manager) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) 
                continue;
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    System.out.println("Nepareiza rinda CSV failā: " + line);
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    int quantity = Integer.parseInt(parts[3]);
                    manager.addProduct(name, "Default", price, quantity);
                } catch (NumberFormatException e) {
                    System.out.println("Kļūda ielādējot skaitliskos datus: " + e.getMessage());
                }
            }
            System.out.println("Produkti ielādēti!");
        } catch (IOException e) {
            System.out.println("Kļūda ielādējot produktus: " + e.getMessage());
        }
    }

    //kategoriju saglabāšana failā
    public static void saveCategories(List<Category> categories) {
        try (FileWriter writer = new FileWriter(CATEGORIES_FILE)) {
            for (Category category : categories) {
                writer.write(category.getName() + "\n");
            }
            System.out.println("Kategorijas saglabātas!");
        } catch (IOException e) {
            System.out.println("Kļūda saglabājot kategorijas: " + e.getMessage());
        }
    }

    //kategoriju ielāde no faila
    public static void loadCategories(InventoryManager manager) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CATEGORIES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    manager.addCategory(line);
                }
            }
            System.out.println("Kategorijas ielādētas!");
        } catch (IOException e) {
            System.out.println("Kļūda ielādējot kategorijas: " + e.getMessage());
        }
    }
}