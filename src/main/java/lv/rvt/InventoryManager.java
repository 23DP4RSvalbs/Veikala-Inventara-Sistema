package lv.rvt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lv.rvt.tools.Helper;

public class InventoryManager {
    private List<Product> products;
    private List<Category> categories;

    public InventoryManager() {
        products = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public void addProduct(String name, String category, double price, int quantity) {
        for (Product p : products) {
            if (p.getName().equals(name) &&
                p.getCategory().equals(category) &&
                p.getPrice() == price &&
                p.getQuantity() == quantity) {
                System.out.println("Produkts ar šādiem datiem jau eksistē!");
                return;
            }
        }
        if (!Helper.validateProductName(name) || !Helper.validateCategory(category)) {
            System.out.println("Nederīgi produkta dati!");
            return;
        }
        int id = generateUniqueId();
        Product product = new Product(id, name, category, price, quantity);
        products.add(product);
        System.out.println("Produkts pievienots: " + product);
    }

    public void addLoadedProduct(Product product) {
        products.add(product);
    }

    private int generateUniqueId() {
        Random random = new Random();
        int id;
        do {
            id = random.nextInt(900) + 100; // 100 to 999
        } while (findProductById(id) != null);
        return id;
    }

    public void editProduct(int id, String name, String category, double price, int quantity) {
        Product product = findProductById(id);
        if (product != null) {
            if (name != null && !name.isEmpty() && Helper.validateProductName(name)) {
                product.setName(name);
            }
            if (category != null && !category.isEmpty() && Helper.validateCategory(category)) {
                product.setCategory(category);
            }
            if (price != -1) {
                product.setPrice(price);
            }
            if (quantity != -1) {
                product.setQuantity(quantity);
            }
            System.out.println("Produkts atjaunināts: " + product);
        } else {
            System.out.println("Produkts nav atrasts!");
        }
    }

    public void deleteProduct(int id) {
        Product product = findProductById(id);
        if (product != null) {
            products.remove(product);
            System.out.println("Produkts izdzēsts: " + product);
        } else {
            System.out.println("Produkts nav atrasts!");
        }
    }

    public void showAllProducts() {
        if (products.isEmpty()) {
            System.out.println("Nav produktu!");
        } else {
            for (Product product : products) {
                System.out.println(product);
            }
        }
    }

    public void searchProducts(String keyword) {
        List<Product> found = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(keyword.toLowerCase())) {
                found.add(product);
            }
        }
        if (found.isEmpty()) {
            System.out.println("Nav atrasts neviens produkts!");
        } else {
            for (Product product : found) {
                System.out.println(product);
            }
        }
    }

    public void filterProductsByCategory(String category) {
        List<Product> found = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                found.add(product);
            }
        }
        if (found.isEmpty()) {
            System.out.println("Nav produktu šajā kategorijā!");
        } else {
            for (Product product : found) {
                System.out.println(product);
            }
        }
    }

    public void sortProductsByPrice() {
        List<Product> sorted = new ArrayList<>(products);
        for (int i = 0; i < sorted.size() - 1; i++) {
            for (int j = 0; j < sorted.size() - i - 1; j++) {
                if (sorted.get(j).getPrice() > sorted.get(j + 1).getPrice()) {
                    Product temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                }
            }
        }
        for (Product product : sorted) {
            System.out.println(product);
        }
    }

    public double calculateTotalInventoryValue() {
        double total = 0;
        for (Product product : products) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    public double calculateAveragePrice() {
        if (products.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (Product product : products) {
            total += product.getPrice();
        }
        return total / products.size();
    }

    public void addCategory(String name) {
        if (!Helper.validateCategory(name)) {
            System.out.println("Nederīgs kategorijas nosaukums!");
            return;
        }
        Category category = new Category(name);
        categories.add(category);
        System.out.println("Kategorija pievienota: " + category);
    }

    public void showAllCategories() {
        if (categories.isEmpty()) {
            System.out.println("Nav kategoriju!");
        } else {
            for (Category category : categories) {
                System.out.println(category);
            }
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Category> getCategories() {
        return categories;
    }

    private Product findProductById(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }
}