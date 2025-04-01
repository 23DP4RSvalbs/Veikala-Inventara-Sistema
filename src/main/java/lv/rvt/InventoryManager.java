package lv.rvt;

import java.util.ArrayList;
import java.util.List;
import lv.rvt.tools.Helper;

public class InventoryManager {
    private List<Product> products;
    private List<Category> categories;
    private int nextId;

    public InventoryManager() {
        products = new ArrayList<>();
        categories = new ArrayList<>();
        nextId = 1;
    }


    public void addProduct(String name, String category, double price, int quantity) {


        if (!Helper.validateProductName(name) || !Helper.validatePrice(price) || !Helper.validateQuantity(quantity)) {
            System.out.println("Nederīgi produkta dati!");
            return;
        }


        Product product = new Product(nextId++, name, category, price, quantity);
        products.add(product);
        System.out.println("Produkts pievienots:" + product);
    }

    public void showAllProducts() {

    
        if (products.isEmpty()) {
            System.out.println("Nav produktu!");
        } 
        
        else {
            for (Product product : products) {
                System.out.println(product);

                
            }
        }


    }
}