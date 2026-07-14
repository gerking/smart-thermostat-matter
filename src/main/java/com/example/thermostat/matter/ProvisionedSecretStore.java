package com.example.thermostat.matter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the individual, factory-provisioned device secrets that commissioning
 * checks against. Each device has its own secret - nothing is shared between multiple
 * devices. The expected value comes exclusively from here - never from the caller of
 * {@link CommissioningCredentialVerifier#verify(Credentials)}.
 */
public class ProvisionedSecretStore {

    private final Map<String, byte[]> provisionedSecrets = new ConcurrentHashMap<>();

    public void provision(String deviceId, byte[] expectedDeviceSecret) {
        provisionedSecrets.put(deviceId, expectedDeviceSecret);
    }

    public byte[] lookupExpectedSecret(String deviceId) {
        return provisionedSecrets.get(deviceId);
    }
}
