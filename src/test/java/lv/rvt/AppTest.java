package lv.rvt;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

/**
 * Inventāra pārvaldības sistēmas testi
 * Lai palaistu testus, izmantojiet: mvn test
 */
public class AppTest {
    private InventoryManager manager;
    
    @Before
    public void setup() {
        manager = new InventoryManager();
        FileManager fileManager = new FileManager(manager);
        manager.setFileManager(fileManager);
    }
    
    @Test
    public void testKategorijuDarbibas() {
        // Pievienojam kategorijas
        manager.addCategory("Elektronika");
        manager.addCategory("Pārtika");
        
        // Pārbaudām vai kategorijas ir izveidotas
        assertTrue("Kategorijai vajadzētu eksistēt", manager.categoryExists("Elektronika"));
        assertTrue("Kategorijai vajadzētu eksistēt", manager.categoryExists("Pārtika"));
        
        // Pārbaudām dublikātu novēršanu
        int sakumaSkaits = manager.getCategories().size();
        manager.addCategory("Elektronika");
        assertEquals("Dublikātiem nevajadzētu tikt pievienotiem", sakumaSkaits, manager.getCategories().size());
        
        // Pārbaudām nederīgas kategorijas
        manager.addCategory("");
        manager.addCategory(null);
        assertEquals("Nederīgas kategorijas nedrīkst pievienot", sakumaSkaits, manager.getCategories().size());
    }
    
    @Test
    public void testProduktuDarbibas() {
        // Sagatavojam testu
        manager.addCategory("Elektronika");
        
        // Pievienojam jaunu produktu
        Product produkts = manager.addProduct("Dators", "Elektronika", 999.99, 5);
        
        // Pārbaudām produkta izveidi
        assertNotNull("Produktam jābūt izveidotam", produkts);
        assertEquals("Produkta nosaukumam jābūt pareizam", "Dators", produkts.getName());
        assertEquals("Produkta kategorijai jābūt pareizai", "Elektronika", produkts.getCategory());
        assertEquals("Cenai jābūt pareizai", 999.99, produkts.getPrice(), 0.01);
        assertEquals("Daudzumam jābūt pareizam", 5, produkts.getQuantity());
    }
    
    @Test
    public void testMeklesana() {
        // Sagatavojam testus
        manager.addCategory("Elektronika");
        manager.addCategory("Pārtika");
        
        manager.addProduct("Dators", "Elektronika", 999.99, 5);
        manager.addProduct("Ābols", "Pārtika", 1.0, 100);
        
        // Meklēšana pēc nosaukuma
        List<Product> rezultati = manager.searchByName("Dat");
        assertEquals("Vajadzētu atrast vienu produktu", 1, rezultati.size());
        assertEquals("Vajadzētu atrast Datoru", "Dators", rezultati.get(0).getName());
        
        // Meklēšana pēc kategorijas
        rezultati = manager.searchByCategory("Pārtika");
        assertEquals("Vajadzētu atrast vienu pārtikas produktu", 1, rezultati.size());
    }
    
    @Test
    public void testAprekinasana() {
        // Sagatavojam datus
        manager.addCategory("Elektronika");
        manager.addProduct("Dators", "Elektronika", 1000.0, 2); // Kopā: 2000.0
        manager.addProduct("Pele", "Elektronika", 20.0, 5);     // Kopā: 100.0
        
        // Pārbaudām kopējo vērtību
        double kopVeriba = manager.calculateTotalInventoryValue();
        assertEquals("Kopējai vērtībai jābūt 2100.0", 2100.0, kopVeriba, 0.01);
    }
    
    @Test
    public void testProduktuLabosana() {
        // Sagatavojam testu
        manager.addCategory("Elektronika");
        Product produkts = manager.addProduct("Dators", "Elektronika", 1000.0, 2);
        
        // Labojam produktu
        manager.editProduct(produkts.getId(), "Portatīvais Dators", null, 1500.0, -1);
        Product labotais = manager.findProductById(produkts.getId());
        
        // Pārbaudām izmaiņas
        assertEquals("Nosaukumam jābūt izmainītam", "Portatīvais Dators", labotais.getName());
        assertEquals("Cenai jābūt izmainītai", 1500.0, labotais.getPrice(), 0.01);
        assertEquals("Daudzumam jāpaliek nemainīgam", 2, labotais.getQuantity());
    }
}