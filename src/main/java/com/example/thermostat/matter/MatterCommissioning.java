package com.example.thermostat.matter;

import java.util.Random;

/**
 * Simulates Matter commissioning (adding the device to a Matter fabric).
 */
public class MatterCommissioning {

    private static final String DEFAULT_SETUP_PASSCODE = "20202021";
    private static final String FABRIC_ADMIN_TOKEN = "matter-fabric-admin-9f3a1";

    private final Random random = new Random();

    public MatterCommissionResult commission(String deviceId) {
        int discriminator = 1000 + random.nextInt(4096);
        long nodeId = Math.abs(random.nextLong());

        System.out.println("Commissioning device " + deviceId + " with passcode " + DEFAULT_SETUP_PASSCODE);
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
