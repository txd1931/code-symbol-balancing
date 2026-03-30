# Java Quickstart

Minimal Java project template using Maven.

Core workflows — build, test, debug, and code quality — are IDE-agnostic and driven entirely from the CLI via the Maven Wrapper. They work the same regardless of which editor you use. [VS Code](https://code.visualstudio.com/) is the recommended editor and is required for workflows that depend on editor-level integration, such as format-on-save and workspace-level Java configuration.

## Philosophy

This template is designed for fast solo project bootstrap while remaining practical for team use.

- **Clone-and-go setup**: the goal is to remove startup friction so a project can begin with working tooling before writing application code.
- **CLI-first workflows**: build, test, debug, and quality checks are executed through Maven Wrapper commands so behavior is consistent across environments.
- **IDE-agnostic core with VS Code recommendation**: the build pipeline does not depend on a specific IDE, while VS Code settings are provided as a practical, real-world default for editor integration.
- **Maven by industry convention**: Maven is used as the standard, predictable project contract in many Java codebases.
- **Quality tools enabled by default**: Checkstyle, Spotless, and SpotBugs are part of the default workflow to provide clear, explicit feedback from the CLI rather than relying on a heavily customized IDE.
- **Lightweight environment by design**: one main advantage of this template is keeping the development environment lean, with sensible defaults and wrapper-based commands instead of a heavy IDE-centric setup.

## Requirements

Required:

- Java SDK (Java 21 recommended)
- `JAVA_HOME` environment variable set to the installed JDK path
- Git

Recommended for editor workflows:

- [VS Code](https://code.visualstudio.com/)
- [Language Support for Java(TM) by Red Hat](https://marketplace.visualstudio.com/items?itemName=redhat.java)
- [Adoptium Temurin](https://adoptium.net/)

Maven is bundled via the wrapper (`mvnw` / `mvnw.cmd`) — no separate Maven installation needed.

## Build

```bash
# Unix
./mvnw package

# Windows
.\mvnw.cmd package
```

Produces `target/java-quickstart-1.0-SNAPSHOT.jar`.

## Run

```bash
java -jar target/java-quickstart-1.0-SNAPSHOT.jar
```

Or run directly without packaging:

```bash
# Unix
./mvnw exec:java

# Windows
.\mvnw.cmd exec:java
```

## Test

```bash
# Unix
./mvnw test

# Windows
.\mvnw.cmd test
```

## Dependency workflow

When adding dependencies, use this flow to keep the build strict and predictable:

1. Add the dependency in `pom.xml` under `<dependencies>`.
2. Prefer BOM-managed versions when available (the template imports `org.junit:junit-bom`).
3. Declare direct usage explicitly (do not rely on transitive dependencies).
4. Run full verification.
5. Check for available updates.

```bash
# Unix
./mvnw verify
./mvnw versions:display-dependency-updates versions:display-plugin-updates

# Windows
.\mvnw.cmd verify
.\mvnw.cmd versions:display-dependency-updates versions:display-plugin-updates
```

`verify` includes strict dependency analysis (`maven-dependency-plugin:analyze-only`) and fails when dependencies are declared-but-unused or used-but-undeclared.

Example from this template:
- Tests compile against `junit-jupiter-api`.
- Test execution uses `junit-jupiter-engine`.
- The dependency analyzer is configured to allow the engine as a reflective runtime dependency.

## Code Quality

All checks run together during `verify`:

```bash
# Unix
./mvnw verify

# Windows
.\mvnw.cmd verify
```

This runs, in order:
- **Dependency analysis** — `maven-dependency-plugin:analyze-only` to enforce explicit and minimal dependencies
- **Checkstyle** — Google Java Style enforcement
- **Spotless** — formatting check against the Eclipse formatter config in `config/formatting/`
- **SpotBugs** — static bug analysis

To auto-fix formatting violations:

```bash
# Unix
./mvnw spotless:apply

# Windows
.\mvnw.cmd spotless:apply
```

## Debug (CLI)

Requires two terminals.

**Terminal 1** — launch the app suspended, waiting for a debugger:

```bash
# Unix
./mvnw -Pdebug compile exec:exec

# Windows
.\mvnw.cmd -Pdebug compile exec:exec
```

**Terminal 2** — attach jdb:

```bash
jdb -connect com.sun.jdi.SocketAttach:hostname=localhost,port=5005
```

Default port is `5005`. Override with `-Ddebug.port=<port>`.

## Java Flight Recorder (JFR)

Run the app with the built-in JFR profiler enabled:

```bash
# Unix
./mvnw -Pjfr compile exec:exec

# Windows
.\mvnw.cmd -Pjfr compile exec:exec
```

Default recording output: `target/flight-recording.jfr`.

For whole-run profiling, customize recording settings at runtime:

```bash
# Windows example
.\mvnw.cmd -Pjfr -Djfr.duration=120s -Djfr.settings=profile -Djfr.outputFile=target/custom.jfr compile exec:exec
```

### Section-level profiling

For precise section boundaries, use the JFR API directly in code.

```java
import java.nio.file.Path;
import jdk.jfr.Configuration;
import jdk.jfr.Recording;

public class ProfilerExample {

  public static void main(String[] args) throws Exception {
    Recording recording = new Recording(Configuration.getConfiguration("profile"));
    recording.setName("section-profile");
    recording.setDumpOnExit(true);

    try {
      recording.start();

      runHeavyAlgorithm();

    } finally {
      recording.stop();
      recording.dump(Path.of("target", "profile.jfr"));
      recording.close();
    }
  }
}
```

This approach records only the interval between `recording.start()` and `recording.stop()`, which is usually better for profiling a specific method or algorithm.

Then inspect the section recording:

```bash
jfr summary target/profile.jfr
jfr print --events ExecutionSample,CPULoad target/profile.jfr
```

Inspect the recording from the CLI:

```bash
jfr summary target/flight-recording.jfr
jfr print --events CPULoad,ExecutionSample target/flight-recording.jfr
```

### Optional GUI analysis with JDK Mission Control

For deeper interactive analysis, open the generated `.jfr` file with [JDK Mission Control (JMC)](https://www.oracle.com/java/technologies/jdk-mission-control.html).

Typical workflow:
- Generate a recording with either `-Pjfr` or the section-level `Recording` API approach.
- Open JMC.
- Load `target/flight-recording.jfr` (or `target/profile.jfr`).
- Use the GUI views for flame graphs, method profiling, allocation analysis, GC pauses, and thread activity.

JMC is optional, but it is often faster than CLI output for root-cause analysis and hotspot exploration.

Notes:
- `jfr.settings=profile` gives higher detail and is a good default for local investigation.
- `jfr.duration` is optional. Use it to bound whole-run captures; for section-level analysis, custom events define the effective interval.
- For long-running processes, prefer starting/stopping recordings with `jcmd` around the specific workflow to reduce noise.

## Project Structure

```
src/
  main/
    java/com/example/app/       # Application source
    resources/                  # Application resources
  test/
    java/com/example/app/       # Test source
    resources/                  # Test resources
config/
  formatting/                  # Eclipse formatter config (used by Spotless)
```