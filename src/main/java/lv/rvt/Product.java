package lv.rvt;

import lv.rvt.tools.Helper;
import java.util.Objects;

public class Product {
    private final int id;
    private String name;
    private String category;
    private double price;
    private int quantity;

    public Product(int id, String name, String category, double price, int quantity) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID jābūt pozitīvam skaitlim");
        }
        
        this.id = id;
        setName(name);
        setCategory(category);
        setPrice(price);
        setQuantity(quantity);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!Helper.validateProductName(name)) {
            throw new IllegalArgumentException("Nederīgs produkta nosaukums");
        }
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (!Helper.validateCategory(category)) {
            throw new IllegalArgumentException("Nederīgs kategorijas formāts");
        }
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (!Helper.validatePrice(price)) {
            throw new IllegalArgumentException("Cena jābūt starp 0 un " + Helper.MAX_PRICE);
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (!Helper.validateQuantity(quantity)) {
            throw new IllegalArgumentException("Daudzums jābūt starp 0 un " + Helper.MAX_QUANTITY);
        }
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %.2f EUR, %d gab.", 
                           id, name, category, price, quantity);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product other) {
            return id == other.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}