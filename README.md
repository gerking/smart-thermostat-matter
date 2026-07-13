# Smart Radiator Thermostat – Matter/Thread Demo (CodeQL Static Analysis)

This small Java project simulates a smart radiator thermostat connected to a
**Matter** network (the smart-home application layer protocol) over the
**Thread** wireless standard. It serves as a demo codebase for practicing and
demonstrating static analysis with **CodeQL**.

The code is written to look like a realistic IoT device (control logic,
commissioning, network join, telemetry persistence, firmware updates, remote
access) while deliberately containing several **typical vulnerability
classes** that CodeQL reliably detects.

## Project structure

```
src/main/java/com/example/thermostat/
├── ThermostatDevice.java          Core logic (valve position, target temperature) – no vulnerabilities
├── ThermostatMain.java            Entry point, wires the components together
├── matter/
│   └── MatterCommissioning.java   Matter commissioning simulation
├── thread/
│   └── ThreadNetworkManager.java  Thread network join
├── persistence/
│   ├── DatabaseConfig.java        DB credentials
│   └── TemperatureHistoryRepository.java  Temperature history (JDBC)
├── firmware/
│   └── FirmwareUpdateService.java Firmware upload & extraction
├── remote/
│   ├── RemoteAccessService.java   Cloud connection, remote commands
│   └── CommandExecutor.java       Diagnostic commands
├── util/
│   └── CryptoUtil.java            Checksums & encryption
└── config/
    └── ConfigLoader.java          Cloud API configuration
```

## Intentionally included vulnerabilities

Every finding is marked in the code with a `VULN (CWE-...)` comment.

| File | Vulnerability | CWE | Expected CodeQL query (Java) |
|---|---|---|---|
| `MatterCommissioning.java` | Hardcoded Matter setup passcode | CWE-798 | `java/hardcoded-credentials` |
| `MatterCommissioning.java` | Insecure randomness (`java.util.Random`) | CWE-338 | `java/insecure-randomness` |
| `ThreadNetworkManager.java` | Hardcoded Thread network key | CWE-798 | `java/hardcoded-credentials` |
| `ThreadNetworkManager.java` | Key logged in plaintext | CWE-532 | `java/sensitive-log-exposure` (or a custom query) |
| `DatabaseConfig.java` | Hardcoded DB credentials | CWE-798 | `java/hardcoded-credentials` |
| `TemperatureHistoryRepository.java` | SQL injection via string concatenation | CWE-89 | `java/sql-injection` |
| `FirmwareUpdateService.java` | Path traversal on firmware upload | CWE-22 | `java/path-injection` |
| `FirmwareUpdateService.java` | Command injection while extracting | CWE-78 | `java/command-line-injection` |
| `RemoteAccessService.java` | Disabled certificate validation (trust-all) | CWE-295 | `java/insecure-trustmanager` |
| `RemoteAccessService.java` | Unsafe deserialization | CWE-502 | `java/unsafe-deserialization` |
| `CommandExecutor.java` | Command injection in diagnostic commands | CWE-78 | `java/command-line-injection` |
| `CryptoUtil.java` | MD5 used for integrity checking | CWE-327 | `java/weak-cryptographic-algorithm` |
| `CryptoUtil.java` | DES encryption | CWE-327 | `java/weak-cryptographic-algorithm` |
| `ConfigLoader.java` | Hardcoded cloud API key | CWE-798 | `java/hardcoded-credentials` |

Exact query IDs may vary slightly depending on the CodeQL version/query
pack; the table is meant as a guide to what a standard analysis with the
`java-security-and-quality` suite should surface.

## Prerequisites

- JDK 17+ (only JDK built-ins are used, **no** external Maven dependencies
  required – the project also compiles offline)
- [CodeQL CLI](https://github.com/github/codeql-cli-binaries) or the CodeQL
  extension for VS Code
- optional: Maven, if you'd rather build via `pom.xml`

## Creating and analyzing a CodeQL database

### Option A: with `javac` (no Maven/internet required)

```bash
# 1. Create the CodeQL database (build tracing via javac)
codeql database create thermostat-db \
  --language=java \
  --command="javac -d out $(find src -name '*.java')"

# 2. Run the analysis with the standard security query pack
codeql database analyze thermostat-db \
  java-security-and-quality.qls \
  --format=sarif-latest \
  --output=results.sarif

# 3. Review the results (e.g. in VS Code with the CodeQL extension,
#    or with a SARIF viewer)
```

### Option B: with Maven

```bash
codeql database create thermostat-db --language=java --command="mvn -q clean compile"
codeql database analyze thermostat-db java-security-and-quality.qls \
  --format=sarif-latest --output=results.sarif
```

## Educational note

This project is intended **exclusively for teaching/demonstration purposes**.
It deliberately shows insecure patterns to illustrate how CodeQL detects
them – it should not be used as a template for real production software. A
production firmware/cloud project would need, among other things, secrets
loaded from a secret store, parameterized SQL queries, validated/whitelisted
input, modern cryptography (e.g. AES-GCM, SHA-256/SHA-3), and properly
implemented certificate validation.
