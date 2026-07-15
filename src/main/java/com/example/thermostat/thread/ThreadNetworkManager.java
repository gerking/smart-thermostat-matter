package com.example.thermostat.thread;

import java.util.logging.Logger;

/**
 * Manages the thermostat joining the Thread mesh network.
 */
public class ThreadNetworkManager {

    private static final Logger LOGGER = Logger.getLogger(ThreadNetworkManager.class.getName());

    // FIX (was CWE-798, Hardcoded Credentials): value now comes from an environment
    // variable instead of being a literal in source.
    private static final String THREAD_NETWORK_KEY = System.getenv("THREAD_NETWORK_KEY");
    private static final String THREAD_PAN_ID = "0x1234";

    public void joinNetwork(String deviceId) {
        // FIX (was CWE-532, Insertion of Sensitive Information into Log File): the
        // network key is no longer included in the log line.
        LOGGER.info("Device " + deviceId + " is joining the Thread network. PAN-ID=" + THREAD_PAN_ID);
        // ... actual radio join logic (802.15.4) would go here
    }

    public String getNetworkKey() {
        return THREAD_NETWORK_KEY;
    }
}
