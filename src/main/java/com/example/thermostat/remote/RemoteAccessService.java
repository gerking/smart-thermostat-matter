package com.example.thermostat.remote;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.io.ObjectInputFilter;
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
     * FIX (was CWE-295, Improper Certificate Validation): the custom "trust-all"
     * TrustManager was removed. {@code null} for the trust managers tells the JVM to
     * use its built-in default trust manager (system trust store).
     */
    public SSLContext createCloudSslContext() throws Exception {
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, null, new SecureRandom());
        return sc;
    }

    /**
     * Receives a command object sent by the cloud backend.
     *
     * FIX (was CWE-502, Deserialization of Untrusted Data): a JDK ObjectInputFilter
     * (JEP 290) now restricts deserialization: max nesting depth 5, max array length
     * 100, java.lang.String explicitly allowed, everything else rejected ("!*") before
     * readObject() would instantiate it. In a real protocol, the allow-list would name
     * the concrete command/DTO classes instead of String.
     */
    public Object receiveRemoteCommand(InputStream networkStream) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(networkStream)) {
            ois.setObjectInputFilter(
                    ObjectInputFilter.Config.createFilter("maxdepth=5;maxarray=100;java.lang.String;!*"));
            return ois.readObject();
        }
    }
}
