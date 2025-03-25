package lv.rvt;

import java.util.Scanner;

public class UserInterface {
    private InventoryManager manager;
    private Scanner scanner;

    public UserInterface() {
        manager = new InventoryManager();
        scanner = new Scanner(System.in);
    }
}