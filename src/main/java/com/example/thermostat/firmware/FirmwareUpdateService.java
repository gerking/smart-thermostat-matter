package com.example.thermostat.firmware;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FirmwareUpdateService {

    private static final String FIRMWARE_DIR = "/var/lib/thermostat/firmware/";

    /**
     * Stores an uploaded firmware image under the file name supplied by the client.
     *
     * FIX (was CWE-22, Path Traversal): the target path is resolved to its canonical
     * form (resolving ".." segments and symlinks, not just textually) and then checked
     * to still lie within FIRMWARE_DIR. Unlike a name-pattern allow-list, this can't be
     * bypassed via symlink tricks or unexpected path encodings, since the check happens
     * on the fully resolved path.
     */
    public void storeUploadedFirmware(String fileName, InputStream data) throws Exception {
        File target = new File(FIRMWARE_DIR + fileName).getCanonicalFile();
        String allowedRoot = new File(FIRMWARE_DIR).getCanonicalPath() + File.separator;
        if (!target.getPath().startsWith(allowedRoot)) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        try (FileOutputStream out = new FileOutputStream(target)) {
            data.transferTo(out);
        }
    }

    /**
     * Extracts a previously uploaded firmware archive.
     *
     * FIX (was CWE-78, OS Command Injection): No shell wrapper anymore
     * ("/bin/sh -c" is gone). ProcessBuilder passes archiveName as its own,
     * atomic argv element to "tar" - there is no shell interpreter left that
     * could read metacharacters in it as command separators.
     */
    public void extractFirmwareArchive(String archiveName) throws Exception {
        new ProcessBuilder("tar", "-xzf", FIRMWARE_DIR + archiveName, "-C", FIRMWARE_DIR).start();
    }
}
