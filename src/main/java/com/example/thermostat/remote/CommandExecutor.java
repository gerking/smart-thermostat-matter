package com.example.thermostat.remote;

public class CommandExecutor {

    /**
     * Runs a diagnostic command triggered from the support portal.
     *
     * VULN (CWE-78, OS Command Injection): diagName originates from a remote-control
     * request and is passed to Runtime.exec() without validation.
     */
    public String runDiagnostic(String diagName) throws Exception {
        Process p = Runtime.getRuntime().exec("/opt/thermostat/diag.sh " + diagName);
        p.waitFor();
        return "Diagnostic '" + diagName + "' executed, exit code " + p.exitValue();
    }
}
