package pt.up.fe.up201405729.cmov1.sharedlibrary;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String SHA256(String data) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha256.digest(data.getBytes());
        return bytesToHex(hash);
    }

    // Based on https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
