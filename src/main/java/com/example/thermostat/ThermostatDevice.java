package com.example.thermostat;

/**
 * Represents a smart radiator thermostat.
 * Contains the actual control logic (valve position based on target/current temperature).
 */
public class ThermostatDevice {

    private final String deviceId;
    private double currentTemperatureCelsius;
    private double targetTemperatureCelsius;
    private int valvePositionPercent; // 0-100
    private boolean windowOpenDetected;

    public ThermostatDevice(String deviceId, double currentTemperatureCelsius, double targetTemperatureCelsius) {
        this.deviceId = deviceId;
        this.currentTemperatureCelsius = currentTemperatureCelsius;
        this.targetTemperatureCelsius = targetTemperatureCelsius;
        this.valvePositionPercent = 0;
    }

    public void updateCurrentTemperature(double measuredCelsius) {
        this.currentTemperatureCelsius = measuredCelsius;
        recalculateValvePosition();
    }

    public void setTargetTemperature(double targetCelsius) {
        if (targetCelsius < 5 || targetCelsius > 30) {
            throw new IllegalArgumentException("Target temperature out of allowed range (5-30°C)");
        }
        this.targetTemperatureCelsius = targetCelsius;
        recalculateValvePosition();
    }

    public void setWindowOpenDetected(boolean open) {
        this.windowOpenDetected = open;
        recalculateValvePosition();
    }

    private void recalculateValvePosition() {
        if (windowOpenDetected) {
            valvePositionPercent = 0; // frost protection / energy saving while a window is open
            return;
        }
        double delta = targetTemperatureCelsius - currentTemperatureCelsius;
        if (delta <= 0) {
            valvePositionPercent = 0;
        } else if (delta >= 3) {
            valvePositionPercent = 100;
        } else {
            valvePositionPercent = (int) Math.round((delta / 3.0) * 100);
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public double getCurrentTemperatureCelsius() {
        return currentTemperatureCelsius;
    }

    public double getTargetTemperatureCelsius() {
        return targetTemperatureCelsius;
    }

    public int getValvePositionPercent() {
        return valvePositionPercent;
    }
}
