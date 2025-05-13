package lv.rvt;

import lv.rvt.tools.Helper;
import java.util.Objects;

// Klase, kas reprezentē produktu veikala inventāra sistēmā
public class Product {
    private final int id;
    private String name;
    private String category;
    private double price;
    private int quantity;

    // Konstruktors jauna produkta izveidei
    public Product(int id, String name, String category, double price, int quantity) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID nevar būt mazāks vai vienāds ar 0");
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

    // Iestata produkta nosaukumu, pārbaudot tā derīgumu
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
            throw new IllegalArgumentException("Nederīga kategorija");
        }
        this.category = category;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (!Helper.validatePrice(price)) {
            throw new IllegalArgumentException("Nederīga cena");
        }
        this.price = price;
    }

 
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (!Helper.validateQuantity(quantity)) {
            throw new IllegalArgumentException("Nederīgs daudzums");
        }
        this.quantity = quantity;
    }

    // Pārraksta toString metodi, lai attēlotu produkta informāciju
    @Override
    public String toString() {
        return String.format("ID: %d, Nosaukums: %s, Kategorija: %s, Cena: %.2f€, Daudzums: %d",
                id, name, category, price, quantity);
    }

    // Pārraksta equals metodi, lai salīdzinātu produktus
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product other = (Product) obj;
        return id == other.id;
    }

    // Pārraksta hashCode metodi, lai nodrošinātu korektu darbību ar HashSet un HashMap
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}