package lv.rvt;

import lv.rvt.tools.Helper;

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
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Produkta nosaukums nevar būt tukšs");
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
        if (price < 0) {
            throw new IllegalArgumentException("Cena nevar būt negatīva");
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Daudzums nevar būt negatīvs");
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
        if (obj == this) return true;
        if (!(obj instanceof Product)) return false;
        Product other = (Product) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}