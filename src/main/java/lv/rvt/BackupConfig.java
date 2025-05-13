package lv.rvt;

import lv.rvt.tools.RecoveryManager;
import lv.rvt.tools.ConfigManager;

public class BackupConfig {
    private static BackupConfig instance;
    private int changesBeforeBackup = 5;
    private int currentChanges = 0;

    private BackupConfig() {
        // Reset changes counter
        this.currentChanges = 0;
        
        // Load saved backup interval
        String savedInterval = ConfigManager.getInstance().getProperty("backup.interval");
        if (savedInterval != null) {
            try {
                int interval = Integer.parseInt(savedInterval);
                if (interval > 0) {
                    this.changesBeforeBackup = interval;
                }
            } catch (NumberFormatException ignored) {
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

    public int getChangesBeforeBackup() {
        return changesBeforeBackup;
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

    public int getCurrentChanges() {
        return currentChanges;
    }
}