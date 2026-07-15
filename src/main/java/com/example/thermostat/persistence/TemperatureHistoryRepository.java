package com.example.thermostat.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Stores and reads back a thermostat's temperature history from a central
 * telemetry database.
 */
public class TemperatureHistoryRepository {

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
    }

    /**
     * Prints the temperature history for a given device.
     *
     * FIX (was CWE-89, SQL Injection): deviceId is now passed as a bound parameter via
     * PreparedStatement instead of being concatenated into the query string. The
     * database always treats the value as pure data, never as SQL syntax.
     */
    public void printHistoryForDevice(String deviceId) throws SQLException {
        String query = "SELECT ts, temperature FROM temperature_history WHERE device_id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, deviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getTimestamp("ts") + " -> " + rs.getDouble("temperature") + "°C");
                }
            }
        }
    }
}
