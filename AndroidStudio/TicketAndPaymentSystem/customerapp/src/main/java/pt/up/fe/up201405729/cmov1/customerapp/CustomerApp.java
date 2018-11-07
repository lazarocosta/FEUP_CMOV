package pt.up.fe.up201405729.cmov1.customerapp;

import android.app.Application;
import android.content.Context;

import pt.up.fe.up201405729.cmov1.restservices.EncryptionManager;

public class CustomerApp extends Application {
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
