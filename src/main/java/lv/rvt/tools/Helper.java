package lv.rvt.tools;

public class Helper {

    public static boolean validateProductName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public static boolean validatePrice(double price) {
        return price >= 0;
    }

    public static boolean validateQuantity(int quantity) {
        return quantity >= 0;

        
    }
}