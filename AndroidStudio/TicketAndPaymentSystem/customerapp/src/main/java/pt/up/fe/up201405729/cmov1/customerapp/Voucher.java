package pt.up.fe.up201405729.cmov1.customerapp;

import android.support.annotation.NonNull;
import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

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

    public Voucher(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject voucher = jsonObject.getJSONObject("Voucher");
            this.uuid = voucher.getString("uuid");
            this.productCode = ProductCode.Popcorn; // TODO: voucher.get("productCode");
            this.state = State.notUsed; // TODO: voucher.get("state");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            //TODO: voucher.put("productCode", productCode);
            //TODO: voucher.put("state", state);
            jsonObject.put("Voucher", voucher);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
