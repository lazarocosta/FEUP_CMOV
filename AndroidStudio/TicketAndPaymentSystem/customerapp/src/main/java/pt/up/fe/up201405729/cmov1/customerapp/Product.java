package pt.up.fe.up201405729.cmov1.customerapp;

public class Product {
    private String name;
    private int price;
    private int quantity;

    public Product(String name, int price){
        this.name = name;
        this.price = price;
        this.quantity = 1;
    }
    public Product(String name, int price, int quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
