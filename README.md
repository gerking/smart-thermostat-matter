# Smart Radiator Thermostat – Matter/Thread Demo (CodeQL Static Analysis)

This small Java project simulates a smart radiator thermostat connected to a
**Matter** network (the smart-home application layer protocol) over the
**Thread** wireless standard. It serves as a demo codebase for practicing and
demonstrating static analysis with **CodeQL**.

The code is written to look like a realistic IoT device (control logic,
commissioning, network join, telemetry persistence, firmware updates, remote
access) while deliberately containing several **typical vulnerability
classes**.

## Project structure

```
src/main/java/com/example/thermostat/
├── ThermostatDevice.java          Core logic (valve position, target temperature) – no vulnerabilities
├── ThermostatMain.java            Entry point, wires the components together
├── matter/
│   ├── MatterCommissioning.java   Matter commissioning simulation
│   ├── Credentials.java           Encrypted shared-secret data sent by the device
│   ├── ProvisionedSecretStore.java  Factory-provisioned expected secrets per device
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

codeql-custom-queries-java/         Custom CodeQL query pack (see below)
```

## Intentionally included vulnerabilities

Every finding is marked in the code with a `VULN (CWE-...)` comment.

| File | Vulnerability | CWE | Matching query |
|---|---|---|---|
| `MatterCommissioning.java` | Hardcoded Matter setup passcode | CWE-798 | `java/hardcoded-credentials` (built-in) and `demo/hardcoded-secret-field` |
| `MatterCommissioning.java` | Insecure randomness (`java.util.Random`) | CWE-338 | `demo/insecure-random-for-security` |
| `ThreadNetworkManager.java` | Hardcoded Thread network key | CWE-798 | `demo/hardcoded-secret-field` |
| `ThreadNetworkManager.java` | Key logged in plaintext | CWE-532 | `demo/sensitive-data-logged` |
| `DatabaseConfig.java` | Hardcoded DB credentials | CWE-798 | `java/hardcoded-credentials` (built-in) |
| `TemperatureHistoryRepository.java` | SQL injection via string concatenation | CWE-89 | `demo/sql-injection-from-parameter` |
| `FirmwareUpdateService.java` | Path traversal on firmware upload | CWE-22 | `demo/path-injection-from-parameter` |
| `FirmwareUpdateService.java` | Command injection while extracting | CWE-78 | `demo/command-injection-from-parameter` |
| `RemoteAccessService.java` | Disabled certificate validation (trust-all) | CWE-295 | `demo/trust-all-manager` |
| `RemoteAccessService.java` | Unsafe deserialization | CWE-502 | `demo/unsafe-deserialization` |
| `CommandExecutor.java` | Command injection in diagnostic commands | CWE-78 | `demo/command-injection-from-parameter` |
| `CryptoUtil.java` | MD5 used for integrity checking | CWE-327 | `java/weak-cryptographic-algorithm` (built-in) |
| `CryptoUtil.java` | DES encryption | CWE-327 | `java/weak-cryptographic-algorithm` (built-in) |
| `ConfigLoader.java` | Hardcoded cloud API key | CWE-798 | `demo/hardcoded-secret-field` |
| `CommissioningCredentialVerifier.java` | `Cipher.getInstance("AES")` without a mode → implicit ECB mode | CWE-327 | `java/weak-cryptographic-algorithm` (built-in) |

**On `CommissioningCredentialVerifier.verify(Credentials c)`:** This class
demonstrates a real `verify(...): boolean` design pattern: the device sends
its individual, factory-provisioned device secret encrypted, the verifier decrypts it
and compares it against the expected value it stores itself
(`ProvisionedSecretStore`) - structurally a classic password check with a
decryption step in front of it. Deliberately separated: `Credentials`
carries only what the device sends, never the expected value; that comes
exclusively from `ProvisionedSecretStore`, which the caller of
`verify(...)` cannot influence.

The actual vulnerability isn't an obvious mistake but a quietly-applying
Java default: if `Cipher.getInstance(...)` is called with only the
algorithm (e.g., `"AES"`) and no explicit mode, Java silently defaults to
the insecure ECB mode. This category is already caught by the built-in
standard suite (`java/weak-cryptographic-algorithm`) - the same query that
also produces the MD5/DES findings in `CryptoUtil.java`. So no custom query
is needed for it; this is also a good example of a structural query (no
data flow required) firing regardless of the context the insecure cipher
call sits in.

**Note on realism:** This is a simplified, Matter-inspired teaching
example. Real Matter verifies passcode possession during commissioning via
PASE/SPAKE2+ (a password-authenticated key exchange), in which the
expected value never travels over the wire as a standalone data value -
the separation of input and expectation is anchored structurally in the
protocol itself, not just in the code. For the CodeQL exercise, the
simpler pattern here is sufficient.

## Two kinds of CodeQL queries – and why the standard suite alone isn't enough

CodeQL's standard security query suite (`java-security-and-quality.qls`)
contains two kinds of security queries:

1. **Purely structural checks** (no data flow needed) – e.g. weak crypto
   algorithms (MD5/DES) or hardcoded credentials that reach a known sink
   like `DriverManager.getConnection(...)` directly. These fire reliably
   regardless of the rest of the project.
2. **Taint-tracking queries** (SQL injection, command injection, path
   traversal, unsafe deserialization) – these only report a finding when a
   complete data flow can be proven from a **recognized** remote source
   (`RemoteFlowSource`, e.g. `HttpServletRequest.getParameter`) to the sink.
   Since this demo project doesn't contain a real web framework, the
   standard suite finds nothing here — not because the code is safe, but
   because CodeQL can't prove the parameters come from outside.

**Instead** of artificially adding an HTTP layer to the project,
`codeql-custom-queries-java/` contains a **custom query pack** tailored
specifically to this project:

- For the taint-tracking cases (SQL/command/path injection), **every String
  parameter of every method** is treated as potentially untrusted — not
  just recognized web-request sources. That's the right heuristic for a
  small library/demo project without a real entry point.
- For hardcoded secrets, logged secrets, insecure randomness, unsafe
  deserialization, and trust-all trust managers, there are purely
  structural queries (no data flow needed) tailored directly to the
  naming/code patterns used in the project.

| Query file | Detects | Genericity |
|---|---|---|
| `HardcodedSecretField.ql` | hardcoded secrets in `static final String` fields | generic (keywords like PASSWORD/KEY/TOKEN, no project names) |
| `InsecureRandomForSecurity.ql` | every use of `java.util.Random` in the project | generic, but low precision (many false positives possible) |
| `SensitiveDataLogged.ql` | logging calls that print a secret field in cleartext | generic (keywords) |
| `SqlInjectionFromParameter.ql` | String parameters flowing into `Statement.executeQuery(...)` | generic (real taint tracking, sink-based) |
| `CommandInjectionFromParameter.ql` | String parameters flowing into `Runtime.exec(...)`/`ProcessBuilder` | generic (real taint tracking, sink-based) |
| `PathInjectionFromParameter.ql` | String parameters flowing into `new File(...)` | generic (real taint tracking, sink-based) |
| `UnsafeDeserialization.ql` | every call to `ObjectInputStream.readObject()` | generic (purely structural, JDK type) |
| `TrustAllManager.ql` | `X509TrustManager` implementations without a validating `checkServerTrusted` | generic (purely structural, JDK type) |

**Note on genericity:** An earlier version of `InsecureRandomForSecurity.ql`
additionally filtered on class names like `Commission`/`Pairing` — in truth
that was a disguised description of `MatterCommissioning` rather than a
general heuristic. That filter has been removed; the query is now generic
but less precise (it flags every `Random` instantiation, not just
security-relevant ones). The keyword-based queries
(`HardcodedSecretField.ql`, `SensitiveDataLogged.ql`) deliberately use
generic terms (PASSWORD, KEY, TOKEN, SECRET, ...) instead of concrete field
names from this project — the same kind of heuristic real tools like
gitleaks or detect-secrets use, with the same limitations (e.g. a database
column called `PRIMARY_KEY` could be falsely flagged).


## Prerequisites

- JDK 17+ (only JDK built-ins are used, **no** external Maven dependencies
  required – the project also compiles offline)
- [CodeQL CLI](https://github.com/github/codeql-cli-binaries) or the CodeQL
  extension for VS Code
- internet access once, for `codeql pack install` (downloads the
  `codeql/java-all` dependency for the custom query pack)
- optional: Maven, if you'd rather build via `pom.xml`

## Creating a CodeQL database

```bash
codeql database create thermostat-db \
  --language=java \
  --command="javac -d out $(find src -name '*.java')"
```

## Running the custom queries (recommended for this project)

```bash
# One-time: resolve the query pack's dependency
cd codeql-custom-queries-java
codeql pack install
cd ..

# Run all custom queries as a suite
codeql database analyze thermostat-db \
  codeql-custom-queries-java/demo-suite.qls \
  --format=sarif-latest \
  --output=results-demo.sarif

# Alternatively, run a single query for a focused demo, e.g.
codeql database analyze thermostat-db \
  codeql-custom-queries-java/SqlInjectionFromParameter.ql \
  --format=csv --output=sql-injection.csv
column -s, -t sql-injection.csv | less -S
```

For the `@kind path-problem` queries (SQL/command/path injection), viewing
results in VS Code with the CodeQL extension is worthwhile: it visualizes
the complete data flow path from parameter to sink.

## Additionally: standard suite for the structural findings

```bash
codeql database analyze thermostat-db \
  java-security-and-quality.qls \
  --format=sarif-latest \
  --output=results-standard.sarif
```

Here you should see the MD5/DES findings, the disabled certificate
validation, and the hardcoded DB credentials — i.e. the "3 hits" from
before. Combined with the custom suite above, this covers all the
intentionally built-in vulnerabilities.

## Educational note

This project is intended **exclusively for teaching/demonstration purposes**.
It deliberately shows insecure patterns to illustrate how CodeQL detects
them – it should not be used as a template for real production software. A
production firmware/cloud project would need, among other things, secrets
loaded from a secret store, parameterized SQL queries, validated/whitelisted
input, modern cryptography (e.g. AES-GCM, SHA-256/SHA-3), and properly
implemented certificate validation.

The custom queries in `codeql-custom-queries-java/` are deliberately broad
(e.g. "every String parameter is a potential source"). That's reasonable
for a small, controlled demo project, but would produce many false
positives in a large real codebase — there, the built-in,
RemoteFlowSource-based queries are the right choice.
