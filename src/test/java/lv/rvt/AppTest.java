package lv.rvt;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.util.List;

/**
 * Inventāra pārvaldības sistēmas testi
 */
public class AppTest {
    private InventoryManager manager;
    private static boolean demonstrationMode = false;
    
    // Galvenā metode testu palaišanai
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("demo")) {
            demonstrationMode = true;
            System.out.println("\n=== DEMONSTRĀCIJAS REŽĪMS ===");
            System.out.println("Tiks parādītas mākslīgi radītas kļūdas demonstrācijai\n");
        }

        System.out.println("\n=== Inventāra Pārvaldības Sistēmas Testu Palaišana ===\n");
        Result result = JUnitCore.runClasses(AppTest.class);
        
        // Izvada testu rezultātu kopsavilkumu
        System.out.println("\n=== Testu Kopsavilkums ===");
        System.out.println("Kopējais testu skaits: " + result.getRunCount());
        System.out.println("Sekmīgie testi: " + (result.getRunCount() - result.getFailureCount()));
        System.out.println("Nesekmīgie testi: " + result.getFailureCount());
        System.out.println("Izpildes laiks: " + result.getRunTime() + " ms\n");
        
        // Apstrādā kļūdas, ja tādas ir
        if (result.getFailureCount() > 0) {
            System.out.println("=== Nesekmīgo Testu Detaļas ===");
            for (Failure failure : result.getFailures()) {
                System.out.println("\nTests: " + failure.getTestHeader());
                System.out.println("Kļūda: " + failure.getMessage());
                System.out.println("Sagaidāmā uzvedība: " + getExpectedBehavior(failure.getTestHeader()));
                System.out.println("----------------------------------------");
            }
        } else {
            System.out.println("Visi testi izpildīti sekmīgi!");
        }
    }

    // Atgriež testa sagaidāmo uzvedību
    private static String getExpectedBehavior(String testName) {
        return switch (testName) {
            case "testKategorijuDarbibas" -> 
                "Jāspēj pievienot kategorijas un novērst dublikātus";
            case "testProduktuDarbibas" -> 
                "Jāizveido produkti ar pareizām īpašībām";
            case "testMeklesana" -> 
                "Jāatrod produkti pēc nosaukuma un kategorijas";
            case "testAprekinasana" -> 
                "Jāaprēķina pareiza kopējā inventāra vērtība";
            case "testProduktuLabosana" -> 
                "Jāatjaunina produkta īpašības pareizi";
            default -> "Testam jāizpildās sekmīgi";
        };
    }

    // Sagatavo testa vidi pirms katra testa
    @Before
    public void setup() {
        manager = new InventoryManager();
        FileManager fileManager = new FileManager(manager);
        fileManager.setTestMode(true);
        manager.setFileManager(fileManager);
    }
    
    // Tests kategoriju funkcionalitātei
    @Test
    public void testKategorijuDarbibas() {
        if (demonstrationMode) {
            System.out.println("\nTesta nosaukums: Kategoriju darbības");
            System.out.println("Sagaidāmā uzvedība: " + getExpectedBehavior("testKategorijuDarbibas"));
            // Demonstrējam kļūdu - kategorija netiek pievienota
            manager.addCategory("Electronics");
            assertFalse("DEMO: Kategorijai nevajadzētu eksistēt", 
                manager.categoryExists("Electronics"));
            return;
        }

        manager.addCategory("Electronics");
        manager.addCategory("Elektronika");
        
        assertTrue("Kategorijai vajadzētu eksistēt", manager.categoryExists("Electronics"));
        assertTrue("Kategorijai vajadzētu eksistēt", manager.categoryExists("Elektronika"));
        
        int sakumaSkaits = manager.getCategories().size();
        manager.addCategory("Electronics"); // Mēģinam pievienot dublikātu
        assertEquals("Dublikātiem nevajadzētu tikt pievienotiem", sakumaSkaits, manager.getCategories().size());
    }
    
    // Tests produktu pamatfunkcijām
    @Test
    public void testProduktuDarbibas() {
        if (demonstrationMode) {
            System.out.println("\nTesta nosaukums: Produktu darbības");
            System.out.println("Sagaidāmā uzvedība: " + getExpectedBehavior("testProduktuDarbibas"));
            // Demonstrējam kļūdu - nepareiza cena
            manager.addCategory("Electronics");
            Product produkts = manager.addProduct("Laptop", "Electronics", 999.99, 5);
            assertEquals("DEMO: Cenai jābūt 1099.99", 1099.99, produkts.getPrice(), 0.01);
            return;
        }

        manager.addCategory("Electronics");
        Product produkts = manager.addProduct("Laptop", "Electronics", 999.99, 5);
        
        assertNotNull("Produktam jābūt izveidotam", produkts);
        assertEquals("Produkta nosaukumam jābūt pareizam", "Laptop", produkts.getName());
        assertEquals("Produkta kategorijai jābūt pareizai", "Electronics", produkts.getCategory());
        assertEquals("Cenai jābūt pareizai", 999.99, produkts.getPrice(), 0.01);
        assertEquals("Daudzumam jābūt pareizam", 5, produkts.getQuantity());
    }
    
    // Tests meklēšanas funkcionalitātei
    @Test
    public void testMeklesana() {
        if (demonstrationMode) {
            System.out.println("\nTesta nosaukums: Meklēšana");
            System.out.println("Sagaidāmā uzvedība: " + getExpectedBehavior("testMeklesana"));
            // Demonstrējam kļūdu - meklēšana neatrod eksistējošu produktu
            manager.addCategory("Electronics");
            manager.addProduct("Laptop", "Electronics", 999.99, 5);
            List<Product> rezultati = manager.searchByName("Laptop");
            assertTrue("DEMO: Meklēšanai nevajadzētu atrast rezultātus", 
                rezultati.isEmpty());
            return;
        }

        manager.addCategory("Electronics");
        manager.addProduct("Laptop", "Electronics", 999.99, 5);
        manager.addProduct("Laptop Pro", "Electronics", 1999.99, 3);
        
        List<Product> rezultati = manager.searchByName("Lap");
        assertFalse("Meklēšanai vajadzētu atgriezt rezultātus", rezultati.isEmpty());
        assertTrue("Meklēšanai vajadzētu atrast vismaz vienu produktu ar 'Lap'", 
            rezultati.stream().anyMatch(p -> p.getName().contains("Lap")));
    }
    
    // Tests aprēķinu funkcionalitātei
    @Test
    public void testAprekinasana() {
        if (demonstrationMode) {
            System.out.println("\nTesta nosaukums: Aprēķināšana");
            System.out.println("Sagaidāmā uzvedība: " + getExpectedBehavior("testAprekinasana"));
            demonstrationMode = false;
        }

        double pocetnaSumma = manager.calculateTotalInventoryValue();
        manager.addCategory("Electronics");
        manager.addProduct("TestLaptop", "Electronics", 999.99, 5);
        manager.addProduct("TestMouse", "Electronics", 20.00, 7);
        
        double kopVeriba = manager.calculateTotalInventoryValue();
        double papildusVeriba = (999.99 * 5) + (20.00 * 7);
        assertEquals("Jaunā vērtība jābūt sākotnējai + pievienotā", 
            pocetnaSumma + papildusVeriba, kopVeriba, 0.01);
    }
    
    // Tests produktu labošanas funkcionalitātei
    @Test
    public void testProduktuLabosana() {
        if (demonstrationMode) {
            System.out.println("\nTesta nosaukums: Produktu labošana");
            System.out.println("Sagaidāmā uzvedība: " + getExpectedBehavior("testProduktuLabosana"));
            // Izslēdzam demonstrācijas režīmu šim testam
            demonstrationMode = false;
        }

        manager.addCategory("Electronics");
        Product produkts = manager.addProduct("TestProduct", "Electronics", 1000.0, 5);
        int sakumaId = produkts.getId();
        
        // Saglabājam sākotnējos datus
        String sakumaNosaukums = produkts.getName();
        int sakumaDaudzums = produkts.getQuantity();
        
        // Mainām tikai cenu
        double jaunaCena = 1500.0;
        manager.editProduct(sakumaId, sakumaNosaukums, null, jaunaCena, sakumaDaudzums);
        Product labotais = manager.findProductById(sakumaId);
        
        assertNotNull("Produktam jāeksistē pēc labošanas", labotais);
        assertEquals("Nosaukumam jāpaliek tam pašam", sakumaNosaukums, labotais.getName());
        assertEquals("Cenai jābūt izmainītai", jaunaCena, labotais.getPrice(), 0.01);
        assertEquals("Daudzumam jāpaliek tam pašam", sakumaDaudzums, labotais.getQuantity());
    }
}