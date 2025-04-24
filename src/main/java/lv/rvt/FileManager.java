package lv.rvt;

import java.io.*;
import java.util.List;

public class FileManager {
    private static final String PRODUCTS_FILE = "data/products.csv";
    private static final String CATEGORIES_FILE = "data/categories.csv";

    // Produkta saglabāšana
    public static void saveProducts(List<Product> products) {
        try (FileWriter writer = new FileWriter(PRODUCTS_FILE)) {
            writer.write("ID,ProductName,Category,Price,Quantity\n");
            for (Product product : products) {
                String name = product.getName().replace(",", "\\,");
                String category = product.getCategory().replace(",", "\\,");
                writer.write(String.format("%d,%s,%s,%.2f,%d\n",
                    product.getId(),
                    name,
                    category,
                    product.getPrice(),
                    product.getQuantity()));
            }
            System.out.println("Produkti saglabāti!");
        } catch (IOException e) {
            System.out.println("Kļūda saglabājot produktus: " + e.getMessage());
        }
    }

    // Produktu ielāde no faila
    public static void loadProducts(InventoryManager manager) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) 
                    continue;
                String[] parts = line.split(",", -1); // Handle empty fields
                if (parts.length < 5) {
                    System.out.println("Nepareiza rinda CSV failā: " + line);
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1].replace("\\,", ",");
                    String category = parts[2].replace("\\,", ",");
                    double price = Double.parseDouble(parts[3]);
                    int quantity = Integer.parseInt(parts[4]);
                    Product product = new Product(id, name, category, price, quantity);
                    manager.addLoadedProduct(product);
                } catch (NumberFormatException e) {
                    System.out.println("Kļūda ielādējot skaitliskos datus: " + e.getMessage());
                }
            }
            System.out.println("Produkti ielādēti!");
        } catch (IOException e) {
            System.out.println("Kļūda ielādējot produktus: " + e.getMessage());
        }
    }

    // Kategoriju saglabāšana failā
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

    // Kategoriju ielāde no faila
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