package lv.rvt.tools;

// Klase rezerves kopiju konfigurācijas pārvaldībai
public class BackupConfig {
    private static BackupConfig instance;
    private int changesBeforeBackup = 5;
    private int currentChanges = 0;
    private BackupConfig() {
        // Ielādē saglabāto intervālu no konfigurācijas
        String savedInterval = ConfigManager.getInstance().getProperty("backup.interval");
        if (savedInterval != null) {
            try {
                int interval = Integer.parseInt(savedInterval);
                if (interval > 0) {
                    this.changesBeforeBackup = interval;
                }
            } catch (NumberFormatException e) {
                ConfigManager.getInstance().setProperty("backup.interval", String.valueOf(changesBeforeBackup));
            }
        } else {
            ConfigManager.getInstance().setProperty("backup.interval", String.valueOf(changesBeforeBackup));
        }
    }

    // Atgriež vienīgo instances eksemplāru
    public static BackupConfig getInstance() {
        if (instance == null) {
            instance = new BackupConfig();
        }
        return instance;
    }

    // Iestata izmaiņu skaitu pirms rezerves kopijas izveides
    public void setChangesBeforeBackup(int changes) {
        if (changes > 0) {
            this.changesBeforeBackup = changes;
            ConfigManager.getInstance().setProperty("backup.interval", String.valueOf(changes));
            // Atiestata izmaiņu skaitītāju, kad tiek mainīts intervāls
            resetChanges();
            System.out.println("✓ Rezerves kopiju intervāls nomainīts uz " + changes + " izmaiņām");
            System.out.println("✓ Izmaiņu skaitītājs atiestatīts uz 0");
        }
    }

    // Palielina izmaiņu skaitītāju un veido rezerves kopiju, ja nepieciešams
    public void incrementChanges() {
        currentChanges++;
        if (currentChanges >= changesBeforeBackup) {
            RecoveryManager.createBackup();
            resetChanges();
        }
    }

    // Atiestata izmaiņu skaitītāju
    public void resetChanges() {
        currentChanges = 0;
    }
}
