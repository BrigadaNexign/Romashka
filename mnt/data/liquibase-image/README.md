# Custom Liquibase Image (with bundled changelogs)

## Build
```bash
docker build -t my-liquibase:0.0.4 .
```

## Run with environment variables
```bash
docker run --rm   -e LIQUIBASE_URL="jdbc:postgresql://host.docker.internal:5432/appdb"   -e LIQUIBASE_USERNAME="postgres"   -e LIQUIBASE_PASSWORD="secret"   my-liquibase:0.0.4
```

To run a different command (e.g. `updateSQL` or `rollback`), override `LIQUIBASE_COMMAND`:
```bash
docker run --rm   -e LIQUIBASE_URL="jdbc:postgresql://host.docker.internal:5432/appdb"   -e LIQUIBASE_USERNAME="postgres"   -e LIQUIBASE_PASSWORD="secret"   -e LIQUIBASE_COMMAND="updateSQL"   my-liquibase:0.0.4
```

## Use a properties file instead of env
Copy `liquibase.properties.template` to `liquibase.properties`, adjust values, then either:
- Rebuild with `COPY liquibase.properties /liquibase/liquibase.properties` added to the Dockerfile, or
- Mount at runtime: `-v $(pwd)/liquibase.properties:/liquibase/liquibase.properties`, remove env vars and entrypoint flags, and call `liquibase update` directly.

## Notes
- The image includes your versioned changelogs under `/liquibase/changelog` with `changelog-master.xml` including:
  - `v.0.0.2/cumulative.xml`
  - `v.0.0.3/cumulative.xml`
  - `v.0.0.4/cumulative.xml`
- If your database requires a JDBC driver not present in the base image, uncomment the `ARG/ RUN` lines in the Dockerfile to download it.
