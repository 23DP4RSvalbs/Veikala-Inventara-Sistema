package lv.rvt.tools;

public class Helper {

    public static boolean validateProductName(String name) {
        return name != null && !name.trim().isEmpty() && !name.equals("_") && name.matches("^[a-zA-Z_]+$");
    }

    public static boolean validateCategory(String category) {
        return category != null && !category.trim().isEmpty() && !category.equals("_") && category.matches("^[a-zA-Z_]+$");
    }

    public static boolean validatePrice(String priceInput) {
        try {
            double price = Double.parseDouble(priceInput);
            return price >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean validateQuantity(String quantityInput) {
        try {
            int quantity = Integer.parseInt(quantityInput);
            return quantity >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}