package com.example.thermostat.persistence;

public class DatabaseConfig {

    // VULN (CWE-798, Hardcoded Credentials): database credentials hardcoded in source.
    public static final String JDBC_URL = "jdbc:mysql://cloud.thermostat-vendor.example:3306/telemetry";
    public static final String DB_USER = "thermostat_service";
    // FIX (was CWE-798, Hardcoded Credentials): value now comes from an environment
    // variable instead of being a literal in source.
    public static final String DB_PASSWORD = System.getenv("THERMOSTAT_DB_PASSWORD");
}
