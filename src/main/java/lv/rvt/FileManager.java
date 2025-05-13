package lv.rvt;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import lv.rvt.interfaces.DataManagement;
import lv.rvt.tools.BackupConfig;
import lv.rvt.tools.CsvHelper;
import lv.rvt.tools.Helper;
import lv.rvt.tools.ImportResult;
import lv.rvt.tools.RecoveryManager;

public class FileManager implements DataManagement {
    private static final String DATA_DIR = "data";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.csv";
    private static final String CATEGORIES_FILE = DATA_DIR + "/categories.csv";
    private static final String EXPORT_DIR = DATA_DIR + "/export";
    private static final String BACKUP_DIR = DATA_DIR + "/backup";

    private final InventoryManager manager;
    private boolean isImporting = false;
    private boolean isInitializing = false;
    private boolean testMode = false;
    /** Used to control logging output throughout this class */
    @SuppressWarnings("unused")  // Field is used throughout class
    private boolean silentMode = false; // Jauns lauks paziņojumu atslēgšanai

    public FileManager(InventoryManager manager) {
        this.manager = manager;
        manager.setFileManager(this);
        initializeDataFiles();
        isInitializing = true;
        loadData();
        isInitializing = false;
    }

    private void initializeDataFiles() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(EXPORT_DIR));
            Files.createDirectories(Paths.get(BACKUP_DIR));
            createDataFilesIfMissing();
        } catch (IOException e) {
            throw new RuntimeException("⚠ Neizdevās izveidot nepieciešamās mapes: " + e.getMessage());
        }
    }

    private void createDataFilesIfMissing() {
        try {
            if (!Files.exists(Paths.get(PRODUCTS_FILE))) {
                Files.createFile(Paths.get(PRODUCTS_FILE));
            }
            if (!Files.exists(Paths.get(CATEGORIES_FILE))) {
                Files.createFile(Paths.get(CATEGORIES_FILE));
            }
        } catch (IOException e) {
            throw new RuntimeException("⚠ Neizdevās izveidot datu failus: " + e.getMessage());
        }
    }

    public boolean bothDataFilesExist() {
        return Files.exists(Paths.get(PRODUCTS_FILE)) && Files.exists(Paths.get(CATEGORIES_FILE));
    }

    public void checkAndClearIfFilesDeleted() {
        if (!bothDataFilesExist()) {
            System.out.println("CSV faili nav atrasti. Notīra atmiņā esošos datus, lai izvairītos no novecojušiem datiem.");
            manager.getProducts().clear();
            manager.getCategories().clear();
        }
    }

    public void cleanInvalidProducts() {
        List<Product> currentProducts = new ArrayList<>(manager.getProducts());
        List<Product> validProducts = new ArrayList<>();
        boolean hasInvalidData = false;

        for (Product product : currentProducts) {
            if (manager.categoryExists(product.getCategory())) {
                validProducts.add(product);
            } else {
                System.out.println("Dzēš produktu ar neeksistējošu kategoriju: " + product);
                hasInvalidData = true;
            }
        }

        if (hasInvalidData) {
            manager.getProducts().clear();
            manager.getProducts().addAll(validProducts);
            if (!isInitializing) {
                saveProducts(validProducts, false);
            }
        }
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
        this.silentMode = testMode; // Testa režīmā atslēdzam paziņojumus
    }

    @Override
    public void saveData() {
        if (testMode) return; // Skip file operations in test mode
        if (!isImporting) {
            createDataFilesIfMissing();
            saveCategories(manager.getCategories(), true);
            cleanInvalidProducts();
            saveProducts(manager.getProducts(), true);
        }
    }

    @Override
    public void loadData() {
        if (testMode) return; // Skip file operations in test mode
        createDataFilesIfMissing();
        if (bothDataFilesExist()) {
            ImportResult categoryResult = loadAndValidateCategories();
            System.out.printf("Ielādētas kategorijas: %d/%d derīgas%n", 
                categoryResult.getValidCount(), categoryResult.getTotalCount());
            if (!categoryResult.getErrors().isEmpty()) {
                System.out.println("Kategoriju validācijas kļūdas:");
                categoryResult.getErrors().forEach(System.out::println);
            }

            ImportResult productResult = loadAndValidateProducts();
            System.out.printf("Ielādēti produkti: %d/%d derīgi%n", 
                productResult.getValidCount(), productResult.getTotalCount());
            if (!productResult.getErrors().isEmpty()) {
                System.out.println("Produktu validācijas kļūdas:");
                productResult.getErrors().forEach(System.out::println);
            }

            if (productResult.getValidCount() > 0) {
                saveProducts(manager.getProducts(), false);
                cleanInvalidProducts();
            } else {
                System.out.println("No valid products loaded; preserving existing products.csv");
            }
        }
    }

    public void saveAll() {
        createDataFilesIfMissing();
        saveCategories(manager.getCategories(), false);
        saveProducts(manager.getProducts(), false);
        cleanInvalidProducts();
    }

    public void loadAll() {
        createDataFilesIfMissing();
        if (bothDataFilesExist()) {
            loadAndValidateCategories();
            loadAndValidateProducts();
            if (manager.getProducts().size() > 0) {
                saveProducts(manager.getProducts(), false);
            }
            cleanInvalidProducts();
        }
    }

    public void saveProducts(List<Product> products, boolean incrementBackup) {
        if (testMode) return; // Skip file operations in test mode
        boolean fileExisted = Files.exists(Paths.get(PRODUCTS_FILE));
        if (products.isEmpty() && fileExisted && !isImporting) {
            System.out.println("Izlaista tukša produktu saraksta saglabāšana, lai saglabātu esošo products.csv");
            return;
        }
        System.out.println("Saglabā products.csv ar " + products.size() + " produktiem");
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            writer.println("ID,ProductName,Category,Price,Quantity");
            if (!products.isEmpty()) {
                for (Product p : products) {
                    writer.printf("%s,%s,%s,%s,%s%n", 
                        CsvHelper.escapeCsv(String.valueOf(p.getId())),
                        CsvHelper.escapeCsv(p.getName()),
                        CsvHelper.escapeCsv(p.getCategory()),
                        CsvHelper.escapeCsv(String.format("%.2f", p.getPrice())),
                        CsvHelper.escapeCsv(String.valueOf(p.getQuantity())));
                }
            }
            if (incrementBackup) {
                BackupConfig.getInstance().incrementChanges();
            }
            if (!fileExisted) {
                System.out.println("Tā kā products.csv fails neeksistēja, tas tika izveidots no jauna.");
            }
        } catch (IOException e) {
            throw new RuntimeException("⚠ Neizdevās saglabāt produktus: " + e.getMessage());
        }
    }

    public void loadProducts() {
        loadAndValidateProducts();
        if (manager.getProducts().size() > 0) {
            saveProducts(manager.getProducts(), false);
        }
        cleanInvalidProducts();
    }

    private void saveCategories(List<Category> categories, boolean incrementBackup) {
        if (testMode) return; // Skip file operations in test mode
        boolean fileExisted = Files.exists(Paths.get(CATEGORIES_FILE));
        // Filter out any invalid categories before saving
        List<Category> validCategories = categories.stream()
            .filter(c -> Helper.validateCategory(c.getName()))
            .collect(Collectors.toList());
        
        if (validCategories.size() < categories.size()) {
            System.out.println("⚠ " + (categories.size() - validCategories.size()) + " nederīgas kategorijas tika noņemtas");
            categories.clear();
            categories.addAll(validCategories);
        }
        
        System.out.println("Saglabā categories.csv ar " + validCategories.size() + " kategorijām");
        try (FileWriter writer = new FileWriter(CATEGORIES_FILE)) {
            for (Category category : validCategories) {
                writer.write(CsvHelper.escapeCsv(category.getName()) + "\n");
            }
            if (incrementBackup) {
                BackupConfig.getInstance().incrementChanges();
            }
            if (!fileExisted) {
                System.out.println("Tā kā categories.csv fails neeksistēja, tas tika izveidots no jauna.");
            }
        } catch (IOException e) {
            throw new RuntimeException("⚠ Neizdevās saglabāt kategorijas: " + e.getMessage());
        }
    }

    public void loadCategories() {
        loadAndValidateCategories();
    }

    public void exportData(String format) {
        if (!format.equalsIgnoreCase("csv")) {
            throw new IllegalArgumentException("Tikai CSV formāts ir atbalstīts");
        }

        if (!bothDataFilesExist()) {
            throw new IllegalStateException("Nav atrasti products.csv un categories.csv faili");
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String exportDir = EXPORT_DIR + "/" + timestamp;
        Path exportPath = Paths.get(exportDir);

        try {
            Files.createDirectories(exportPath);
            
            Path productsPath = exportPath.resolve("products.csv");
            try (PrintWriter writer = new PrintWriter(new FileWriter(productsPath.toString()))) {
                for (Product p : manager.getProducts()) {
                    writer.println(String.format("%d,%s,%s,%.2f,%d",
                        p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity()));
                }
            }

            Path categoriesPath = exportPath.resolve("categories.csv");
            try (PrintWriter writer = new PrintWriter(new FileWriter(categoriesPath.toString()))) {
                for (Category c : manager.getCategories()) {
                    writer.println(c.getName());
                }
            }

            System.out.println("Dati veiksmīgi eksportēti uz: " + exportDir);
        } catch (IOException e) {
            throw new RuntimeException("Eksportēšana neizdevās: " + e.getMessage());
        }
    }

    private ImportResult loadAndValidateCategories() {
        ImportResult result = new ImportResult();
        Set<String> uniqueCategories = new HashSet<>();
        manager.getCategories().clear();
        
        if (!Files.exists(Paths.get(CATEGORIES_FILE))) {
            return result;
        }

        List<Category> tempCategories = new ArrayList<>();
        List<String> validLines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(CATEGORIES_FILE))) {
            String line;
            int currentLine = 0;
            while ((line = reader.readLine()) != null) {
                final int lineNumber = ++currentLine;
                line = line.trim();
                if (line.isEmpty()) continue;
                
                result.incrementTotal();
                String category = line;
                
                if (!Helper.validateCategory(category)) {
                    result.addError(String.format("Rinda %d: Nederīga kategorija: %s", lineNumber, category));
                    continue;
                }

                if (uniqueCategories.add(category)) {
                    tempCategories.add(new Category(category));
                    validLines.add(category);
                    result.incrementValid();
                } else {
                    result.addError(String.format("Rinda %d: Dublēta kategorija '%s'", lineNumber, category));
                }
            }
        } catch (IOException e) {
            result.addError("Neizdevās nolasīt kategoriju failu: " + e.getMessage());
            return result;
        }

        // Update categories if we have valid ones
        if (result.getValidCount() > 0) {
            manager.getCategories().addAll(tempCategories);
            
            // Update the categories file to contain only valid categories
            try (PrintWriter writer = new PrintWriter(new FileWriter(CATEGORIES_FILE))) {
                for (String category : validLines) {
                    writer.println(category);
                }
                System.out.println("Kategoriju fails tika atjaunots, dzēšot " + 
                    (result.getTotalCount() - result.getValidCount()) + " nederīgas kategorijas");
            } catch (IOException e) {
                result.addError("Neizdevās atjaunot kategoriju failu: " + e.getMessage());
            }
        }
        
        return result;
    }

    private ImportResult loadAndValidateProducts() {
        ImportResult result = new ImportResult();
        Set<Integer> uniqueIds = new HashSet<>();
        List<Product> tempProducts = new ArrayList<>();

        // Skip if file doesn't exist
        if (!Files.exists(Paths.get(PRODUCTS_FILE))) {
            System.out.println("Produktu fails nav atrasts.");
            return result;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(PRODUCTS_FILE));
            
            // Skip empty file
            if (lines.isEmpty()) {
                System.out.println("Produktu fails ir tukšs.");
                return result;
            }

            // Validate header
            String header = lines.get(0).trim();
            if (!header.equals("ID,ProductName,Category,Price,Quantity")) {
                result.addError("Nederīgs faila formāts - trūkst vai nepareiza galvene");
                return result;
            }

            // Process each data line
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                result.incrementTotal();
                String[] parts = CsvHelper.parseLine(line);

                if (parts.length != 5) {
                    result.addError(String.format("Rinda %d: Nepareizs kolonnu skaits", i + 1));
                    continue;
                }

                try {
                    // Parse and validate ID
                    int id = Integer.parseInt(parts[0].trim());
                    if (!uniqueIds.add(id)) {
                        result.addError(String.format("Rinda %d: Dublēts produkta ID: %d", i + 1, id));
                        continue;
                    }

                    String name = parts[1].trim();
                    String category = parts[2].trim();
                    double price = Double.parseDouble(parts[3].trim());
                    int quantity = Integer.parseInt(parts[4].trim());

                    // Validate category exists
                    if (!manager.categoryExists(category)) {
                        result.addError(String.format("Rinda %d: Nederīga kategorija '%s'", i + 1, category));
                        continue;
                    }

                    // Validate price and quantity
                    if (!Helper.validatePrice(price)) {
                        result.addError(String.format("Rinda %d: Nederīga cena '%.2f' (jābūt starp 0 un %.2f)", i + 1, price, Helper.MAX_PRICE));
                        continue;
                    }
                    if (!Helper.validateQuantity(quantity)) {
                        result.addError(String.format("Rinda %d: Nederīgs daudzums '%d' (jābūt starp 0 un %d)", i + 1, quantity, Helper.MAX_QUANTITY));
                        continue;
                    }

                    // Create the product
                    Product product = new Product(id, name, category, price, quantity);
                    tempProducts.add(product);
                    result.incrementValid();

                } catch (NumberFormatException e) {
                    result.addError(String.format("Rinda %d: Nederīgs skaitlis: %s", i + 1, e.getMessage()));
                } catch (Exception e) {
                    result.addError(String.format("Rinda %d: %s", i + 1, e.getMessage()));
                }
            }

            // Update products if we have valid ones
            if (!tempProducts.isEmpty()) {
                manager.getProducts().clear();
                manager.getProducts().addAll(tempProducts);
            }

        } catch (IOException e) {
            result.addError("Neizdevās nolasīt produktu failu: " + e.getMessage());
        }

        return result;
    }

    @Override
    public void importData(String format) {
        if (!format.equalsIgnoreCase("csv")) {
            throw new IllegalArgumentException("⚠ Tikai CSV formāts ir atbalstīts");
        }

        // Store existing data
        List<Product> existingProducts = new ArrayList<>(manager.getProducts());
        List<Category> existingCategories = new ArrayList<>(manager.getCategories());

        // Create backup before making changes
        System.out.println("✓ Veido rezerves kopiju pirms importēšanas...");
        RecoveryManager.createBackup();

        System.out.println("✓ Sāk importēšanas procesu...");
        isImporting = true;

        try {
            // Process categories first
            System.out.println("✓ Importē kategorijas...");
            ImportResult categoryResult = loadAndValidateCategories();
            System.out.printf("Kategoriju validācija: %d/%d derīgas%n", 
                categoryResult.getValidCount(), categoryResult.getTotalCount());
            
            if (categoryResult.getErrors().size() > 0) {
                System.out.println("\nKategoriju validācijas kļūdas:");
                categoryResult.getErrors().forEach(error -> System.out.println("KĻŪDA: " + error));
            }

            // Save valid categories
            if (categoryResult.getValidCount() > 0) {
                saveCategories(manager.getCategories(), false);
            } else {
                System.out.println("⚠ Nav derīgu kategoriju, izmanto esošās kategorijas");
                manager.getCategories().clear();
                manager.getCategories().addAll(existingCategories);
            }

            // Now process products since categories are loaded
            System.out.println("\nImportē produktus...");
            ImportResult productResult = loadAndValidateProducts();
            System.out.printf("Produktu validācija: %d/%d derīgi%n", 
                productResult.getValidCount(), productResult.getTotalCount());
            
            if (productResult.getErrors().size() > 0) {
                System.out.println("\nProduktu validācijas kļūdas:");
                productResult.getErrors().forEach(error -> System.out.println("KĻŪDA: " + error));
            }

            if (productResult.getValidCount() > 0) {
                cleanInvalidProducts();
                saveProducts(manager.getProducts(), false);
                System.out.println("Imports veiksmīgi pabeigts");
            } else {
                System.out.println("Nav derīgu produktu, atjauno sākotnējos datus");
                manager.getProducts().clear();
                manager.getProducts().addAll(existingProducts);
            }

        } catch (Exception e) {
            System.out.println("\nKļūda importa laikā: " + e.getMessage());
            // Restore original data
            manager.getCategories().clear();
            manager.getCategories().addAll(existingCategories);
            manager.getProducts().clear();
            manager.getProducts().addAll(existingProducts);
        } finally {
            isImporting = false;
        }
    }
}