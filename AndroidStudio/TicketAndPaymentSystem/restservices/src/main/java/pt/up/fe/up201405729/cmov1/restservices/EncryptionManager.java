package pt.up.fe.up201405729.cmov1.restservices;

/*
Based on:
 - https://www.androidauthority.com/use-android-keystore-store-passwords-sensitive-information-623779/
 - https://codedump.io/share/9UiToGn1kp26/1/crash-casting-androidkeystorersaprivatekey-to-rsaprivatekey
*/

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class EncryptionManager {
    private final String keyAlias = "pt.up.fe.up201405729.cmov1.restservices.EncryptionManager";
    private final String charsetName = "UTF-8";
    private KeyStore keyStore;
    private Context context;

    public EncryptionManager(Context context) {
        this.context = context;
        try {
            this.keyStore = KeyStore.getInstance("AndroidKeyStore");
            this.keyStore.load(null);
            createNewKeys();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewKeys() {
        try {
            if (!keyStore.containsAlias(keyAlias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(keyAlias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);
                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public String encryptString(String decryptedString) {
        try {
            PublicKey publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            cipherOutputStream.write(decryptedString.getBytes(charsetName));
            cipherOutputStream.close();
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public String decryptString(String encryptedString) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, null);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(Base64.decode(encryptedString, Base64.DEFAULT)), cipher);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1)
                values.add((byte) nextByte);
            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++)
                bytes[i] = values.get(i);
            return new String(bytes, 0, bytes.length, charsetName);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    private void handleException(Exception e) {
        final String TAG = "handleException()";
        Toast.makeText(context, "Exception " + e.getMessage() + " occurred", Toast.LENGTH_LONG).show();
        Log.e(TAG, Log.getStackTraceString(e));
    }

    public String getPublicKey() {
        try {
            PublicKey publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
            return publicKey.toString();
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
    }
}
