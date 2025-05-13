package lv.rvt;

import lv.rvt.tools.Helper;
import java.util.Objects;

// Klase, kas reprezentē produktu kategoriju veikala inventāra sistēmā
public class Category {
    private String name;

    public Category(String name) {
        setName(name);
    }
    public String getName() {
        return name;
    }

    // Iestata kategorijas nosaukumu, pārbaudot tā derīgumu
    public void setName(String name) {
        if (Helper.validateCategory(name)) {
            this.name = name;
        }
    }

    // Pārraksta toString metodi, lai attēlotu kategorijas nosaukumu
    @Override
    public String toString() {
        return name;
    }

    // Pārraksta equals metodi, lai salīdzinātu kategorijas pēc to nosaukumiem
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Category other) {
            return Objects.equals(name, other.name);
        }
        return false;
    }

    // Pārraksta hashCode metodi, lai nodrošinātu korektu darbību ar HashSet un HashMap
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}