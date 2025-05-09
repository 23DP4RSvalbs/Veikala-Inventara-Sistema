package lv.rvt;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import lv.rvt.interfaces.DataManagement;
import lv.rvt.tools.CsvHelper;
import lv.rvt.tools.Helper;
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
            throw new RuntimeException("Failed to create necessary directories: " + e.getMessage());
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
            throw new RuntimeException("Failed to create data files: " + e.getMessage());
        }
    }

    public boolean bothDataFilesExist() {
        return Files.exists(Paths.get(PRODUCTS_FILE)) && Files.exists(Paths.get(CATEGORIES_FILE));
    }

    public void checkAndClearIfFilesDeleted() {
        if (!bothDataFilesExist()) {
            System.out.println("Detected that .csv files are missing. Clearing in-memory data to avoid stale data.");
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
                System.out.println("Removing invalid product with nonexistent category: " + product);
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

    @Override
    public void saveData() {
        if (!isImporting) {
            createDataFilesIfMissing();
            saveCategories(manager.getCategories(), true);
            cleanInvalidProducts();
            BackupConfig.getInstance().incrementChanges();
        }
    }

    @Override
    public void loadData() {
        createDataFilesIfMissing();
        if (bothDataFilesExist()) {
            ImportResult categoryResult = loadAndValidateCategories();
            System.out.printf("Categories loaded: %d/%d valid%n", 
                categoryResult.getValidCount(), categoryResult.getTotalCount());
            if (!categoryResult.getErrors().isEmpty()) {
                System.out.println("Category validation errors:");
                categoryResult.getErrors().forEach(System.out::println);
            }

            ImportResult productResult = loadAndValidateProducts();
            System.out.printf("Products loaded: %d/%d valid%n", 
                productResult.getValidCount(), productResult.getTotalCount());
            if (!productResult.getErrors().isEmpty()) {
                System.out.println("Product validation errors:");
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
        boolean fileExisted = Files.exists(Paths.get(PRODUCTS_FILE));
        if (products.isEmpty() && fileExisted && !isImporting) {
            System.out.println("Skipping save of empty product list to preserve existing products.csv");
            return;
        }
        System.out.println("Saving products.csv with " + products.size() + " products");
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
                System.out.println("Since there wasn't a products.csv file, a new one was created instantly.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save products: " + e.getMessage());
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
        boolean fileExisted = Files.exists(Paths.get(CATEGORIES_FILE));
        System.out.println("Saving categories.csv with " + categories.size() + " categories");
        try (FileWriter writer = new FileWriter(CATEGORIES_FILE)) {
            for (Category category : categories) {
                writer.write(CsvHelper.escapeCsv(category.getName()) + "\n");
            }
            if (incrementBackup) {
                BackupConfig.getInstance().incrementChanges();
            }
            if (!fileExisted) {
                System.out.println("Since there wasn't a categories.csv file, a new one was created instantly.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save categories: " + e.getMessage());
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

    public class ImportResult {
        private int totalCount;
        private int validCount;
        private List<String> errors;

        public ImportResult() {
            this.errors = new ArrayList<>();
            this.totalCount = 0;
            this.validCount = 0;
        }

        public void incrementTotal() {
            totalCount++;
        }

        public void incrementValid() {
            validCount++;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int getValidCount() {
            return validCount;
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public void addError(String error) {
            errors.add(error);
        }
    }

    private ImportResult loadAndValidateCategories() {
        ImportResult result = new ImportResult();
        Set<String> uniqueCategories = new HashSet<>();
        manager.getCategories().clear();
        
        if (!Files.exists(Paths.get(CATEGORIES_FILE))) {
            return result;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(CATEGORIES_FILE))) {
            String line;
            int currentLine = 0;
            while ((line = reader.readLine()) != null) {
                final int lineNumber = ++currentLine;
                if (line.trim().isEmpty()) continue;
                result.incrementTotal();
                
                try {
                    String[] parts = CsvHelper.parseLine(line);
                    if (parts.length == 0) {
                        result.addError(String.format("Rinda %d: Tukša rinda", lineNumber));
                        continue;
                    }
                    if (parts.length > 1) {
                        result.addError(String.format("Rinda %d: Pārāk daudz kolonnu, vajadzīga tikai kategorija", lineNumber));
                        continue;
                    }
                    
                    String category = parts[0].trim();
                    if (category.isEmpty()) {
                        result.addError(String.format("Rinda %d: Tukša kategorija", lineNumber));
                        continue;
                    }
                    
                    Helper.ValidationResult validation = Helper.validateCsvCategoryLine(category);
                    if (validation.isValid()) {
                        if (uniqueCategories.add(category)) {
                            manager.addCategory(category);
                            result.incrementValid();
                        } else {
                            result.addError(String.format("Rinda %d: Dublēta kategorija '%s'", lineNumber, category));
                        }
                    } else {
                        validation.getErrors().forEach(err -> {
                            result.addError(String.format("Rinda %d: %s", lineNumber, err));
                        });
                    }
                } catch (Exception e) {
                    result.addError(String.format("Rinda %d: Kļūda - %s", lineNumber, e.getMessage()));
                }
            }
        } catch (IOException e) {
            result.addError("Neizdevās nolasīt kategoriju failu: " + e.getMessage());
        }
        
        return result;
    }

    private ImportResult loadAndValidateProducts() {
        ImportResult result = new ImportResult();
        Set<Integer> uniqueIds = new HashSet<>();
        Map<Integer, List<Product>> originalProducts = new HashMap<>();
        List<Product> tempProducts = new ArrayList<>();
        
        if (!Files.exists(Paths.get(PRODUCTS_FILE))) {
            return result;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String header = reader.readLine();
            if (header == null) {
                result.addError("products.csv ir tukšs vai trūkst galvenes");
                return result;
            }
            if (!header.trim().equals("ID,ProductName,Category,Price,Quantity")) {
                result.addError("Nederīgs faila formāts - trūkst vai nepareiza galvene: " + header);
                return result;
            }

            String line;
            int currentLine = 1;
            while ((line = reader.readLine()) != null) {
                final int lineNumber = ++currentLine;
                if (line.trim().isEmpty()) {
                    continue;
                }
                result.incrementTotal();
                
                try {
                    String[] parts = CsvHelper.parseLine(line);
                    
                    if (parts.length != 5) {
                        result.addError(String.format("Rinda %d: Nepareizs kolonnu skaits (%d), vajag 5", lineNumber, parts.length));
                        continue;
                    }

                    Helper.ValidationResult validation = Helper.validateCsvProductLine(parts);
                    if (!validation.isValid()) {
                        validation.getErrors().forEach(err -> {
                            result.addError(String.format("Rinda %d: %s", lineNumber, err));
                        });
                        continue;
                    }

                    int id;
                    try {
                        id = Integer.parseInt(parts[0].trim());
                        if (!uniqueIds.add(id)) {
                            result.addError(String.format("Rinda %d: Dublēts produkta ID: %d", lineNumber, id));
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        result.addError(String.format("Rinda %d: Nederīgs ID formāts: %s", lineNumber, parts[0]));
                        continue;
                    }

                    String name = parts[1].trim();
                    String category = parts[2].trim();
                    double price;
                    int quantity;
                    try {
                        price = Double.parseDouble(parts[3].trim());
                        quantity = Integer.parseInt(parts[4].trim());
                    } catch (NumberFormatException e) {
                        result.addError(String.format("Rinda %d: Nederīgs cenu vai daudzuma formāts: %s", lineNumber, e.getMessage()));
                        continue;
                    }

                    if (!manager.categoryExists(category)) {
                        result.addError(String.format("Rinda %d: Nederīga kategorija '%s' - kategorija neeksistē", lineNumber, category));
                        continue;
                    }

                    Product product = new Product(id, name, category, price, quantity);
                    originalProducts.computeIfAbsent(id, k -> new ArrayList<>()).add(product);
                    result.incrementValid();
                } catch (Exception e) {
                    result.addError(String.format("Rinda %d: Kļūda - %s", lineNumber, e.getMessage()));
                }
            }
        } catch (IOException e) {
            result.addError("Neizdevās nolasīt produktu failu: " + e.getMessage());
        }

        Set<Integer> loadedIds = new HashSet<>();
        for (Map.Entry<Integer, List<Product>> entry : originalProducts.entrySet()) {
            int originalId = entry.getKey();
            List<Product> products = entry.getValue();

            Product firstProduct = products.get(0);
            int newId = originalId;
            if (!loadedIds.add(originalId)) {
                newId = manager.getNextAvailableId();
            }
            tempProducts.add(new Product(newId, firstProduct.getName(), 
                firstProduct.getCategory(), firstProduct.getPrice(), firstProduct.getQuantity()));

            for (int i = 1; i < products.size(); i++) {
                Product p = products.get(i);
                int nextId = manager.getNextAvailableId();
                tempProducts.add(new Product(nextId, p.getName(), 
                    p.getCategory(), p.getPrice(), p.getQuantity()));
            }
        }

        manager.getProducts().clear();
        manager.getProducts().addAll(tempProducts);
        
        return result;
    }

    @Override
    public void importData(String format) {
        if (!format.equalsIgnoreCase("csv")) {
            throw new IllegalArgumentException("Only CSV format is supported");
        }

        if (!bothDataFilesExist()) {
            System.out.println("Cannot import: both products.csv and categories.csv must be present.");
            return;
        }

        System.out.println("Starting import process...");
        isImporting = true;
        
        ImportResult categoryResult = loadAndValidateCategories();
        ImportResult productResult = loadAndValidateProducts();
        
        System.out.printf("Categories validation: %d/%d valid%n", 
            categoryResult.getValidCount(), categoryResult.getTotalCount());
        System.out.printf("Products validation: %d/%d valid%n", 
            productResult.getValidCount(), productResult.getTotalCount());
        
        if (!categoryResult.getErrors().isEmpty() || !productResult.getErrors().isEmpty()) {
            System.out.println("Some entries were skipped due to validation errors:");
            categoryResult.getErrors().forEach(System.out::println);
            productResult.getErrors().forEach(System.out::println);
        }

        isImporting = false;
        System.out.println("Saving valid imported data...");
        RecoveryManager.createBackup();
        if (categoryResult.getValidCount() > 0) {
            saveCategories(manager.getCategories(), false);
        }
        cleanInvalidProducts();
        saveProducts(manager.getProducts(), false);
        System.out.println("Import completed with valid data.");
    }
}