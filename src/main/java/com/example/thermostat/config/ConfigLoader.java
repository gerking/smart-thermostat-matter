package com.example.thermostat.config;

public class ConfigLoader {

    // VULN (CWE-798, Hardcoded Credentials): the API key for the cloud sync service is
    // hardcoded in source instead of being loaded from a secret store.
    public static final String CLOUD_API_KEY = "sk_live_51Hxyz0000THERMOAPIKEY9999";
    public static final String CLOUD_SYNC_ENDPOINT = "https://cloud.thermostat-vendor.example/api/v1/sync";
}
