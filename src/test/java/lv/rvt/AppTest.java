package lv.rvt;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

/**
 * Unit tests for the Inventory Management System.
 * Run these tests using: mvn test
 */
public class AppTest {
    private InventoryManager manager;
    
    @Before
    public void setup() {
        manager = new InventoryManager();
    }
    
    @Test
    public void testCategoryOperations() {
        // Test category addition
        manager.addCategory("Electronics");
        manager.addCategory("Food");
        assertTrue("Category should exist after adding", manager.categoryExists("Electronics"));
        assertTrue("Category should exist after adding", manager.categoryExists("Food"));
        
        // Test duplicate category
        int initialSize = manager.getCategories().size();
        manager.addCategory("Electronics");
        assertEquals("Duplicate category should not be added", initialSize, manager.getCategories().size());
        
        // Test invalid category
        manager.addCategory("");
        manager.addCategory(null);
        assertEquals("Invalid categories should not be added", initialSize, manager.getCategories().size());
    }
    
    @Test
    public void testProductOperations() {
        manager.addCategory("Electronics");
        
        // Test product addition
        Product product = manager.addProduct("Laptop", "Electronics", 999.99, 5);
        assertNotNull("Product should be created", product);
        assertEquals("Product name should match", "Laptop", product.getName());
        assertEquals("Product category should match", "Electronics", product.getCategory());
        assertEquals("Product price should match", 999.99, product.getPrice(), 0.01);
        assertEquals("Product quantity should match", 5, product.getQuantity());
        
        // Test product with invalid category
        Product invalidProduct = manager.addProduct("Test", "InvalidCategory", 10.0, 1);
        assertNull("Product with invalid category should not be added", invalidProduct);
        
        // Test product with invalid data
        invalidProduct = manager.addProduct("", "Electronics", -1.0, -1);
        assertNull("Product with invalid data should not be added", invalidProduct);
    }
    
    @Test
    public void testSearchFunctionality() {
        manager.addCategory("Electronics");
        manager.addCategory("Food");
        
        manager.addProduct("Laptop", "Electronics", 999.99, 5);
        manager.addProduct("Apple", "Food", 1.0, 100);
        manager.addProduct("Desktop", "Electronics", 1500.0, 3);
        
        // Test search by name
        List<Product> results = manager.searchByName("top");
        assertEquals("Should find products containing 'top'", 1, results.size());
        assertEquals("Should find 'Laptop'", "Laptop", results.get(0).getName());
        
        // Test search by category
        results = manager.searchByCategory("Electronics");
        assertEquals("Should find products in Electronics category", 2, results.size());
        
        // Test search by price range
        results = manager.searchByPriceRange(0.0, 100.0);
        assertEquals("Should find products in price range", 1, results.size());
        assertEquals("Should find 'Apple'", "Apple", results.get(0).getName());
    }
    
    @Test
    public void testInventoryCalculations() {
        manager.addCategory("Electronics");
        manager.addProduct("Laptop", "Electronics", 1000.0, 2);
        manager.addProduct("Mouse", "Electronics", 20.0, 5);
        
        // Test total inventory value
        double totalValue = manager.calculateTotalInventoryValue();
        assertEquals("Total inventory value should be correct", 2100.0, totalValue, 0.01);
        
        // Test average price
        double avgPrice = manager.calculateAveragePrice();
        assertEquals("Average price should be correct", 510.0, avgPrice, 0.01);
    }
    
    @Test
    public void testProductEditing() {
        manager.addCategory("Electronics");
        Product original = manager.addProduct("Laptop", "Electronics", 1000.0, 2);
        
        // Test editing product
        manager.editProduct(original.getId(), "Desktop PC", null, 1500.0, -1);
        Product edited = manager.findProductById(original.getId());
        
        assertNotNull("Edited product should exist", edited);
        assertEquals("Name should be updated", "Desktop PC", edited.getName());
        assertEquals("Category should remain unchanged", "Electronics", edited.getCategory());
        assertEquals("Price should be updated", 1500.0, edited.getPrice(), 0.01);
        assertEquals("Quantity should remain unchanged", 2, edited.getQuantity());
    }
}