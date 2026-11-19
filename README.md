# Lingua Space

> Product name: **Lingua Space**. The GitHub repository (`LiguaSpace`) and the Java package (`com.example.linguaspace`) keep their original technical names.

## Run locally without PostgreSQL

The deployed app (Render) uses PostgreSQL through `DATABASE_URL`. For local work, H2 is on the
runtime classpath, so the same app can run against a file database:

```powershell
$env:DATABASE_URL = 'jdbc:h2:file:./data/lingua-local;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE'
$env:DATABASE_USERNAME = 'sa'; $env:DATABASE_PASSWORD = ''; $env:PORT = '8081'
.\gradlew.bat bootRun
```

Or use the desktop shortcut created by `SAEO-WORKSPACE/scripts/create-shortcuts.ps1`.
Local data lives in `data/` (ignored by Git); production data stays in PostgreSQL.
