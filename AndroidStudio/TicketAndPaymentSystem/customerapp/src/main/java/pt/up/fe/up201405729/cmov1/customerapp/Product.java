package pt.up.fe.up201405729.cmov1.customerapp;

import java.io.Serializable;

public class Product implements Serializable {
    private String uuid;
    private String name;
    private double price;
    private int quantity;

    public Product(String uuid, String name, double price, int quantity){
        this.uuid = uuid;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(String uuid, String name, double price){
        this.uuid = uuid;
        this.name = name;
        this.price = price;
        this.quantity = 0;
    }

    public String getUuid() {
        return uuid;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity should be zero or higher.");
        this.quantity = quantity;
    }
}
