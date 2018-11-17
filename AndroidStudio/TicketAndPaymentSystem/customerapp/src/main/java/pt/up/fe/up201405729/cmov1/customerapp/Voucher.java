package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Voucher implements Serializable {
    public enum ProductCode {FreeCoffee, Popcorn, Discount}
    public enum State {used, notUsed}
    private String uuid;
    private ProductCode productCode;
    private State state;
    private String productCodeString;

    public Voucher(String uuid, String productCode, String state) {
        this.uuid = uuid;
        this.productCode = parseProductCode(productCode);
        this.state = parseState(state);
    }

    public Voucher(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject voucher = jsonObject.getJSONObject("Voucher");
            this.uuid = voucher.getString("uuid");
            this.productCode = parseProductCode(voucher.getString("productCode"));
            this.state = parseState(voucher.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ProductCode parseProductCode(String productCode) {
        ProductCode myProductCode;
        switch (productCode) {
            case "freecoffee":
                productCodeString = "free coffee";
                myProductCode = ProductCode.FreeCoffee;
                break;
            case "popcorn":
                productCodeString = "popcorn";
                myProductCode = ProductCode.Popcorn;
                break;
            case "5%discountCafeteria":
                productCodeString = "5 discount Cafeteria";
                myProductCode = ProductCode.Discount;
                break;
            default:
                throw new IllegalArgumentException("Invalid product code: " + productCode);
        }
        return myProductCode;
    }

    private State parseState(String state) {
        State myState;
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
        return myState;
    }

    public String getProductCodeString() {
        return productCodeString;
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

    @NonNull
    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject voucher = new JSONObject();
            voucher.put("uuid", uuid);
            voucher.put("productCode", parseProductCode(productCode));
            voucher.put("state", parseState(state));
            jsonObject.put("Voucher", voucher);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String parseProductCode(ProductCode productCode) {
        String productCodeStr;
        if (productCode.equals(ProductCode.FreeCoffee)) {
            productCodeString = "free coffee";
            productCodeStr = "freecoffee";
        } else if (productCode.equals(ProductCode.Popcorn)) {
            productCodeString = "popcorn";
            productCodeStr = "popcorn";
        } else if (productCode.equals(ProductCode.Discount)) {
            productCodeString = "5 discount Cafeteria";
            productCodeStr = "5%discountCafeteria";
        } else
            throw new IllegalArgumentException("Invalid product code: " + productCode);
        return productCodeStr;
    }

    private String parseState(State state) {
        String stateStr;
        if (state.equals(State.used))
            stateStr = "used";
        else if (state.equals(State.notUsed))
            stateStr = "not used";
        else
            throw new IllegalArgumentException("Invalid state: " + state);
        return stateStr;
    }
}
