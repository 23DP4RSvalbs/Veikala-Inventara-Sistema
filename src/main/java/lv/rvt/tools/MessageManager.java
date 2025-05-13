package lv.rvt.tools;

import java.util.*;

// Klase valodu un ziņojumu pārvaldīšanai
public class MessageManager {
    private static MessageManager instance;
    private ResourceBundle messages;
    private String currentLanguage;
    private static final String BASE_NAME = "messages";

    private MessageManager() {
        ConfigManager config = ConfigManager.getInstance();
        currentLanguage = config.getProperty("app.language");
        if (currentLanguage == null) {
            currentLanguage = "lv";
        }
        loadMessages();
    }

    // Atgriež vienīgo MessageManager instanci
    public static MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    // Ielādē ziņojumus atbilstoši izvēlētajai valodai
    private void loadMessages() {
        try {
            messages = ResourceBundle.getBundle(BASE_NAME, Locale.of(currentLanguage));
        } catch (MissingResourceException e) {
            System.err.println("Nevar atrast ziņojumu failu valodai: " + currentLanguage);
            messages = ResourceBundle.getBundle(BASE_NAME, Locale.of("lv"));
        }
    }

    // Atgriež ziņojumu pēc atslēgas
    public String getString(String key) {
        try {
            return messages.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public void setLanguage(String language) {
        if (!language.equals(currentLanguage)) {
            currentLanguage = language;
            notifyLanguageChange();
        }
    }

    private void notifyLanguageChange() {
        ConfigManager.getInstance().setProperty("app.language", currentLanguage);
        

        loadMessages();
    }

    public boolean isLanguageSupported(String language) {
        try {
            ResourceBundle.getBundle(BASE_NAME, Locale.of(language));
            return true;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    // Formatē ziņojumu ar parametriem
    public String formatMessage(String key, Object... args) {
        try {
            return String.format(getString(key), args);
        } catch (Exception e) {
            return key;
        }
    }
}