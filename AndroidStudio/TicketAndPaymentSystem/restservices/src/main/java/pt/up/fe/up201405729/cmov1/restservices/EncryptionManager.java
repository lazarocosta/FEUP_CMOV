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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class EncryptionManager {
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String keyAlias = "pt.up.fe.up201405729.cmov1.restservices.EncryptionManager";
    private static final int KEY_SIZE = 512;
    private static final String KEY_ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String charsetName = "UTF-8";
    private static final int NUM_KEY_BYTES = KEY_SIZE / Byte.SIZE;
    private static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";
    private KeyStore keyStore;
    private Context context;

    public EncryptionManager(Context context) {
        this.context = context;
        try {
            this.keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            this.keyStore.load(null);
            createNewKeys();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            handleException(e);
        }
    }

    private void createNewKeys() {
        try {
            if (!keyStore.containsAlias(keyAlias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setKeySize(KEY_SIZE)
                        .setAlias(keyAlias)
                        .setSubject(new X500Principal("CN=" + keyAlias + ", O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM, ANDROID_KEYSTORE);
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
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
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
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, null);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
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

    public RSAPublicKey getPublicKey() {
        try {
            return (RSAPublicKey) keyStore.getCertificate(keyAlias).getPublicKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    // Based on https://paginas.fe.up.pt/~apm/CM/docs/MsgSignNFCandQR.zip and adapted for our needs
    public byte[] buildMessage(String message) {
        byte[] messageBytes = message.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(messageBytes.length + NUM_KEY_BYTES);
        byteBuffer.put(messageBytes);
        byte[] signedMessageBytes = byteBuffer.array();
        try {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, null);
            Signature sg = Signature.getInstance(SIGNATURE_ALGORITHM);
            sg.initSign(privateKey);
            sg.update(signedMessageBytes, 0, messageBytes.length);
            sg.sign(signedMessageBytes, messageBytes.length, NUM_KEY_BYTES);
        } catch (Exception e) {
            handleException(e);
        }
        return signedMessageBytes;
    }
    */

    public JSONObject buildSignedJSONObject(JSONObject jsonObject) {
        JSONObject signedJSONObject = new JSONObject();
        try {
            signedJSONObject.put("data", jsonObject);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, null);
            Signature sg = Signature.getInstance(SIGNATURE_ALGORITHM);
            sg.initSign(privateKey);
            sg.update(jsonObject.toString().getBytes());
            byte[] signature = new byte[NUM_KEY_BYTES];
            sg.sign(signature, 0, NUM_KEY_BYTES);
            signedJSONObject.put("signature", signature);
        } catch (JSONException | NoSuchAlgorithmException | UnrecoverableKeyException | SignatureException | InvalidKeyException | KeyStoreException e) {
            handleException(e);
        }
        return signedJSONObject;
    }

    /*
    // Based on https://paginas.fe.up.pt/~apm/CM/docs/MsgSignNFCandQR.zip and adapted for our needs
    public boolean validate(byte[] message, byte[] signature) {
        boolean verified = false;
        try {
            PublicKey publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
            Signature sg = Signature.getInstance(SIGNATURE_ALGORITHM);
            sg.initVerify(publicKey);
            sg.update(message);
            verified = sg.verify(signature);
        } catch (Exception e) {
            handleException(e);
        }
        return verified;
    }
    */

    public boolean validate(JSONObject signedJSONObject) {
        boolean verified = false;
        try {
            PublicKey publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
            Signature sg = Signature.getInstance(SIGNATURE_ALGORITHM);
            sg.initVerify(publicKey);
            sg.update(signedJSONObject.getJSONObject("data").toString().getBytes());
            verified = sg.verify((byte[]) signedJSONObject.get("signature"));
        } catch (NoSuchAlgorithmException | JSONException | InvalidKeyException | KeyStoreException | SignatureException e) {
            handleException(e);
        }
        return verified;
    }
}
