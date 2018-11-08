package pt.up.fe.up201405729.cmov1.customerapp;

import java.io.Serializable;

public class Voucher implements Serializable {
    public enum ProductCode {FreeCoffee, Popcorn, Discount}
    public enum State {used, notUsed}
    private String uuid;
    private ProductCode productCode;
    private State state;

    public Voucher(String uuid, String productCode, String state) {
        ProductCode myProductCode;
        State myState;
        switch (productCode) {
            case "freecoffee":
                myProductCode = ProductCode.FreeCoffee;
                break;
            case "popcorn":
                myProductCode = ProductCode.Popcorn;
                break;
            case "5%discountCafeteria":
                myProductCode = ProductCode.Discount;
                break;
            default:
                throw new IllegalArgumentException("Invalid product code: " + productCode);
        }
        switch (state) {
            case "used":
                myState = State.used;
                break;
            case "not used":
                myState = State.notUsed;
                break;
            default:
                throw new IllegalArgumentException("Invalid state: " + state);
        }
        this.uuid = uuid;
        this.productCode = myProductCode;
        this.state = myState;
    }

    public String getUuid() {
        return uuid;
    }

    public ProductCode getProductCode() {
        return productCode;
    }

    public String productCodeToString() {
        return this.productCode.toString();
    }

    public State getState() {
        return state;
    }
}
