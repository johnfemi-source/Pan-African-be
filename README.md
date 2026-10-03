# Pan-African Backend

Java 21, Gradle, Spring Boot, and MySQL starter using a layered backend structure. No product-specific entities or business rules are assumed; add them once the PRD is available.

## Run locally

Prerequisites: JDK 21 and a locally installed MySQL server. The Gradle wrapper is included.

1. In MySQL, create the local database and application user:

	```sql
	CREATE DATABASE pan_african;
	CREATE USER 'pan_african'@'localhost' IDENTIFIED BY 'pan_african_dev';
	GRANT ALL PRIVILEGES ON pan_african.* TO 'pan_african'@'localhost';
	```

2. Start the API with `./gradlew bootRun`.
3. Check the API at `http://localhost:8081/api/v1/health` or the Actuator endpoint at `http://localhost:8081/actuator/health`.
4. Run the test suite with `./gradlew test`.

The health response is JSON: `{"status":"UP","service":"pan-african-backend"}`.

## Database configuration

The application reads connection settings from environment variables:

| Variable | Default |
| --- | --- |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `pan_african` |
| `DB_USER` | `pan_african` |
| `DB_PASSWORD` | `pan_african_dev` |
| `DB_DDL_AUTO` | `update` |
| `PORT` | `8081` |

`DB_URL` can override the complete JDBC URL. Defaults are for local development only; use secrets from your MySQL provider outside local development. Keep schema changes explicit and set up migrations before production.

## Deploy on Railway with MySQL

1. Create a Railway project from this backend GitHub repository. The Dockerfile is at the repository root, so leave the root directory blank.
2. Add a Railway MySQL service. If its service name is `MySQL`, add these variables to the backend service:

| Variable | Value |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}` |
| `DB_USER` | `${{MySQL.MYSQLUSER}}` |
| `DB_PASSWORD` | `${{MySQL.MYSQLPASSWORD}}` |
| `DB_DDL_AUTO` | `update` |
| `FRONTEND_ORIGINS` | `https://pan-african-one.vercel.app` |
| `PAYAN_BOOTSTRAP_ADMIN_USERNAME` | Your chosen initial admin username |
| `PAYAN_BOOTSTRAP_ADMIN_PASSWORD` | A strong initial admin password, stored as a Railway secret |

Replace `MySQL` in the variable references if your database service has a different name. Railway supplies `PORT` automatically. Use the Railway MySQL service variables, not `localhost` or a public database host.

After the service is running, generate a public domain in the backend service's Networking settings. Set the Vercel `VITE_API_URL` variable to that domain without an `/api/v1` suffix, then redeploy the frontend. The backend allows the Vercel origin through `FRONTEND_ORIGINS`.

## First admin account

There is no shared default admin account. To create the first super admin, stop any running backend, then start it with a username and a password entered directly in your terminal:

```sh
export PAYAN_BOOTSTRAP_ADMIN_USERNAME=your-chosen-username
read -rsp 'Choose an admin password: ' PAYAN_BOOTSTRAP_ADMIN_PASSWORD
printf '\n'
export PAYAN_BOOTSTRAP_ADMIN_PASSWORD
./gradlew bootRun --console=plain
```

The bootstrap creates an account only when the database has no staff accounts. The password is stored as a BCrypt hash. Once the backend is running, open the frontend dashboard and sign in with that username and password. Existing accounts are never overwritten by bootstrap settings.

## Layers

- `api`: REST controllers and request/response DTOs.
- `service`: business use cases and transaction boundaries.
- `repository`: Spring Data persistence interfaces.
- `domain`: entities and domain rules.
- `config` and `exception`: cross-cutting configuration and API error handling.

Place new feature code in the appropriate layer and keep controllers focused on HTTP concerns.
# Pan-African-be
