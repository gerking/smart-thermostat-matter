package com.example.thermostat.config;

public class ConfigLoader {

    // FIX (was CWE-798, Hardcoded Credentials): value now comes from an environment
    // variable instead of being a literal in source.
    public static final String CLOUD_API_KEY = System.getenv("THERMOSTAT_CLOUD_API_KEY");
    public static final String CLOUD_SYNC_ENDPOINT = "https://cloud.thermostat-vendor.example/api/v1/sync";
}
