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


    public static boolean validateSearchId(String idInput) {
        return idInput != null && idInput.matches("^[0-9]+$");
    }


    public static boolean validateSearchName(String name) {
        return name != null && name.length() >= 2 && !name.equals("_") && name.matches("^[a-zA-Z_]+$");
    }


    public static boolean validateSearchCategory(String category) {
        return category != null && category.length() >= 2 && !category.equals("_") && category.matches("^[a-zA-Z_]+$");
    }


    public static boolean validateSearchPrice(String priceInput) {
        try {
            Double.parseDouble(priceInput);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean validateSearchQuantity(String quantityInput) {
        try {
            Integer.parseInt(quantityInput);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    
    public static boolean validatePriceRange(String minInput, String maxInput) {
        try {
            double min = Double.parseDouble(minInput);
            double max = Double.parseDouble(maxInput);
            return min >= 0 && max >= min;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    
    public static boolean validateQuantityRange(String minInput, String maxInput) {
        try {
            int min = Integer.parseInt(minInput);
            int max = Integer.parseInt(maxInput);
            return min >= 0 && max >= min;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}