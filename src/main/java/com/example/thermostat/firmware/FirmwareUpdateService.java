package com.example.thermostat.firmware;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FirmwareUpdateService {

    private static final String FIRMWARE_DIR = "/var/lib/thermostat/firmware/";

    /**
     * Stores an uploaded firmware image under the file name supplied by the client.
     *
     * VULN (CWE-22, Path Traversal): fileName is concatenated with the base directory
     * without validation. A value like "../../etc/cron.d/evil" would write outside FIRMWARE_DIR.
     */
    public void storeUploadedFirmware(String fileName, InputStream data) throws Exception {
        File target = new File(FIRMWARE_DIR + fileName);
        try (FileOutputStream out = new FileOutputStream(target)) {
            data.transferTo(out);
        }
    }

    /**
     * Extracts a previously uploaded firmware archive.
     *
     * VULN (CWE-78, OS Command Injection): archiveName is inserted directly into a shell
     * call without escaping or validation.
     */
    public void extractFirmwareArchive(String archiveName) throws Exception {
        String command = "tar -xzf " + FIRMWARE_DIR + archiveName + " -C " + FIRMWARE_DIR;
        Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
    }
}
