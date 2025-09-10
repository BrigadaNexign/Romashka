# Minimal custom Liquibase image with bundled changelogs
FROM liquibase/liquibase:latest

# Optional: add PostgreSQL JDBC driver (uncomment to pin a specific version)
# ARG POSTGRESQL_JDBC_VERSION=42.7.4
# RUN curl -L -o /liquibase/lib/postgresql-${POSTGRESQL_JDBC_VERSION}.jar \
#   https://repo1.maven.org/maven2/org/postgresql/postgresql/${POSTGRESQL_JDBC_VERSION}/postgresql-${POSTGRESQL_JDBC_VERSION}.jar

# Copy changelogs into image
COPY liquibase.d /liquibase/changelog
COPY liquibase.properties /liquibase/liquibase.properties

# Default locations and behavior
ENV CHANGELOG_FILE=/liquibase/changelog/changelog-master.xml
ENV LIQUIBASE_COMMAND=update

# Simple entrypoint that runs Liquibase once with env-provided connection
COPY --chmod=0755 entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
