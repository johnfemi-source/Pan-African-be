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
| `DB_DDL_AUTO` | `none` |
| `PORT` | `8081` |

`DB_URL` can override the complete JDBC URL. The default credentials are for local development only; set `DB_USER` and `DB_PASSWORD` appropriately outside local development. Keep schema changes explicit and set up migrations before introducing persistent entities.

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
