package com.example.thermostat.matter;

/**
 * Carries exactly what the device actually sends over the wire during commissioning:
 * its device ID and its individual, factory-provisioned device secret, encrypted with
 * the fabric join key. Deliberately NO field for the expected value - that must not
 * travel over the same channel as attacker-controlled input.
 *
 * Naming note: deliberately called "deviceSecret" rather than "sharedSecret" - the
 * secret is individual per device (known in advance only between this one device and
 * the server), not "shared" in the sense of multiple people/devices holding the same
 * value (that would be the classic shared-password anti-pattern).
 */
public class Credentials {

    private final String deviceId;
    private final byte[] encryptedDeviceSecret;

    public Credentials(String deviceId, byte[] encryptedDeviceSecret) {
        this.deviceId = deviceId;
        this.encryptedDeviceSecret = encryptedDeviceSecret;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public byte[] getEncryptedDeviceSecret() {
        return encryptedDeviceSecret;
    }
}
