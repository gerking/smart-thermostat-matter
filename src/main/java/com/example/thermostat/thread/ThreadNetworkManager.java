package com.example.thermostat.thread;

import java.util.logging.Logger;

/**
 * Manages the thermostat joining the Thread mesh network.
 */
public class ThreadNetworkManager {

    private static final Logger LOGGER = Logger.getLogger(ThreadNetworkManager.class.getName());

    // VULN (CWE-798, Hardcoded Credentials): the Thread network key (master key) is hardcoded
    // in source instead of being provisioned securely.
    private static final String THREAD_NETWORK_KEY = "00112233445566778899aabbccddeeff";
    private static final String THREAD_PAN_ID = "0x1234";

    public void joinNetwork(String deviceId) {
        // VULN (CWE-532, Insertion of Sensitive Information into Log File): the secret
        // network key is logged in plaintext.
        LOGGER.info("Device " + deviceId + " is joining the Thread network. PAN-ID=" + THREAD_PAN_ID
                + " NetworkKey=" + THREAD_NETWORK_KEY);
        // ... actual radio join logic (802.15.4) would go here
    }

    public String getNetworkKey() {
        return THREAD_NETWORK_KEY;
    }
}
