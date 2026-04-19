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

##  Entry 05
- Date: 2026-04-18
- Time: 2:19 PM
- Author: `Hamza-Malik05`
- Summary: Implemented JdbcTemplate-backed persistence and service layer for Accountant; updated REST controller and ensured consistency with the DAO/Service refactor (continued migration away from JPA).
- Files changed / opened:
  - `src/main/java/com/plant_management/dao/AccountantDao.java`
  - `src/main/java/com/plant_management/service/AccountantService.java`
  - `src/main/java/com/plant_management/controller/AccountantController.java`
  - `src/main/java/com/plant_management/controller/EmployeeController.java`
  - `Documentation.md`
- State: working
- Next steps:
  - Run unit and integration tests for accountant endpoints .
  - Verify `accountants` table migrations and generated keys in PostgreSQL.
  - Add `EmployeeDao` lookups in `AccountantDao` to populate `employee` when required.
  - Continue removing JPA dependencies from `pom.xml` and validate full build.
- Notes:
  - Replaced JPA repositories with `JdbcTemplate` DAOs; used `RowMapper` and `GeneratedKeyHolder` for inserts.
  - Controller uses the service layer; ensured DI is consistent to avoid autowiring issues.
  - Use `mvnw.cmd` on Windows or `mvn` to run tests and start the application.

## Entry 06
- Date: 2026-04-18
- Time: 3:45 PM
- Author: `Hamza-Malik05`
- Summary: Converted Batch and Attendance repositories to JdbcTemplate DAOs; fixed `AttendanceService` save/batch logic and corrected method call typos; updated services to use DAOs and ensured transactional handling.
- Files changed / opened:
  - `src/main/java/com/plant_management/dao/BatchDao.java`
  - `src/main/java/com/plant_management/service/BatchService.java`
  - `src/main/java/com/plant_management/dao/AttendanceDao.java`
  - `src/main/java/com/plant_management/service/AttendanceService.java`
  - `src/main/java/com/plant_management/dao/AccountantDao.java`
  - `src/main/java/com/plant_management/service/AccountantService.java`
  - `src/main/java/com/plant_management/dao/EmployeeDao.java`
  - `src/main/java/com/plant_management/controller/AuthorizationController.java`
  - `src/main/java/com/plant_management/dto/BatchRequestDTO.java`
  - `Documentation.md`
- State: working
- Next steps:
  - Run targeted tests: `mvn -Dtest=Attendance* test` and `mvn -Dtest=Batch* test` (use `mvnw.cmd` on Windows).
  - Verify generated keys and schema compatibility for `batches` and `attendance` tables in PostgreSQL.
  - Populate nested `employee` / `product` objects in DAOs (use `EmployeeDao` / `ProductDao` lookups) where full objects are required.
  - Remove remaining JPA dependencies from `pom.xml` and run full build.
- Notes:
  - Replaced JPA repositories with `JdbcTemplate` DAOs; used `RowMapper`, `PreparedStatementCreator`, and `GeneratedKeyHolder` for inserts/updates.
  - `AttendanceService.initializeAttendanceForDate` now creates and saves records individually to avoid unsupported `saveAll` usage.
  - `BatchService` now delegates persistence to `BatchDao` and continues to update storage units via repositories.
  - Use `mvnw.cmd` on Windows and ensure database migrations are applied before running integration tests.

## Entry 07
- Date: 2026-04-18
- Time: 7:03 PM
- Author: `Hamza-Malik05`
- Summary: Added documentation entry for recent DAO/service refactorings and model updates.
- Files changed / opened:
  - Remaining Model files.
- State: working
- Next steps:
  - Change the JPA Repository Interfaces to JdbcTemplate DAOs for the remaining entities.
  - Remove remaining JPA dependencies from `pom.xml` and run full build.
- Notes:
  - Continued migration from JPA to `JdbcTemplate` DAOs; ensured insert/update key handling with `GeneratedKeyHolder`.
  - `AttendanceService.initializeAttendanceForDate` now saves records individually to avoid unsupported `saveAll` usage.
  - New/updated POJOs: `Vehicle`, `Bill`, `Customer` (basic fields present); confirm mapping in DAOs if persisted.
  - Commands used: `mvnw.cmd -Dtest=Attendance* test`, `psql` for quick schema checks.
---

## Entry 08
- Date: 2026-04-18
- Time: 9:16 PM
- Author: Hamza-Malik05
- Summary: Converted several repositories to JdbcTemplate DAOs; fixed RowMapper mappings, enum handling and insert/update key handling; updated service logic to use DAOs consistently (orders, transactions, suppliers) and adjusted delivery flows to use OrderDao.
- Files changed / opened:
  - src/main/java/com/plant_management/dao/TransactionDao.java
  - src/main/java/com/plant_management/dao/SupplierDao.java
  - src/main/java/com/plant_management/dao/OrderDao.java
  - src/main/java/com/plant_management/service/OrderService.java
  - src/main/java/com/plant_management/service/DeliveryService.java
  - src/main/java/com/plant_management/dao/EmployeeDao.java
  - src/main/java/com/plant_management/service/ProductService.java
- State: working
- Next steps:
  - Run unit and integration tests: mvnw.cmd -Dtest=Order* test, mvnw.cmd -Dtest=Delivery* test.
  - Convert remaining JPA repositories to DAOs (notably DeliveryRepository, ProductRepository) and update ProductService/controllers to use DAOs.
  - Resolve any failing tests and adjust DAO RowMappers to populate nested objects where required.
  - Remove leftover JPA dependencies from pom.xml and validate full build.
- Notes:
  - Environment: Windows, PostgreSQL.
  - Commands used: mvnw.cmd -Dtest=DbConnectionTest test, mvnw.cmd -Dtest=Order* test.
  - Observations: GeneratedKeyHolder used for inserts; ensure database schema sequences/PK behavior matches insert expectations.
---
## Entry 09
- Date: 2026-04-19
- Time: 2:54 PM
- Author: Hamza-Malik05
- Summary: Migrated Salary, Bill, Product and other repositories to JDBC DAOs with PostgreSQL functions for complex joins and fixed date/time parsing.
- Files changed / opened:
  - src/main/java/com/plant_management/dao/SalariesDao.java
  - src/main/java/com/plant_management/dao/BillDao.java
  - src/main/java/com/plant_management/dao/ProductDao.java
  - src/main/java/com/plant_management/service/BillService.java
  - src/main/java/com/plant_management/service/ProductService.java
  - src/main/java/com/plant_management/model/Bill.java
  - src/main/java/com/plant_management/model/Salaries.java
  - All other remaining Model, DAO and Service files for consistency.
- State: working
- Next steps:
  - Execute SQL scripts to create get_salary_by_employee_and_date, get_salaries_by_date, and get_all_bill_details functions in PostgreSQL.
  - Update the database schema to support the create_new_bill procedure called in BillService.
  - Verify BillResponseDTO constructor matches the updated RowMapper logic for LocalDate conversion.
  - Test the backend for optimal and working functionality with the new DAOs and database functions.
- Notes:
- Environment: Windows, PostgreSQL.
  - Implemented explicit PostgreSQL RETURNS TABLE functions to replace JPA @Query annotations for Salaries and Bill joins.
  - Resolved Cannot resolve method 'valueOf(Date)' error by converting java.util.Date to java.sql.Date using .getTime().
  - Added manual Timestamp to LocalDate parsing in BillDao to ensure compatibility with BillResponseDTO.
  - Refactored BillService and ProductService to use constructor-based injection for DAOs instead of field-based @Autowired.
---

## Guidelines / Best practices
- Append new entries at the top; do not rewrite history.
- Include exact commands and any error output when blocked.
- Keep next steps small and specific to allow immediate resumption.