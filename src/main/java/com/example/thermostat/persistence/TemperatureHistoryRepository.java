package com.example.thermostat.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
     */
    public void printHistoryForDevice(String deviceId) throws SQLException {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String query = "SELECT ts, temperature FROM temperature_history WHERE device_id = '" + deviceId + "'";
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    System.out.println(rs.getTimestamp("ts") + " -> " + rs.getDouble("temperature") + "°C");
                }
            }
        }
    }
}
