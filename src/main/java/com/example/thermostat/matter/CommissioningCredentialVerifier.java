package com.example.thermostat.matter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * During Matter commissioning, checks whether the joining device knows its individual,
 * factory-provisioned device secret: the device encrypts its secret with the shared
 * fabric join key, the verifier decrypts it and compares it against the expected value
 * it stores itself.
 *
 * Simplified, Matter-inspired teaching example: the real Matter protocol instead
 * verifies possession of the passcode via PASE/SPAKE2+, a password-authenticated key
 * exchange in which the expected value never travels over the wire as a standalone
 * data value. For this exercise, the simpler "decrypt an encrypted secret and compare"
 * pattern is enough to demonstrate the actual teaching point (cipher mode, separating
 * input from expectation).
 *
 * Naming note: fabricJoinKey is deliberately NOT called "deviceSecret" - this key
 * actually is shared fabric-wide (one value for every device in a fabric), unlike the
 * individual device secret in Credentials. That's itself a separate, discussion-worthy
 * weak point (missing key diversification), but a different one from unclear naming.
 */
public class CommissioningCredentialVerifier {

    private final byte[] fabricJoinKey;
    private final ProvisionedSecretStore secretStore;

    public CommissioningCredentialVerifier(byte[] fabricJoinKey, ProvisionedSecretStore secretStore) {
        this.fabricJoinKey = fabricJoinKey;
        this.secretStore = secretStore;
    }

    /**
     * VULN (CWE-327, Use of a Broken or Risky Cryptographic Algorithm):
     * Cipher.getInstance("AES") is called without an explicit mode/padding
     * specification. Java then defaults to ECB mode. ECB maps identical plaintext
     * blocks to identical ciphertext blocks, exposing patterns in the encrypted secret
     * and enabling attacks such as block replay. The correct choice would be, e.g.,
     * "AES/GCM/NoPadding", which additionally ensures integrity.
     */
    public boolean verify(Credentials c) {
        byte[] expectedSecret = secretStore.lookupExpectedSecret(c.getDeviceId());
        if (expectedSecret == null) {
            return false;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES"); // VULN: no mode specified -> ECB
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(fabricJoinKey, "AES"));
            byte[] decryptedSecret = cipher.doFinal(c.getEncryptedDeviceSecret());
            return Arrays.equals(decryptedSecret, expectedSecret);
        } catch (Exception e) {
            return false;
        }
    }
}
