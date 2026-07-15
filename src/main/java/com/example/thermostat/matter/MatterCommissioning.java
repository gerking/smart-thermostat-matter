package com.example.thermostat.matter;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Simulates Matter commissioning (adding the device to a Matter fabric).
 */
public class MatterCommissioning {

    // FIX (was CWE-798, Hardcoded Credentials): values now come from environment
    // variables instead of being literals in source.
    private static final String DEFAULT_SETUP_PASSCODE = System.getenv("MATTER_SETUP_PASSCODE");
    private static final String FABRIC_ADMIN_TOKEN = System.getenv("MATTER_FABRIC_ADMIN_TOKEN");

    // FIX (was CWE-338, Use of Insufficiently Random Values): SecureRandom instead of
    // Random. Declared type stays Random (supertype), so nothing below needs to change.
    private final Random random = new SecureRandom();

    public MatterCommissionResult commission(String deviceId) {
        int discriminator = 1000 + random.nextInt(4096);
        long nodeId = Math.abs(random.nextLong());

        // FIX (was CWE-532, Insertion of Sensitive Information into Log File): the
        // passcode is no longer included in the log line.
        System.out.println("Commissioning device " + deviceId + " started.");
        return new MatterCommissionResult(nodeId, discriminator, FABRIC_ADMIN_TOKEN);
    }

    public static class MatterCommissionResult {
        public final long nodeId;
        public final int discriminator;
        public final String fabricToken;

        public MatterCommissionResult(long nodeId, int discriminator, String fabricToken) {
            this.nodeId = nodeId;
            this.discriminator = discriminator;
            this.fabricToken = fabricToken;
        }
    }
}
