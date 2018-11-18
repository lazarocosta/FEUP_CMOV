package pt.up.fe.up201405729.cmov1.customerapp;

import java.io.Serializable;

public class Product implements Serializable {
    public static final Product[] products = new Product[]{
            new Product("Coffee", 0.8, 0),
            new Product("Soda drink", 1.5, 0),
            new Product("Popcorn", 3.0, 0),
            new Product("Sandwich", 1.5, 0)
    };
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public Product(String name, double price){
        this.name = name;
        this.price = price;
        this.quantity = 0;

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

    public void setQuantity(int quantity) throws IllegalAccessException {
        for (Product p : products)
            if (this == p)
                throw new IllegalAccessException("This object should not be changed.");
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity should be zero or higher.");
        this.quantity = quantity;
    }

    public static Product[] getProductsCopy() {
        Product[] newProducts = new Product[products.length];
        for (int i = 0; i < products.length; i++) {
            Product p = products[i];
            newProducts[i] = new Product(p.getName(), p.getPrice(), p.getQuantity());
        }
        return newProducts;
    }
}
