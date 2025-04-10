package lv.rvt;

import java.io.*;
import java.util.List;

public class FileManager {
    private static final String PRODUCTS_FILE = "data/products.csv";
    private static final String CATEGORIES_FILE = "data/categories.csv";

    public static void saveProducts(List<Product> products) {
        try {
            
            FileWriter writer = new FileWriter(PRODUCTS_FILE);
            writer.write("ID,ProductName,Price,Quantity");
            for (Product product : products) 
            
            {
                writer.write(product.getId() + ", " + product.getName() + ", " + product.getPrice() + "," + product.getQuantity() + "\n ");
            }
            writer.close();
        
        }
    }

    public static void loadProducts(InventoryManager manager) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE));
            String line = reader.readLine(); // Izlaižam galveni


            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                int id = Integer.parseInt(parts[0]);

                String name = parts[1];
            reader.close();

        }
        }

        public static void loadCategories(InventoryManager manager) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(CATEGORIES_FILE));
                
        }
}
}
