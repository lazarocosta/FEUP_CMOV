package pt.up.fe.up201405729.cmov1.customerapp;

import java.io.Serializable;

public class Voucher implements Serializable {
    private enum ProductCode {FreeCoffee, Popcorn, Discount}
    private String uuid;
    private ProductCode productCode;

    public Voucher(String uuid, String productCode) {
        ProductCode myProductCode;
        if(productCode.equals("freecoffee"))
            myProductCode = ProductCode.FreeCoffee;
        else if (productCode.equals("popcorn"))
            myProductCode = ProductCode.Popcorn;
        else if (productCode.equals("5%discountCafeteria"))
            myProductCode = ProductCode.Discount;
        else
            throw new IllegalArgumentException("Invalid product code: " + productCode);
        this.uuid = uuid;
        this.productCode = myProductCode;
    }

    public String getUuid() {
        return uuid;
    }

    public ProductCode getProductCode() {
        return productCode;
    }
}
