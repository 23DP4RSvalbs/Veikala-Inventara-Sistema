package lv.rvt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Comparator;
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
            id = random.nextInt(900) + 100; 
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

    public void searchProducts(String criterion, String keyword) {
        List<Product> found = new ArrayList<>();
        
        if (criterion.equalsIgnoreCase("id")) {
            try {
                int searchId = Integer.parseInt(keyword);
                
                List<Product> closeMatches = new ArrayList<>();
                List<Product> partialMatches = new ArrayList<>();
                for (Product product : products) {
                    if (product.getId() == searchId) {
                        closeMatches.add(product);
                    } else if (String.valueOf(product.getId()).contains(keyword)) {
                        partialMatches.add(product);
                    }
                }
    
                closeMatches.sort(Comparator.comparingInt(p -> Math.abs(p.getId() - searchId)));
                found.addAll(closeMatches);
                found.addAll(partialMatches);
            } catch (NumberFormatException e) {
                System.out.println("Nederīgs ID formāts!");
                return;
            }
        } else if (criterion.equalsIgnoreCase("name")) {
            List<Product> prefixMatches = new ArrayList<>();
            List<Product> substringMatches = new ArrayList<>();
            for (Product product : products) {
                String name = product.getName().toLowerCase();
                if (name.startsWith(keyword.toLowerCase())) {
                    prefixMatches.add(product);
                } else if (name.contains(keyword.toLowerCase())) {
                    substringMatches.add(product);
                }
            }
            found.addAll(prefixMatches);
            found.addAll(substringMatches);
        } else if (criterion.equalsIgnoreCase("category")) {
            for (Product product : products) {
                if (product.getCategory().toLowerCase().contains(keyword.toLowerCase())) {
                    found.add(product);
                }
            }
        } else if (criterion.equalsIgnoreCase("price")) {
            try {
                double searchPrice = Double.parseDouble(keyword);
                List<Product> closeMatches = new ArrayList<>();
                List<Product> partialMatches = new ArrayList<>();
                for (Product product : products) {
                    if (product.getPrice() == searchPrice) {
                        closeMatches.add(product);
                    } else if (String.valueOf(product.getPrice()).contains(keyword)) {
                        partialMatches.add(product);
                    }
                }

                closeMatches.sort(Comparator.comparingDouble(p -> Math.abs(p.getPrice() - searchPrice)));
                found.addAll(closeMatches);
                found.addAll(partialMatches);
            } catch (NumberFormatException e) {
                System.out.println("Nederīgs cenas formāts!");
                return;
            }
        } else if (criterion.equalsIgnoreCase("quantity")) {
            try {
                int searchQuantity = Integer.parseInt(keyword);
                List<Product> closeMatches = new ArrayList<>();
                List<Product> partialMatches = new ArrayList<>();
                for (Product product : products) {
                    if (product.getQuantity() == searchQuantity) {
                        closeMatches.add(product);
                    } else if (String.valueOf(product.getQuantity()).contains(keyword)) {
                        partialMatches.add(product);
                    }
                }
    
                closeMatches.sort(Comparator.comparingInt(p -> Math.abs(p.getQuantity() - searchQuantity)));
                found.addAll(closeMatches);
                found.addAll(partialMatches);
            } catch (NumberFormatException e) {
                System.out.println("Nederīgs daudzuma formāts!");
                return;
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

    public void filterAndSortByPrice(double minPrice, double maxPrice, boolean ascending) {
        List<Product> filtered = new ArrayList<>();
        for (Product product : products) {
            if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                filtered.add(product);
            }
        }
        if (ascending) {
            filtered.sort(Comparator.comparingDouble(Product::getPrice));
        } else {
            filtered.sort(Comparator.comparingDouble(Product::getPrice).reversed());
        }
        if (filtered.isEmpty()) {
            System.out.println("Nav produktu šajā cenu diapazonā!");
        } else {
            for (Product product : filtered) {
                System.out.println(product);
            }
        }
    }

    public void filterAndSortByQuantity(int minQuantity, int maxQuantity, boolean ascending) {
        List<Product> filtered = new ArrayList<>();
        for (Product product : products) {
            if (product.getQuantity() >= minQuantity && product.getQuantity() <= maxQuantity) {
                filtered.add(product);
            }
        }
        if (ascending) {
            filtered.sort(Comparator.comparingInt(Product::getQuantity));
        } else {
            filtered.sort(Comparator.comparingInt(Product::getQuantity).reversed());
        }
        if (filtered.isEmpty()) {
            System.out.println("Nav produktu šajā daudzuma diapazonā!");
        } else {
            for (Product product : filtered) {
                System.out.println(product);
            }
        }
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