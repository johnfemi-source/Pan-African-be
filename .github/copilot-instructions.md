# Workspace Instructions

- Backend stack: Java 21, Gradle, Spring Boot, and MySQL.
- Keep HTTP handling in `api`, business logic in `service`, persistence in `repository`, and business data/rules in `domain`.
- Use environment variables for database credentials; never commit secrets.
- Do not invent product entities or behavior without the PRD.
- Run `./gradlew test` after backend changes.
