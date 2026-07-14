# Smart Radiator Thermostat – Matter/Thread Demo (CodeQL Static Analysis)

This small Java project simulates a smart radiator thermostat connected to a
**Matter** network (the smart-home application layer protocol) over the
**Thread** wireless standard. It serves as an exercise codebase for applying
static analysis with **CodeQL**.

## Project structure

```
src/main/java/com/example/thermostat/
├── ThermostatDevice.java          Core logic (valve position, target temperature)
├── ThermostatMain.java            Entry point, wires the components together
├── matter/
│   ├── MatterCommissioning.java   Matter commissioning simulation
│   ├── Credentials.java           Commissioning data sent by the device
│   ├── ProvisionedSecretStore.java  Factory-provisioned device secrets
│   └── CommissioningCredentialVerifier.java  Checks the device secret (verify(...))
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

## Task

The project contains several security vulnerabilities of the kind commonly
found in IoT/smart-home software. Analyze the code with CodeQL and identify:

- Where in the code the vulnerabilities are
- Which vulnerability class (CWE) each one belongs to
- Which CodeQL quer(y/ies) produce each finding
- How the code could be fixed in each case

## Prerequisites

- JDK 17+ (only JDK built-ins are used, **no** external Maven dependencies
  required – the project also compiles offline)
- [CodeQL CLI](https://github.com/github/codeql-cli-binaries) or the CodeQL
  extension for VS Code
- optional: Maven, if you'd rather build via `pom.xml`

## Creating a CodeQL database

```bash
codeql database create thermostat-db \
  --language=java \
  --command="javac -d out $(find src -name '*.java')"
```

## Analyzing with the CodeQL standard suite

```bash
codeql database analyze thermostat-db \
  java-security-and-quality.qls \
  --format=sarif-latest \
  --output=results.sarif
```

Results are easiest to review in VS Code with the CodeQL extension (select
the database, then open the SARIF file or run the suite directly from the
extension). For taint-tracking findings, the extension shows the complete
data flow path from source to sink.

## Note

Not every vulnerability class is automatically found by the standard suite
- some cases need a matching, custom-written query. For cases the standard
suite doesn't find, think about what CodeQL query would be needed to catch
them.
