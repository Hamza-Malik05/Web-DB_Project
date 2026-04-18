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

## Entry 03
- Date: 2026-04-17
- Time: 11:20 AM
- Author: `Hamza-Malik05`
- Summary: Created PostgreSQL database `Flour_mill` and verified JDBC connection from the application tests.
- Files changed / opened:
  - `database.sql`
  - `procedures.sql`
  - `src/main/resources/application.properties`
  - `.env`
  - `src/test/java/.../DbConnectionTest.java`
- State: working
- Next steps:
  - Run full integration tests against `Flour_mill`.
  - Execute and validate stored procedures in `procedures.sql`.
  - Remove credentials from repo and switch to secure secret management.
  - Convert the application classes to use JDBC templates or raw SQL queries instead of JPA for better performance and control.
- Notes:
  - DBMS: PostgreSQL (ver. 18.3)
  - Case sensitivity: plain=lower, delimited=exact
  - Driver: PostgreSQL JDBC Driver (ver. 42.7.3, JDBC4.2)
  - Ping: 102 ms
  - SSL: no
  - Test: `DbConnectionTest` executed successfully; application established a JDBC connection using the configured datasource (`src/main/resources/application.properties` / `.env`). \- No sensitive values recorded in this log.
  - Commands used: `mvn -Dtest=DbConnectionTest test` and validation via `psql` client for schema inspection.

## Entry 04
- Date: 2026-04-18
- Time: 1:04 PM
- Author: `Hamza-Malik05`
- Summary: Refactored persistence for User, Employee, and Department to JdbcTemplate-based DAOs; removed JPA annotations; implemented stored-procedure-based user registration.
- Files changed / opened:
  - `src/main/java/com/plant_management/model/Users.java`
  - `src/main/java/com/plant_management/dao/UserDao.java`
  - `src/main/java/com/plant_management/service/UserService.java`
  - `src/main/java/com/plant_management/controller/UserController.java`
  -  `src/main/java/com/plant_management/model/Department.java`
  - `src/main/java/com/plant_management/dao/DepartmentDao.java`
  - `src/main/java/com/plant_management/service/DepartmentService.java`
  - `src/main/java/com/plant_management/controller/DepartmentController.java`
  -  `src/main/java/com/plant_management/model/Employee.java`
  - `src/main/java/com/plant_management/dao/EmployeeDao.java`
  - `src/main/java/com/plant_management/service/EmployeeService.java`
  - `src/main/java/com/plant_management/controller/EmployeeController.java`

- State: working
- Next steps:
  - Run unit and integration tests for user and employee endpoints.
  - Verify `register_user_from_employee` stored procedure on the PostgreSQL instance.
  - Add `EmployeeDao`/`DepartmentDao` mapping to load `employee` in `UserDao` if needed.
  - Remove remaining JPA dependencies from `pom.xml` and validate build.
- Notes:
  - Replaced JPA entities with plain POJOs and `JdbcTemplate`-backed DAOs.
  - Implemented full CRUD in `UserDao`; `UserService` now delegates to DAO and handles transactions.
  - `register_user_from_employee` stored procedure enforces one-user-per-employee and assigns role by department.
  - Use `mvnw.cmd` on Windows; run tests with `mvn -Dtest=<TestName> test`.

## Guidelines / Best practices
- Append new entries at the top; do not rewrite history.
- Include exact commands and any error output when blocked.
- Keep next steps small and specific to allow immediate resumption.