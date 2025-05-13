package lv.rvt.tools;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

// Klase datu atjaunošanas un rezerves kopiju pārvaldībai
public class RecoveryManager {
    private static final String DATA_DIR = "data";
    private static final String BACKUP_DIR = DATA_DIR + "/backup";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.csv";
    private static final String CATEGORIES_FILE = DATA_DIR + "/categories.csv";

    // Galvenā metode rezerves kopiju tīrīšanai
    public static void main(String[] args) {
        if (args.length == 2 && args[0].equals("clean")) {
            try {
                int keepCount = Integer.parseInt(args[1]);
                cleanupOldBackups(keepCount);
                System.out.println("Backup cleanup complete - keeping " + keepCount + " most recent backups");
            } catch (NumberFormatException e) {
                System.err.println("Error: second argument must be a number");
            }
        } else {
            System.out.println("Usage: RecoveryManager clean <number_of_backups_to_keep>");
        }
    }

    // Izveido jaunu rezerves kopiju ar pašreizējo datumu un laiku
    public static void createBackup() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Path backupPath = Paths.get(BACKUP_DIR, timestamp);

        try {
            Files.createDirectories(backupPath);
            Files.copy(Paths.get(PRODUCTS_FILE), backupPath.resolve("products.csv"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Paths.get(CATEGORIES_FILE), backupPath.resolve("categories.csv"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("⚠ Neizdevās izveidot rezerves kopiju: " + e.getMessage());
        }
    }

    // Atjauno datus no pēdējās rezerves kopijas
    public static void restoreFromLatestBackup() {
        try {
            Path backupDir = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupDir)) {
                System.out.println("⚠ Nav atrasta neviena rezerves kopija");
                return;
            }

            // Atrod pēdējo rezerves kopiju
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

            Path backupPath = backupDir.resolve(latestBackup);
            Path productsBackup = backupPath.resolve("products.csv");
            Path categoriesBackup = backupPath.resolve("categories.csv");

            if (Files.exists(productsBackup) && Files.exists(categoriesBackup)) {
                // Kopē failus no rezerves kopijas
                Files.copy(productsBackup, Paths.get(PRODUCTS_FILE), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(categoriesBackup, Paths.get(CATEGORIES_FILE), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Dati veiksmīgi atjaunoti no rezerves kopijas: " + backupPath);
            } else {
                System.out.println("⚠ Rezerves kopijas faili nav atrasti: " + backupPath);
            }
        } catch (IOException e) {
            System.err.println("⚠ Kļūda atjaunojot no rezerves kopijas: " + e.getMessage());
        }
    }

    // Notīra vecās rezerves kopijas, atstājot norādīto skaitu jaunāko kopiju
    public static void cleanupOldBackups(int keepCount) {
        if (keepCount < 1) {
            throw new IllegalArgumentException("Must keep at least 1 backup");
        }

        try {
            Path backupDir = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupDir)) {
                return;
            }

            // Sakārto kopijas pēc datuma un dzēš vecākās
            List<Path> backups = Files.list(backupDir)
                .filter(Files::isDirectory)
                .sorted((a, b) -> b.getFileName().toString().compareTo(a.getFileName().toString()))
                .collect(Collectors.toList());

            if (backups.size() <= keepCount) {
                return;
            }

            for (int i = keepCount; i < backups.size(); i++) {
                deleteDirectory(backups.get(i));
            }
        } catch (IOException e) {
            throw new RuntimeException("⚠ Neizdevās notīrīt vecās rezerves kopijas: " + e.getMessage());
        }
    }

    // Rekursīvi dzēš direktoriju un tās saturu
    private static void deleteDirectory(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}