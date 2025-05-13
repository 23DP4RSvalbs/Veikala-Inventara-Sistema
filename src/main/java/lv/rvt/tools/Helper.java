package lv.rvt.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.regex.Pattern;

public class Helper {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z_]+$");
    private static final Pattern VALID_NUMBER_PATTERN = Pattern.compile("^-?\\d*\\.?\\d+$");
    public static final double MAX_PRICE = 1000000.0;
    public static final int MAX_QUANTITY = 1000000;
    private static final String STANDARD_NUMBER_FORMAT_ERROR = "⚠ Cenai un Daudzumam jābūt formātā: 00000000.00";

    public static boolean validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        return VALID_NAME_PATTERN.matcher(category.trim()).matches();
    }

    public static boolean validateProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return VALID_NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean validatePrice(double price) {
        return price >= 0 && price <= MAX_PRICE;
    }

    public static boolean validateQuantity(int quantity) {
        return quantity >= 0 && quantity <= MAX_QUANTITY;
    }

    public static boolean validateProductData(String name, String category, double price, int quantity) {
        return validateProductName(name) &&
               validateCategory(category) &&
               validatePrice(price) &&
               validateQuantity(quantity);
    }

    public static boolean validateInput(String input) {
        return input != null && !input.trim().isEmpty() && VALID_NAME_PATTERN.matcher(input).matches();
    }

    public static boolean validateNumericInput(String input) {
        return input != null && !input.trim().isEmpty() && VALID_NUMBER_PATTERN.matcher(input).matches();
    }

    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[^a-zA-Z0-9_]", "_");
    }

    public static String getStandardNumberFormatError() {
        return STANDARD_NUMBER_FORMAT_ERROR;
    }

    public static class ValidationResult {
        private List<String> errors;

        public ValidationResult() {
            this.errors = new ArrayList<>();
        }

        public void addError(String error) {
            errors.add(error);
        }

        public void addErrors(Collection<String> newErrors) {
            errors.addAll(newErrors);
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public int getErrorCount() {
            return errors.size();
        }

        public void clear() {
            errors.clear();
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
                errors.size(), 
                String.join("\n", errors));
        }
    }

    public static ValidationResult validateCsvProductLine(String[] parts) {
        ValidationResult result = new ValidationResult();

        // Check basic format
        if (parts == null || parts.length != 5) {
            result.addError("Nepareizs kolonnu skaits. Vajadzīgs: ID,Nosaukums,Kategorija,Cena,Daudzums");
            return result;
        }

        // Validate ID
        try {
            int id = Integer.parseInt(parts[0].trim());
            if (id <= 0) {
                result.addError("ID jābūt pozitīvam skaitlim: " + parts[0]);
            }
        } catch (NumberFormatException e) {
            result.addError("Nederīgs ID formāts: " + parts[0]);
        }

        // Validate name
        if (!validateProductName(parts[1])) {
            result.addError("Nederīgs produkta nosaukums: " + parts[1] + 
                " (atļauti tikai burti, cipari un _)");
        }

        // Validate category
        if (!validateCategory(parts[2])) {
            result.addError("Nederīgs kategorijas nosaukums: " + parts[2] + 
                " (atļauti tikai burti, cipari un _)");
        }

        // Validate price
        try {
            double price = Double.parseDouble(parts[3].trim());
            if (!validatePrice(price)) {
                result.addError("Nederīga cena: " + parts[3] + 
                    " (jābūt starp 0 un " + MAX_PRICE + ")");
            }
        } catch (NumberFormatException e) {
            result.addError(getStandardNumberFormatError());
        }

        // Validate quantity
        try {
            int quantity = Integer.parseInt(parts[4].trim());
            if (!validateQuantity(quantity)) {
                result.addError("Nederīgs daudzums: " + parts[4] + 
                    " (jābūt starp 0 un " + MAX_QUANTITY + ")");
            }
        } catch (NumberFormatException e) {
            result.addError(getStandardNumberFormatError());
        }

        return result;
    }

    public static ValidationResult validateCsvCategoryLine(String category) {
        ValidationResult result = new ValidationResult();
        
        if (!validateCategory(category)) {
            result.addError("Nederīgs kategorijas formāts: " + category + 
                " (atļauti tikai burti, cipari un _)");
        }
        
        return result;
    }

    public static String sanitizeString(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[^a-zA-Z0-9_]", "_");
    }
}