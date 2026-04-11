GrainSync — Complete Project Documentation
=========================================

Overview
--------
This repository's initial commit is an ORM-based Java Spring Boot backend exposing REST APIs. It is a template for a business ERP system that provides foundational JPA entities, repositories, basic REST controllers, and configuration scaffolding to be extended for HR, Production, Sales, Finance, and Admin modules.

Project layout
--------------
GrainSync/
│
├── `grainsync-frontend/`     \# React + Vite Frontend
│   ├── `src/`
│   ├── `public/`
│   └── `.env`
│
├── `grainsync-backend/`      \# Spring Boot Backend (current repo focus)
│   ├── `src/main/java/`
│   ├── `src/main/resources/`
│   └── `pom.xml`
│
└── `README.md`               \# This documentation

Tech stack
----------
- Java 17
- Spring Boot 3.x
- Spring Data JPA (ORM)
- Spring Web (REST API)
- MySQL (runtime)
- Lombok
- Maven (with wrapper)

Frontend (React + Vite)
-----------------------
Directory: `grainsync-frontend`

Quick start:
1. Clone the frontend repo or subfolder.
2. Install dependencies: `npm install`
3. Configure `.env` (example):
   - `VITE_BACKEND_URL=https://your-backend.example.com`
4. Run: `npm run dev`

Backend (Spring Boot)
---------------------
Directory: `grainsync-backend`

Description:
- First commit focuses on an ORM-based backend (Spring Data JPA) exposing REST endpoints as a template ERP backend.

Configuration (do not commit secrets)
- Do not store production credentials in the repository.
- Prefer environment variables, platform secrets, or an external config store.

Example environment variables to set (Windows, PowerShell or CI):
- `SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/grainsync_db`
- `SPRING_DATASOURCE_USERNAME=<your_db_user>`
- `SPRING_DATASOURCE_PASSWORD=<your_db_password>`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
- `SERVER_PORT=8080`

Example `application.properties` snippet (use placeholders or environment variable expansion):
Run locally (Windows):
- With Maven wrapper: `mvnw.cmd spring-boot:run`
- Or build and run: `mvnw.cmd clean package` then `java -jar target/*.jar`

Notes
-----
- The project includes `mvnw` scripts and `.mvn/wrapper/` for consistent Maven usage.
- Railway `railway.toml` example is present for deployment; ensure secrets are set in the platform, not the repo.

CI / Deployment
---------------
Example (Railway):
- `railway.toml` uses NIXPACKS and sets `BP_JAVA_VERSION = "17"`.
- Set `MAVEN_GOALS` and repository secrets in Railway UI.
- Start command: `java -jar target/*.jar`

Progress Log (persistent, append-only)
-------------------------------------
Maintain this section at the top of `README.md` to allow resuming work.

Template for each entry (append at top):
- Date: YYYY-MM-DD
- Author: GitHub username / local name
- Summary: one-line summary
- Files changed / opened: list of relevant paths
- State: e.g. `working`, `blocked`, `needs review`
- Next steps: short actionable items
- Notes: commands used, environment, errors

Example entry (initial commit)
- Date: 2026-04-11
- Author: `Hamza-Malik05`
- Summary: Initial template commit — ORM-based Spring Boot backend with JPA entities, repositories, and REST controllers.
- Files changed / opened:
  - `pom.xml`
  - `src/main/resources/application.properties` (template)
  - `src/main/java/...` (domain/repository/controller placeholders)
- State: working
- Next steps:
  - Add entity examples for Employee and Department
  - Implement DTOs and service layer
  - Add integration tests for REST endpoints
- Notes:
  - Use `mvnw.cmd` on Windows; do not commit secrets.

Guidelines
----------
- Never commit real credentials or secrets.
- Add a short, actionable "Next steps" on each session start and append to the Progress Log before pausing.
- Include exact commands and error output when blocked.

Contact / Ownership
-------------------
- Primary repo owner: `Hamza-Malik05`
- Record collaborators in the Progress Log when they make changes.

Changelog
---------
- 2026-04-11: `README.md` updated — clarified first commit intent, added Progress Log template, and removed hardcoded credentials.