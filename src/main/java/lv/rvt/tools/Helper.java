package lv.rvt.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.regex.Pattern;

// Palīgklase dažādu validācijas un palīgfunkciju nodrošināšanai
public class Helper {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z_]+$");
    private static final Pattern VALID_NUMBER_PATTERN = Pattern.compile("^-?\\d*\\.?\\d+$");
    public static final double MAX_PRICE = 1000000.0;
    public static final int MAX_QUANTITY = 1000000;
    private static final String STANDARD_NUMBER_FORMAT_ERROR = "⚠ Cenai un Daudzumam jābūt formātā: 00000000.00";
    public static String getStandardNumberFormatError() {
        return STANDARD_NUMBER_FORMAT_ERROR;
    }

    // Pārbauda, vai ievadītā virkne atbilst derīga vārda formātam
    public static boolean validateString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return VALID_NAME_PATTERN.matcher(input.trim()).matches();
    }

    public static boolean validateCategory(String category) {
        return validateString(category);
    }

    public static boolean validateProductName(String name) {
        return validateString(name);
    }

    public static boolean validatePrice(double price) {
        return price >= 0 && price <= MAX_PRICE;
    }

    public static boolean validateQuantity(int quantity) {
        return quantity >= 0 && quantity <= MAX_QUANTITY;
    }

    // Pārbauda visus produkta datus vienlaicīgi
    public static boolean validateProductData(String name, String category, double price, int quantity) {
        return validateProductName(name) &&
               validateCategory(category) &&
               validatePrice(price) &&
               validateQuantity(quantity);
    }

    // Pārbauda, vai ievadītā virkne ir derīgs skaitlis
    public static boolean validateNumericInput(String input) {
        return validateString(input) && VALID_NUMBER_PATTERN.matcher(input).matches();
    }

    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[^a-zA-Z0-9_]", "_");
    }

    // Iekšējā klase validācijas rezultātu apstrādei
    public static class ValidationResult {
        private List<String> errors = new ArrayList<>();

        public void addError(String error) {
            errors.add(error);
        }

        public void addErrors(Collection<String> newErrors) {
            errors.addAll(newErrors);
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        // Pārbauda, vai validācijas rezultāts ir derīgs
        public boolean isValid() {
            return errors.isEmpty();
        }

        // Atgriež validācijas kļūdu skaitu
        public int getErrorCount() {
            return errors.size();
        }
        public boolean containsError(String errorText) {
            return errors.stream().anyMatch(err -> err.contains(errorText));
        }

        @Override
        public String toString() {
            if (isValid()) {
                return "Validācija veiksmīga";
            }
            return String.format("Validācijas kļūdas (%d):%n%s", 
                errors.size(), String.join("\n", errors));
        }
    }

    // Validē CSV faila produkta rindu
    public static ValidationResult validateCsvProductLine(String[] parts) {
        ValidationResult result = new ValidationResult();

        // Pārbauda lauku skaitu
        if (parts == null || parts.length != 5) {
            result.addError("Nepareizs kolonnu skaits. Vajadzīgs: ID,Nosaukums,Kategorija,Cena,Daudzums");
            return result;
        }

        try {
            // Validē ID lauku
            int id = Integer.parseInt(parts[0].trim());
            if (id <= 0) {
                result.addError("ID jābūt pozitīvam skaitlim: " + parts[0]);
            }
        } catch (NumberFormatException e) {
            result.addError("Nederīgs ID formāts: " + parts[0]);
        }

        // Validē produkta nosaukuma lauku
        if (!validateProductName(parts[1])) {
            result.addError("Nederīgs produkta nosaukums: " + parts[1] + " (atļauti tikai burti, cipari un _)");
        }

        // Validē kategorijas lauku
        if (!validateCategory(parts[2])) {
            result.addError("Nederīgs kategorijas nosaukums: " + parts[2] + " (atļauti tikai burti, cipari un _)");
        }

        try {
            // Validē cenas lauku
            double price = Double.parseDouble(parts[3].trim());
            if (!validatePrice(price)) {
                result.addError("Nederīga cena: " + parts[3] + " (jābūt starp 0 un " + MAX_PRICE + ")");
            }
        } catch (NumberFormatException e) {
            result.addError(STANDARD_NUMBER_FORMAT_ERROR);
        }

        try {
            // Validē daudzuma lauku
            int quantity = Integer.parseInt(parts[4].trim());
            if (!validateQuantity(quantity)) {
                result.addError("Nederīgs daudzums: " + parts[4] + " (jābūt starp 0 un " + MAX_QUANTITY + ")");
            }
        } catch (NumberFormatException e) {
            result.addError(STANDARD_NUMBER_FORMAT_ERROR);
        }

        return result;
    }

    // Validē CSV faila kategorijas rindu
    public static ValidationResult validateCsvCategoryLine(String category) {
        ValidationResult result = new ValidationResult();
        if (!validateCategory(category)) {
            result.addError("Nederīgs kategorijas formāts: " + category + " (atļauti tikai burti, cipari un _)");
        }
        return result;
    }
}