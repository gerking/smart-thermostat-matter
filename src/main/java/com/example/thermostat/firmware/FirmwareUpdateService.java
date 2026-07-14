package com.example.thermostat.firmware;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FirmwareUpdateService {

    private static final String FIRMWARE_DIR = "/var/lib/thermostat/firmware/";

    /**
     * Stores an uploaded firmware image under the file name supplied by the client.
     */
    public void storeUploadedFirmware(String fileName, InputStream data) throws Exception {
        File target = new File(FIRMWARE_DIR + fileName);
        try (FileOutputStream out = new FileOutputStream(target)) {
            data.transferTo(out);
        }
    }

    /**
     * Extracts a previously uploaded firmware archive.
     */
    public void extractFirmwareArchive(String archiveName) throws Exception {
        String command = "tar -xzf " + FIRMWARE_DIR + archiveName + " -C " + FIRMWARE_DIR;
        Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
    }
}
