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

## Guidelines / Best practices
- Append new entries at the top; do not rewrite history.
- Include exact commands and any error output when blocked.
- Keep next steps small and specific to allow immediate resumption.