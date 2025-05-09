package lv.rvt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class AppTest {
    private final InventoryManager manager = new InventoryManager();
    
    @Test
    public void testBasicFunctionality() {
        // Test product addition
        Product p = manager.addProduct("Test Product", "Test Category", 99.99, 10);
        assertNotNull(p);
        assertEquals("Test Product", p.getName());
        assertEquals(99.99, p.getPrice());
        assertEquals(10, p.getQuantity());
        
        // Test product deletion
        manager.deleteProduct(p.getId());
        assertNull(manager.findProductById(p.getId()));
    }
    
    @Test
    public void testCategoryManagement() {
        // Test category addition
        manager.addCategory("Electronics");
        List<Category> categories = manager.getCategories();
        assertEquals(1, categories.size());
        assertEquals("Electronics", categories.get(0).getName());
        
        // Test product with category
        Product p = manager.addProduct("Laptop", "Electronics", 1000.0, 3);
        assertNotNull(p);
        assertEquals("Electronics", p.getCategory());
        
        // Test category statistics
        Map<String, Double> stats = manager.getCategoryStatistics();
        assertEquals(3000.0, stats.get("Electronics"));
    }
    
    @Test
    public void testSearch() {
        manager.addCategory("Food");
        manager.addCategory("Electronics");
        
        manager.addProduct("Apple", "Food", 1.0, 100);
        manager.addProduct("Laptop", "Electronics", 1000.0, 5);
        
        List<Product> results = manager.searchProducts("app");
        assertEquals(2, results.size()); // Should find both "Apple" and "Laptop"
        
        results = manager.searchProducts("Food");
        assertEquals(1, results.size());
        assertEquals("Apple", results.get(0).getName());
    }
    
    @Test
    public void testAdvancedAnalytics() {
        // Setup test data
        manager.addCategory("Electronics");
        manager.addCategory("Food");
        manager.addProduct("LowStock", "Electronics", 500.0, 2);
        manager.addProduct("HighValue", "Electronics", 2000.0, 5);
        manager.addProduct("Normal", "Food", 10.0, 50);

        // Test total value calculation
        double expectedTotalValue = (500.0 * 2) + (2000.0 * 5) + (10.0 * 50);
        assertEquals(expectedTotalValue, manager.calculateTotalInventoryValue());

        // Test category statistics
        Map<String, Double> categoryValues = manager.getCategoryStatistics();
        assertEquals(11000.0, categoryValues.get("Electronics")); // (500*2 + 2000*5)
        assertEquals(500.0, categoryValues.get("Food")); // (10*50)

        // Test low stock products
        List<Product> lowStock = manager.getLowStockProducts(3);
        assertEquals(1, lowStock.size());
        assertEquals("LowStock", lowStock.get(0).getName());

        // Test price analytics
        Product cheapest = manager.getCheapestProduct();
        assertEquals("Normal", cheapest.getName());
        assertEquals(10.0, cheapest.getPrice());

        Product mostExpensive = manager.getMostExpensiveProduct();
        assertEquals("HighValue", mostExpensive.getName());
        assertEquals(2000.0, mostExpensive.getPrice());

        // Test category analytics
        Map.Entry<String, Double> cheapestCat = manager.getCheapestCategory();
        assertEquals("Food", cheapestCat.getKey());
        assertEquals(500.0, cheapestCat.getValue());

        Map.Entry<String, Double> expensiveCat = manager.getMostExpensiveCategory();
        assertEquals("Electronics", expensiveCat.getKey());
        assertEquals(11000.0, expensiveCat.getValue());
    }
}