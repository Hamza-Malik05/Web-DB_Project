# Documentation — Progress Log (append-only)

Maintain this file as the persistent, append-only progress log. Append a new entry at the top on every session start or before pausing.

Template for each entry (append at top)
- Date: YYYY-MM-DD
- Author: GitHub username / local name
- Summary: one-line summary
- Files changed / opened: list of relevant paths
- State: e.g. `working`, `blocked`, `needs review`
- Next steps: short actionable items
- Notes: commands used, environment variables, error output

---

## Initial entry
- Date: 2026-04-11
- Time: 3:39 PM
- Author: `Hamza-Malik05`
- Summary: Initial template commit — ORM-based Spring Boot backend with JPA entities, repositories, and REST controllers.
- Files changed / opened:
  - `pom.xml`
  - `src/main/resources/application.properties` (template)
  - `src/main/java/...` (domain/repository/controller placeholders)
- State: working
- Next steps:
  - Completely overhaul the ORM based architecture to a more modular, layered design.
  - Change the database from MySQL to PostgreSQL for better performance and features.
  - Replace JPA with JDBC using more pure SQL queries for better control and efficiency.
- Notes:
  - Use `mvnw.cmd` on Windows; do not commit secrets.

---

## Entry 01
- Date: 2026-04-12
- Time: 7:15 PM
- Author: `Hamza-Malik05`
- Summary: Updated database configuration to use PostgreSQL.
- Files changed / opened:
  - `pom.xml`
  - `src/main/resources/application.properties`
- State: working
- Next steps:
  - Test the application with the updated PostgreSQL configuration.
  - Verify database connectivity and query execution.
  - Update any SQL scripts or queries to be compatible with PostgreSQL syntax if necessary.
- Notes:
  - Replaced MySQL dependency with PostgreSQL in `pom.xml`.
  - Updated `application.properties` with PostgreSQL connection details.

## Entry 02
- Date: 2026-04-12
- Time: 10:22 PM
- Author: `Hamza-Malik05`
- Summary: Updated `database.sql` and added `procedures.sql` for database procedures.
- Files changed / opened:
  - `database.sql`
  - `procedures.sql`
- State: working
- Next steps:
  - Test the new procedures in `procedures.sql` for correctness.
  - Verify the database schema updates in `database.sql`.
  - Ensure compatibility with the application code.
  - Ensure the connection of the application to the database is working correctly with the new schema and procedures.
- Notes:
  - Added new procedures for creating bills and registering users in `procedures.sql`.
  - Updated `database.sql` with schema changes and initial data.

## Guidelines / Best practices
- Append new entries at the top; do not rewrite history.
- Include exact commands and any error output when blocked.
- Keep next steps small and specific to allow immediate resumption.