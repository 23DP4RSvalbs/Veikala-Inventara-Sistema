package lv.rvt.tools;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecoveryManager {
    private static final String DATA_DIR = "data";
    private static final String BACKUP_DIR = DATA_DIR + "/backup";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.csv";
    private static final String CATEGORIES_FILE = DATA_DIR + "/categories.csv";

    public static void createBackup() {
        try {
       
            Files.createDirectories(Paths.get(BACKUP_DIR));

            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupPath = BACKUP_DIR + "/" + timestamp;
            Files.createDirectories(Paths.get(backupPath));

            Path productsSource = Paths.get(PRODUCTS_FILE);
            Path categoriesSource = Paths.get(CATEGORIES_FILE);

            if (Files.exists(productsSource) && Files.exists(categoriesSource)) {
           
                Path productsTarget = Paths.get(backupPath + "/products.csv");
                Path categoriesTarget = Paths.get(backupPath + "/categories.csv");

                Files.copy(productsSource, productsTarget, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(categoriesSource, categoriesTarget, StandardCopyOption.REPLACE_EXISTING);

                
            } else {
                System.out.println("Nevar izveidot rezerves kopiju - nav atrasti avota faili");
            }
        } catch (IOException e) {
            System.err.println("Kļūda veidojot rezerves kopiju: " + e.getMessage());
        }
    }

    public static void restoreFromLatestBackup() {
        try {
            Path backupDir = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupDir)) {            System.out.println("⚠ Nav atrasta neviena rezerves kopija");
            return;
        }

   
        String latestBackup = Files.list(backupDir)
            .filter(Files::isDirectory)
            .map(Path::getFileName)
            .map(Path::toString)
            .sorted()
            .reduce((first, second) -> second)
            .orElse(null);

        if (latestBackup == null) {
            System.out.println("⚠ Nav atrasta neviena rezerves kopija");
                return;
            }

            String backupPath = BACKUP_DIR + "/" + latestBackup;
            Path productsSource = Paths.get(backupPath + "/products.csv");
            Path categoriesSource = Paths.get(backupPath + "/categories.csv");

            if (Files.exists(productsSource) && Files.exists(categoriesSource)) {
                Files.copy(productsSource, Paths.get(PRODUCTS_FILE), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(categoriesSource, Paths.get(CATEGORIES_FILE), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Dati veiksmīgi atjaunoti no rezerves kopijas: " + backupPath);
            } else {
                System.out.println("⚠ Rezerves kopijas faili nav atrasti: " + backupPath);
            }
        } catch (IOException e) {
            System.err.println("⚠ Kļūda atjaunojot no rezerves kopijas: " + e.getMessage());
        }
    }

    public static void cleanupOldBackups(int keepCount) {
        try {
            Path backupDir = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupDir)) {
                return;
            }

            
            List<Path> backups = Files.list(backupDir)
                .filter(Files::isDirectory)
                .sorted()
                .collect(java.util.stream.Collectors.toList());

         
            if (backups.size() > keepCount) {
                for (int i = 0; i < backups.size() - keepCount; i++) {
                    deleteDirectory(backups.get(i));
                }
            }
        } catch (IOException e) {
            System.err.println("Kļūda dzēšot vecās rezerves kopijas: " + e.getMessage());
        }
    }

    private static void deleteDirectory(Path directory) throws IOException {
        Files.walk(directory)
            .sorted(java.util.Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    System.err.println("Neizdevās izdzēst: " + path);
                }
            });
    }
}