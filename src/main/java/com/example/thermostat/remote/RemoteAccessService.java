package com.example.thermostat.remote;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Handles the thermostat's connection to the vendor backend / cloud.
 */
public class RemoteAccessService {

    /**
     * Builds an SSLContext for the cloud connection.
     *
     * VULN (CWE-295, Improper Certificate Validation): certificate validation is completely
     * bypassed by a TrustManager that trusts everything (a classic "trust-all" trap, e.g.
     * added for debugging and never removed).
     */
    public SSLContext createCloudSslContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // no-op: accepts any client certificate
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // no-op: accepts any server certificate
                }
            }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        return sc;
    }

    /**
     * Receives a command object sent by the cloud backend.
     *
     * VULN (CWE-502, Deserialization of Untrusted Data): data coming from a network
     * connection is deserialized directly with ObjectInputStream, without any validation
     * or class whitelisting.
     */
    public Object receiveRemoteCommand(InputStream networkStream) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(networkStream)) {
            return ois.readObject();
        }
    }
}
