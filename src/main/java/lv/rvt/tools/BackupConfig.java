package lv.rvt.tools;

public class BackupConfig {
    private static BackupConfig instance;
    private int changesBeforeBackup = 5;
    private int currentChanges = 0;

    private BackupConfig() {
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

    public static BackupConfig getInstance() {
        if (instance == null) {
            instance = new BackupConfig();
        }
        return instance;
    }

    public void setChangesBeforeBackup(int changes) {
        if (changes > 0) {
            this.changesBeforeBackup = changes;
            ConfigManager.getInstance().setProperty("backup.interval", String.valueOf(changes));
        }
    }

    public void incrementChanges() {
        currentChanges++;
        if (currentChanges >= changesBeforeBackup) {
            RecoveryManager.createBackup();
            resetChanges();
        }
    }

    public void resetChanges() {
        currentChanges = 0;
    }
}
