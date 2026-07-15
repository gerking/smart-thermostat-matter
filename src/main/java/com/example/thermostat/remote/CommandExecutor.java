package com.example.thermostat.remote;

public class CommandExecutor {

    /**
     * Runs a diagnostic command triggered from the support portal.
     *
     * FIX (was CWE-78, OS Command Injection): Runtime.exec(String) only ever splits on
     * whitespace itself (no shell involved), so the real issue here was argument
     * injection - a diagName containing spaces could smuggle in extra, unintended
     * arguments/flags for diag.sh. ProcessBuilder passes diagName as its own argv
     * element, so diag.sh always receives exactly one argument, regardless of content.
     */
    public String runDiagnostic(String diagName) throws Exception {
        Process p = new ProcessBuilder("/opt/thermostat/diag.sh", diagName).start();
        p.waitFor();
        return "Diagnostic '" + diagName + "' executed, exit code " + p.exitValue();
    }
}
