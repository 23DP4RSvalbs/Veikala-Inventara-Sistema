package lv.rvt;

import java.util.*;
import java.util.stream.Collectors;
import lv.rvt.tools.Helper;

// Klase inventāra pārvaldībai - produktu un kategoriju uzturēšanai
public class InventoryManager {
    private final List<Product> products = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private int nextProductId = 1;
    private FileManager fileManager;

    // Iestata FileManager instanci
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    // Atrod nākamo brīvo ID
    private int findNextAvailableId() {
        Set<Integer> usedIds = products.stream()
            .map(Product::getId)
            .collect(Collectors.toSet());
        
        int candidateId = nextProductId;
        while (usedIds.contains(candidateId)) {
            candidateId++;
        }
        nextProductId = candidateId + 1;
        return candidateId;
    }

    // Saglabā izmaiņas failos
    private void saveChanges() {
        if (fileManager != null) {
            fileManager.saveData();
        }
    }

    // === Produktu pārvaldības metodes ===

    // Pievieno jaunu produktu
    public Product addProduct(String name, String category, double price, int quantity) {
        if (!Helper.validateProductData(name, category, price, quantity)) {
            return null;
        }
        
        if (!categoryExists(category)) {
            return null;
        }
        
        int newId = findNextAvailableId();
        Product product = new Product(newId, name, category, price, quantity);
        products.add(product);
        saveChanges();
        return product;
    }

    // Pievieno ielādētu produktu (bez saglabāšanas)
    public void addLoadedProduct(Product product) {
        if (product == null) return;
        
        if (!products.stream().anyMatch(p -> p.getId() == product.getId())) {
            products.add(product);
            nextProductId = Math.max(nextProductId, product.getId() + 1);
        }
    }

    // Rediģē esošu produktu
    public void editProduct(int id, String name, String category, double price, int quantity) {
        Product product = findProductById(id);
        if (product == null) return;

        String newName = name != null ? name : product.getName();
        String newCategory = category != null ? category : product.getCategory();
        double newPrice = price >= 0 ? price : product.getPrice();
        int newQuantity = quantity >= 0 ? quantity : product.getQuantity();

        if (!Helper.validateProductData(newName, newCategory, newPrice, newQuantity)) {
            return;
        }

        if (newCategory != null && !categoryExists(newCategory)) {
            return;
        }

        Product updatedProduct = new Product(id, newName, newCategory, newPrice, newQuantity);
        int index = products.indexOf(product);
        products.set(index, updatedProduct);
        saveChanges();
    }

    // Dzēš produktu pēc ID
    public void deleteProduct(int id) {
        Product product = findProductById(id);
        if (product != null) {
            products.remove(product);
            saveChanges();
        }
    }

    // Parāda visus produktus
    public void showAllProducts() {
        if (products.isEmpty()) {
            System.out.println("Nav produktu!");
        } else {
            for (Product product : products) {
                System.out.println(product);
            }
        }
    }

    // === Meklēšanas metodes ===

    // Meklē produktus pēc kritērija un atslēgvārda
    public void searchProducts(String criterion, String keyword) {
        final String searchCriterion = criterion.toLowerCase();
        final String searchKeyword = keyword.toLowerCase();

        List<Product> found = products.stream()
            .filter(p -> matchesSearch(p, searchCriterion, searchKeyword))
            .collect(Collectors.toList());

        if (found.isEmpty()) {
            System.out.println("Nav atrasts neviens produkts!");
        } else {
            found.forEach(System.out::println);
        }
    }

    // Pārbauda, vai produkts atbilst meklēšanas kritērijam
    private boolean matchesSearch(final Product p, final String criterion, final String keyword) {
        if (criterion.equals("id")) {
            return String.valueOf(p.getId()).contains(keyword);
        } else if (criterion.equals("name")) {
            return p.getName().toLowerCase().contains(keyword);
        } else if (criterion.equals("category")) {
            return p.getCategory().toLowerCase().contains(keyword);
        } else if (criterion.equals("price")) {
            return String.valueOf(p.getPrice()).contains(keyword);
        } else if (criterion.equals("quantity")) {
            return String.valueOf(p.getQuantity()).contains(keyword);
        }
        return false;
    }

    // Meklē produktus pēc atslēgvārda visos laukos
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        keyword = keyword.toLowerCase().trim();
        List<Product> results = new ArrayList<>();
        
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(keyword) ||
                p.getCategory().toLowerCase().contains(keyword)) {
                results.add(p);
            }
        }
        
        return results;
    }

    // Meklē produktus pēc nosaukuma
    public List<Product> searchByName(String name) {
        return products.stream()
            .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<Product> searchByCategory(String category) {
        return products.stream()
            .filter(p -> p.getCategory().toLowerCase().contains(category.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<Product> searchByPriceRange(double min, double max) {
        return products.stream()
            .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
            .collect(Collectors.toList());
    }

    public List<Product> searchByQuantityRange(int min, int max) {
        return products.stream()
            .filter(p -> p.getQuantity() >= min && p.getQuantity() <= max)
            .collect(Collectors.toList());
    }

    // === Kategoriju pārvaldības metodes ===

    // Pievieno jaunu kategoriju
    public void addCategory(String categoryName) {
        if (!Helper.validateCategory(categoryName)) {
            return;
        }
        
        if (!categories.stream()
                .map(Category::getName)
                .anyMatch(name -> name.equalsIgnoreCase(categoryName))) {
            categories.add(new Category(categoryName));
            saveChanges();
            fileManager.cleanInvalidProducts(); // Remove products with nonexistent categories
            fileManager.saveProducts(this.getProducts(), false); // Rewrite products.csv with valid data
        }
    }

    // Parāda visas kategorijas
    public void showAllCategories() {
        if (categories.isEmpty()) {
            System.out.println("Nav nevienas kategorijas");
            return;
        }
        
        System.out.println("\nPieejamās kategorijas:");
        categories.forEach(cat -> System.out.println("- " + cat.getName()));
    }

    // Pārbauda, vai kategorija eksistē
    public boolean categoryExists(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return false;
        }
        return categories.stream()
            .map(Category::getName)
            .anyMatch(name -> name.equalsIgnoreCase(categoryName.trim()));
    }

    // Pārbauda, vai kategorijā ir produkti
    public boolean hasProductsInCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return false;
        }
        
        return products.stream()
            .anyMatch(p -> p.getCategory().equalsIgnoreCase(categoryName.trim()));
    }
    
    public void deleteCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return;
        }
        
        categories.removeIf(c -> c.getName().equalsIgnoreCase(categoryName.trim()));
        saveChanges();
    }

    // === Statistikas metodes ===

    // Aprēķina kopējo inventāra vērtību
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

    public Map<String, Double> getCategoryStatistics() {
        Map<String, Double> stats = new HashMap<>();
        
        for (Category cat : categories) {
            double totalValue = products.stream()
                .filter(p -> p.getCategory().equals(cat.getName()))
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
            stats.put(cat.getName(), totalValue);
        }
        
        return stats;
    }

    // Iegūst produktus ar zemu krājumu
    public List<Product> getLowStockProducts(int threshold) {
        return products.stream()
            .filter(p -> p.getQuantity() <= threshold)
            .sorted(Comparator.comparingInt(Product::getQuantity))
            .collect(Collectors.toList());
    }

    // Iegūst vidējo cenu pēc kategorijas
    public double getAveragePriceByCategory(String category) {
        return products.stream()
            .filter(p -> p.getCategory().equals(category))
            .mapToDouble(Product::getPrice)
            .average()
            .orElse(0.0);
    }

    public int getNextAvailableId() {
        return nextProductId++;
    }

    public Product getCheapestProduct() {
        return products.stream()
            .min(Comparator.comparingDouble(Product::getPrice))
            .orElse(null);
    }

    public Product getMostExpensiveProduct() {
        return products.stream()
            .max(Comparator.comparingDouble(Product::getPrice))
            .orElse(null);
    }

    public Map.Entry<String, Double> getCheapestCategory() {
        return getCategoryStatistics().entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .orElse(null);
    }

    // Iegūst dārgāko kategoriju
    public Map.Entry<String, Double> getMostExpensiveCategory() {
        return getCategoryStatistics().entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);
    }

    // === Getter metodes un palīgmetodes ===

    public List<Product> getProducts() {
        return products;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Product findProductById(int id) {
        return products.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElse(null);
    }
    public List<Product> getProductsByCategory(String category) {
        return products.stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    // Sakārto produktus pēc kritērija un kārtības
    public List<Product> sortProducts(String criterion, String order) {
        List<Product> sorted = new ArrayList<>(products);
        Comparator<Product> comp = null;
        if (criterion.toLowerCase().equals("name")) {
            comp = Comparator.comparing(Product::getName);
        } else if (criterion.toLowerCase().equals("category")) {
            comp = Comparator.comparing(Product::getCategory);
        } else if (criterion.toLowerCase().equals("price")) {
            comp = Comparator.comparing(Product::getPrice);
        } else if (criterion.toLowerCase().equals("quantity")) {
            comp = Comparator.comparing(Product::getQuantity);
        } else {
            comp = Comparator.comparing(Product::getId);
        }

        if (order.equalsIgnoreCase("desc")) {
            comp = comp.reversed();
        }
        
        sorted.sort(comp);
        return sorted;
    }

    // Iegūst produktu skaitu pēc kategorijas
    public Map<String, Integer> getProductCountByCategory() {
        return products.stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }
}