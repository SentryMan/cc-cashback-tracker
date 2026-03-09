# Cashback Tracker

A lightweight desktop app I made for myself for optimizing credit card spending. Tracks cashback rates by category, log transactions, and recommends which card to use.

## Features

- **Card management** — Add cards with per-category cashback rates, annual fees, and reward spending limits
- **Smart recommendations** — Instantly see which card earns the most for a given spending category
- **Category management** — Define custom spending categories beyond the built-in defaults

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 (records, JPMS) |
| HTTP server | [Avaje Jex](https://avaje.io/jex/) (Java's built-in server wrapper) |
| Dependency injection | [Avaje Inject](https://avaje.io/inject/) |
| JSON | [Avaje JsonB](https://avaje.io/jsonb/) |
| Desktop window | [Avaje Webview](https://github.com/avaje/avaje-webview) |
| Frontend | HTMX + Tailwind CSS |
| Templating | [JStachio](https://github.com/jstachio/jstachio) (Mustache) |

## Getting Started

### Run (standard)

```bash
./run.sh
```

This compiles and launches the app as a modular JAR. A native window opens automatically.

### Run (native executable)

```bash
./runNative.sh
```

Compiles to a standalone native binary via GraalVM — faster startup, no JVM needed at runtime.

### Run (JLink image)

```bash
./runJlinked.sh
```

Builds a trimmed custom JVM runtime. Smaller distribution than a full JDK, faster to build than native compilation.
