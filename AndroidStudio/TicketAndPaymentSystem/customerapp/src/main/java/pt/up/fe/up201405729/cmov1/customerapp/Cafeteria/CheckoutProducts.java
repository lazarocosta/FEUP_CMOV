package pt.up.fe.up201405729.cmov1.customerapp.Cafeteria;

import java.io.Serializable;
import java.util.ArrayList;

import pt.up.fe.up201405729.cmov1.customerapp.Product;

class CheckoutProducts implements Serializable {
    private ArrayList<Product> products;

    CheckoutProducts(ArrayList<Product> products) {
        this.products = products;
    }

    ArrayList<Product> getProducts() {
        return products;
    }
}
