package lv.rvt;

import lv.rvt.tools.RecoveryManager;

public class BackupConfig {
    private static BackupConfig instance;
    private int changesBeforeBackup = 5; // Default to backup every 5 changes
    private int currentChanges = 0;

    private BackupConfig() {}

    public static BackupConfig getInstance() {
        if (instance == null) {
            instance = new BackupConfig();
        }
        return instance;
    }

    public void setChangesBeforeBackup(int changes) {
        if (changes > 0) {
            this.changesBeforeBackup = changes;
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