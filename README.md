# GrainSync — Complete Project Documentation

Progress Log (pointer)
- Maintain the detailed, append-only session history in `Documentation.md`. Append a new entry at the top each session.
- README file is prone to changes along the way.
Overview
--------
This repository's initial commit is an ORM-based Java Spring Boot backend exposing REST APIs. It is a starter template for a business ERP system providing JPA entities, repositories, basic REST controllers, and configuration scaffolding for HR, Production, Sales, Finance, and Admin modules.

Project layout
--------------
```text
GrainSync/
├── grainsync-frontend/   # React + Vite Frontend
├── grainsync-backend/    # Spring Boot Backend (current focus)
├── Documentation.md      # Persistent, append-only progress log
└── README.md             # This high-level overview
```

Tech stack
----------
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring Web (REST API)
- MySQL (runtime)
- Lombok
- Maven (with wrapper)

Quick start (Backend)
---------------------
1. Set environment variables (do not commit secrets):
   - `SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/grainsync_db`
   - `SPRING_DATASOURCE_USERNAME=<your_db_user>`
   - `SPRING_DATASOURCE_PASSWORD=<your_db_password>`
   - `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
   - `SERVER_PORT=8080`
2. Example `application.properties` (use env expansion):
   ```
   spring.datasource.url=${SPRING_DATASOURCE_URL}
   spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
   spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
   spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO}
   server.port=${SERVER_PORT}
   ```
3. Run on Windows:
- `mvnw.cmd spring-boot:run`
- Or build and run: `mvnw.cmd clean package` then `java -jar target/*.jar`

CI / Deployment
---------------
- `railway.toml` example uses NIXPACKS with `BP_JAVA_VERSION = "17"`.
- Set `MAVEN_GOALS` and secrets in the platform UI.
- Start command: `java -jar target/*.jar`

Guidelines
----------
- Use `Documentation.md` as the single source of truth for session history (append-only).
- Never commit real credentials or secrets; use environment variables or platform secret stores.
- On session start or before pausing, append a short actionable "Next steps" entry to `Documentation.md`.

Contact / Ownership
-------------------
- Primary repo owner: `Hamza-Malik05`
- Contributors: `SkinnyLadd`, `AbdullahSom`


