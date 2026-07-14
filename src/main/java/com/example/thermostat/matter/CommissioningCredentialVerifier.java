package com.example.thermostat.matter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * During Matter commissioning, checks whether the joining device knows its individual,
 * factory-provisioned device secret: the device encrypts its secret with the shared
 * fabric join key, the verifier decrypts it and compares it against the expected value
 * it stores itself.
 */
public class CommissioningCredentialVerifier {

    private final byte[] fabricJoinKey;
    private final ProvisionedSecretStore secretStore;

    public CommissioningCredentialVerifier(byte[] fabricJoinKey, ProvisionedSecretStore secretStore) {
        this.fabricJoinKey = fabricJoinKey;
        this.secretStore = secretStore;
    }

    public boolean verify(Credentials c) {
        byte[] expectedSecret = secretStore.lookupExpectedSecret(c.getDeviceId());
        if (expectedSecret == null) {
            return false;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(fabricJoinKey, "AES"));
            byte[] decryptedSecret = cipher.doFinal(c.getEncryptedDeviceSecret());
            return Arrays.equals(decryptedSecret, expectedSecret);
        } catch (Exception e) {
            return false;
        }
    }
}
