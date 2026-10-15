# Jakarta Servlet Initialization Demo

This project is a small Jakarta Servlet web application that demonstrates the
servlet lifecycle. The `InitDemoServlet` records the time at which its servlet
instance is initialized and then reports that value for every request.

## What it demonstrates

- `init()` runs once for a servlet instance.
- The same servlet instance handles subsequent `GET /init-demo` requests.
- The regular request counter increases on every request.
- The atomic request counter uses `AtomicInteger`, which is safe for concurrent
  updates.
- The instance identifier remains unchanged while the servlet instance is
  reused.

## Requirements

- Java 25 or newer
- Maven 3.9 or newer, or the included Maven Wrapper
- A Jakarta Servlet 6.1-compatible web container, such as Tomcat 11

## Build

From the project directory, run:

```text
./mvnw clean package
```

On Windows, use:

```text
mvnw.cmd clean package
```

The resulting WAR file is created in `target/`.

## Test

Run the unit tests with:

```text
mvnw.cmd test
```

The tests verify servlet mapping, initialization timestamp creation, request
attributes, forwarding to `result.jsp`, counter behavior across repeated
requests, the English landing page and result page, and the Jakarta deployment
descriptor. The suite also includes an embedded Tomcat integration test,
concurrent request coverage, and parser-backed HTML assertions.

The GitHub Actions workflow runs `clean verify` on every push and pull request.

## Run

Deploy the generated `jakarta-init-1.0-SNAPSHOT.war` file to a compatible
servlet container. Open the application context in a browser and select the
initialization demo, or navigate directly to:

```text
/jakarta-init-1.0-SNAPSHOT/init-demo
```

Reload the page several times to observe that the initialization time and
instance identifier stay the same while the counters increase.

## Application structure

| Path | Purpose |
| --- | --- |
| `src/main/java/es/daw/jakartainit/InitDemoServlet.java` | Servlet lifecycle and request handling |
| `src/main/webapp/index.html` | Application landing page |
| `src/main/webapp/result.jsp` | Initialization and counter output |
| `pom.xml` | Maven project and dependency configuration |

## Request attributes

The servlet forwards these attributes to the result page:

| Attribute | Description |
| --- | --- |
| `initializationTime` | ISO-8601 timestamp recorded by `init()` |
| `requestCount` | Non-atomic count of handled requests |
| `atomicRequestCount` | Atomic count of handled requests |
| `instanceId` | The servlet instance's `hashCode()` |
