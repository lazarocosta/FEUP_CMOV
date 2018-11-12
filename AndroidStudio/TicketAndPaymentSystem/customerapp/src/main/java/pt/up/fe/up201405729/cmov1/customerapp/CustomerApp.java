package pt.up.fe.up201405729.cmov1.customerapp;

import android.app.Application;
import android.content.Context;

import pt.up.fe.up201405729.cmov1.restservices.EncryptionManager;

public class CustomerApp extends Application {
    public static final String sharedPreferencesKeyName = "pt.up.fe.up201405729.cmov1.customerapp.prefs";
    public static final String performancesRVAdapterKeyName = "pt.up.fe.up201405729.cmov1.customerapp.performancesRVAdapter";
    public static final String qrCodeContentKeyName = "pt.up.fe.up201405729.cmov1.customerapp.qrCodeContent";
    public static final String checkoutDataKeyName = "pt.up.fe.up201405729.cmov1.customerapp.CheckoutActivity.checkoutData";
    public static final String selectProductsActivityRVAdapterKeyName = "pt.up.fe.up201405729.cmov1.customerapp.cafeteria.selectProductsActivityRVAdapter";
    public static final String cafeteriaSelectedProductsKeyName = "pt.up.fe.up201405729.cmov1.customerapp.cafeteria.cafeteriaSelectedProductsKeyName";
    public static final String addVouchersActivityRVAdapterKeyName = "pt.up.fe.up201405729.cmov1.customerapp.cafeteria.addVouchersActivityRVAdapterKeyName";
    public static final String vouchersFilename = "pt.up.fe.up201405729.cmov1.customerapp.vouchersFile";
    public static final String ticketsFilename = "pt.up.fe.up201405729.cmov1.customerapp.ticketsFile";
    private EncryptionManager encryptionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Context packageContext = this;
        encryptionManager = new EncryptionManager(packageContext);
    }

    public EncryptionManager getEncryptionManager() {
        return encryptionManager;
    }
}
