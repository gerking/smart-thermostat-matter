package com.example.thermostat.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class CryptoUtil {

    /**
     * Computes an "integrity checksum" for a firmware image.
     *
     * VULN (CWE-327, Use of a Broken Cryptographic Algorithm): MD5 is considered
     * cryptographically broken and unsuitable for integrity/security checks.
     */
    public static String firmwareChecksum(byte[] firmwareBytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(firmwareBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Encrypts the Thread network key before sending it to the cloud.
     *
     * VULN (CWE-327, Use of a Broken Cryptographic Algorithm): DES is outdated and considered
     * insecure due to its short key length (56 bits).
     */
    public static byte[] encryptNetworkKey(byte[] keyBytes, byte[] desKeyBytes) throws Exception {
        SecretKeySpec desKey = new SecretKeySpec(desKeyBytes, "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, desKey);
        return cipher.doFinal(keyBytes);
    }
}
