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
     */
    public SSLContext createCloudSslContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        return sc;
    }

    /**
     * Receives a command object sent by the cloud backend.
     */
    public Object receiveRemoteCommand(InputStream networkStream) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(networkStream)) {
            return ois.readObject();
        }
    }
}
