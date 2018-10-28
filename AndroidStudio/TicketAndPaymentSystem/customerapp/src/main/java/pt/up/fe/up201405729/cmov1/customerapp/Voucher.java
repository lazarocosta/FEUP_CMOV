package pt.up.fe.up201405729.cmov1.customerapp;

public class Voucher {
    private enum ProductCode {FreeCoffee, Popcorn, Discount}
    private ProductCode productCode;
    private String uuid;

    public Voucher(ProductCode productCode, String uuid) {
        this.productCode = productCode;
        this.uuid = uuid;
    }

    public ProductCode getProductCode() {
        return productCode;
    }

    public String getUuid() {
        return uuid;
    }
}
