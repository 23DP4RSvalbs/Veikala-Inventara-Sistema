package lv.rvt.tools;

import java.util.*;

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
            config.setProperty("app.language", currentLanguage);
        }
        loadMessages();
    }

    public static MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    private void loadMessages() {
        try {
            messages = ResourceBundle.getBundle(BASE_NAME, Locale.of(currentLanguage));
        } catch (MissingResourceException e) {
            System.err.println("Failed to load messages for language: " + currentLanguage);
            // Fallback to Latvian
            currentLanguage = "lv";
            messages = ResourceBundle.getBundle(BASE_NAME, Locale.of("lv"));
        }
    }

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
            ConfigManager.getInstance().setProperty("app.language", language);
            loadMessages();
            
            // Notify UI components of language change
            notifyLanguageChange();
        }
    }

    private void notifyLanguageChange() {
        // Save current language preference to config
        ConfigManager.getInstance().setProperty("app.language", currentLanguage);
        
        // Reload messages
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

    public String formatMessage(String key, Object... args) {
        try {
            return String.format(getString(key), args);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }
}