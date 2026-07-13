package com.example.thermostat.persistence;

public class DatabaseConfig {

    // VULN (CWE-798, Hardcoded Credentials): database credentials hardcoded in source.
    public static final String JDBC_URL = "jdbc:mysql://cloud.thermostat-vendor.example:3306/telemetry";
    public static final String DB_USER = "thermostat_service";
    public static final String DB_PASSWORD = "Summer2024!"; 
}
