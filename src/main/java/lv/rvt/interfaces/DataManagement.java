package lv.rvt.interfaces;

// Interfeiss datu pārvaldības funkcionalitātes nodrošināšanai
public interface DataManagement {
    void saveData();
    void loadData();
    void importData(String format);
}