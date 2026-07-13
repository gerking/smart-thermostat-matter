package com.example.thermostat;

import com.example.thermostat.matter.MatterCommissioning;
import com.example.thermostat.thread.ThreadNetworkManager;

public class ThermostatMain {

    public static void main(String[] args) throws Exception {
        ThermostatDevice device = new ThermostatDevice("radiator-living-room-01", 18.5, 21.0);
        device.updateCurrentTemperature(19.2);
        System.out.println("Valve position: " + device.getValvePositionPercent() + "%");

        MatterCommissioning commissioning = new MatterCommissioning();
        MatterCommissioning.MatterCommissionResult result = commissioning.commission(device.getDeviceId());
        System.out.println("Matter node ID: " + result.nodeId);

        ThreadNetworkManager threadManager = new ThreadNetworkManager();
        threadManager.joinNetwork(device.getDeviceId());

        // The remaining components (persistence, firmware update, remote access) live in the
        // persistence/, firmware/ and remote/ packages and would be wired together via a
        // service/DI layer in a real application. For the CodeQL demo it's enough that they
        // are part of the compiled codebase.
    }
}
