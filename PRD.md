# PAYAN Backend Product Requirements Document

**Status:** Draft for MVP implementation  
**Product:** Pan-African Youth Ambassador Network (PAYAN) website and ambassador administration API  
**Backend baseline:** Java 21, Spring Boot 3.5, Spring Data JPA, MySQL  
**API base path:** `/api/v1`

## 1. Purpose

Build the backend that powers the existing PAYAN React website. The API must publish the network's regions, countries, and country updates; accept ambassador applications and contact messages; and provide authenticated tools for representatives and administrators.

This document turns the current frontend's data needs into an implementable MVP contract. The React app is the source of truth for the workflows and displayed fields described here. Product and security decisions needing confirmation are collected in [Open Decisions](#12-open-decisions).

## 2. Goals and Non-goals

### Goals

- Serve public region and country data and country news/event posts.
- Accept public ambassador applications and contact messages.
- Authenticate PAYAN staff and enforce role and geographic access controls.
- Let authorized staff publish country posts and review applications.
- Let super admins create staff accounts and read submitted contact messages.
- Persist data in MySQL with explicit database migrations.

### Non-goals for MVP

- Public user registration or self-service password reset.
- Editing or deleting published posts through the current frontend.
- Email or SMS delivery, application notifications, or applicant accounts.
- Image uploads, event registration, comments, likes, analytics dashboards, or public financial statements.
- Public display of application or contact-message records.

## 3. Users and Permissions

| Role | Scope | Capabilities |
| --- | --- | --- |
| Anonymous visitor | Public | Read regions, countries, and country posts; submit an application; submit a contact message. |
| `COUNTRY_REP` | One assigned country | Read own identity; publish news/events only to assigned country. |
| `REGIONAL_COORDINATOR` | One assigned region | Read own identity; publish to countries in assigned region; list and update applications belonging to that region. |
| `SUPER_ADMIN` | Network-wide | All coordinator capabilities; manage all applications; create user accounts; read contact messages; publish to any country. |

Authorization must be enforced by the backend for every request. Hiding a country option in the UI is not authorization. Public endpoints must not expose user records, application records, or contact messages.

## 4. Core Workflows

1. A visitor loads regions, chooses one, and sees its countries.
2. A visitor opens a country and sees its published updates, newest first, or an empty state.
3. A visitor submits an ambassador application for a country. It begins in `PENDING` status.
4. A visitor submits a contact message. It is stored for super-admin follow-up.
5. A representative signs in, gets their role/scope, and publishes a `NEWS` or `EVENT` post to an allowed country.
6. A regional coordinator reviews applications for their region; a super admin reviews all applications and may create accounts.

## 5. API Conventions

- All routes are under `/api/v1`.
- JSON request and response bodies use `Content-Type: application/json` and camelCase field names.
- Successful collection endpoints return a JSON array directly; item endpoints return an object directly. This matches the current React code's expected shapes.
- Timestamps are ISO 8601 UTC strings, for example `2026-10-02T18:30:00Z`.
- Identifiers are opaque strings or UUIDs; clients must not infer database IDs from slugs.
- Slugs are stable URL-safe identifiers. Country ISO codes are lowercase ISO 3166-1 alpha-2 values for the current flag image URLs.
- Errors return JSON `{ "message": "Human-readable safe message" }` and an appropriate HTTP status. Never return stack traces, SQL details, password hashes, or credentials.
- Lists must have deterministic order. Region/country lists use configured display order then name; country posts use `createdAt` descending.
- MVP list sizes are expected to be small. Add pagination before collections can grow enough to affect response time; if pagination is added, preserve the current direct-array behavior until the frontend is updated.

### API version integration

The frontend currently sends paths such as `/api/regions` and `/api/countries`, but the backend starter documents `/api/v1/health`. Implement the routes in this document under `/api/v1`, and update the frontend API helper to prepend `/api/v1` exactly once (then pass paths such as `/regions`), or explicitly agree on another shared convention before implementation. Do not deploy a frontend/backend pair with mismatched prefixes.

## 6. Endpoint Requirements

### Health

`GET /api/v1/health`

- Public liveness response: `{ "status": "UP", "service": "pan-african-backend" }`.
- Keep database readiness separate from liveness if the service is later deployed with orchestration. Actuator health may remain available according to the deployment policy.

### Public regions and countries

`GET /api/v1/regions`

Returns an ordered array:

```json
[
  { "slug": "west-africa", "name": "West Africa" }
]
```

`GET /api/v1/regions/{regionSlug}`

Returns:

```json
{
  "slug": "west-africa",
  "name": "West Africa",
  "countries": [
    { "slug": "ghana", "name": "Ghana", "iso": "gh" }
  ]
}
```

`GET /api/v1/countries`

Returns an ordered array suitable for application forms and staff assignment:

```json
[
  { "slug": "ghana", "name": "Ghana", "iso": "gh", "regionSlug": "west-africa" }
]
```

`GET /api/v1/countries/{countrySlug}`

Returns country details and public posts:

```json
{
  "regionSlug": "west-africa",
  "regionName": "West Africa",
  "country": { "slug": "ghana", "name": "Ghana", "iso": "gh" },
  "posts": [
    {
      "id": "opaque-id",
      "title": "Youth leadership forum",
      "body": "Post text",
      "category": "EVENT",
      "createdAt": "2026-10-02T18:30:00Z",
      "author": "Representative display name"
    }
  ]
}
```

Rules:

- Only published posts are public. MVP posts may be published immediately on creation; drafts are not required by the current UI.
- `category` is exactly `NEWS` or `EVENT`.
- Unknown region/country slugs return `404`.
- Empty countries return `posts: []`.

`POST /api/v1/countries/{countrySlug}/posts` (authenticated)

Request:

```json
{
  "countrySlug": "ghana",
  "title": "Youth leadership forum",
  "body": "Post text",
  "category": "EVENT"
}
```

- `title`, `body`, and `category` are required. Suggested bounds: title 3-180 chars and body 1-10000 chars.
- The path's `countrySlug` is authoritative. The current frontend also includes `countrySlug` in the request body; if present, it must match the path or the request returns `400`.
- The author is always taken from the authenticated user, never from client input.
- Country reps may publish only to their assigned country; regional coordinators only to countries in their region; super admins to any existing country.
- Create and publish the post immediately, with server-generated ID and UTC timestamps. Return `201 Created` with the public post representation (`id`, `title`, `body`, `category`, `createdAt`, `author`).
- Invalid country returns `404`; missing/invalid fields or category return `400`; missing credentials return `401`; disallowed country scope returns `403`.

### Ambassador applications

`POST /api/v1/applications` (public)

Request:

```json
{
  "fullName": "Ama Example",
  "email": "ama@example.org",
  "phone": "+233...",
  "countrySlug": "ghana",
  "motivation": "Why I want to represent PAYAN"
}
```

- `fullName`, `email`, `countrySlug`, and `motivation` are required; `phone` is optional and may be null/empty.
- Validate lengths, email syntax, and that the country exists. Suggested initial bounds: name 2-160 chars, email max 254, phone max 40, motivation 20-5000 chars.
- Save as `PENDING`, with server-generated ID and timestamps. Do not return private submitted details beyond confirmation; respond `201 Created` with `{ "id": "opaque-id", "status": "PENDING" }`.
- Repeated submissions should not silently overwrite an earlier application. Exact duplicate/rate-limit policy is in Open Decisions.

`GET /api/v1/admin/applications` (coordinator or super admin)

- Return an array with `id`, `fullName`, `email`, `phone`, `countrySlug`, `motivation`, `status`, `createdAt`, and optionally `updatedAt`.
- Coordinators see only applications for countries in their assigned region. Super admins see all.
- Default order: oldest pending first, then most recently updated; document chosen ordering in implementation.

`PATCH /api/v1/admin/applications/{applicationId}` (coordinator or super admin)

Request:

```json
{ "status": "APPROVED" }
```

- Allowed statuses: `PENDING`, `APPROVED`, `REJECTED`.
- Only `APPROVED` and `REJECTED` may be set by this endpoint. Updates require an existing `PENDING` application; repeated or conflicting review transitions return `409 Conflict`.
- Coordinators may update only applications in their region. Unauthorized scope returns `403`; unknown IDs return `404`.
- Success returns the updated application representation, or `204 No Content` if the frontend is adjusted accordingly. Current frontend ignores the response and reloads the list.

### Contact messages

`POST /api/v1/messages` (public)

Request:

```json
{ "name": "Ama Example", "email": "ama@example.org", "body": "Partnership enquiry" }
```

- All fields required; validate name 2-160 chars, email max 254 and syntactically valid, body 10-5000 chars.
- Store with generated ID and `createdAt`; return `201 Created` with a minimal acknowledgement, not the stored message body.
- Add basic abuse protection such as request-size limits and rate limiting before production.

`GET /api/v1/admin/messages` (super admin only)

Returns newest first, with `id`, `name`, `email`, `body`, and `createdAt`.

### Authentication and current user

`GET /api/v1/me` (authenticated)

Returns:

```json
{
  "id": "opaque-id",
  "username": "coordinator-west",
  "role": "REGIONAL_COORDINATOR",
  "regionSlug": "west-africa",
  "countrySlug": null
}
```

- For `COUNTRY_REP`, `countrySlug` is set and `regionSlug` may be returned or null. For `REGIONAL_COORDINATOR`, `regionSlug` is set and `countrySlug` is null. For `SUPER_ADMIN`, both scope fields are null.
- Invalid/missing credentials return `401`; valid credentials without the requested role/scope return `403`.

`POST /api/v1/auth/login` is **not required by the current frontend contract**. The React app currently sends HTTP Basic credentials on each request and calls `/me` to validate them. If retaining that contract, configure Spring Security HTTP Basic and require HTTPS in non-local environments. See security requirements and Open Decisions before production.

### User administration

`POST /api/v1/admin/users` (super admin only)

Request:

```json
{
  "username": "country-rep-ghana",
  "password": "initial secret",
  "role": "COUNTRY_REP",
  "countrySlug": "ghana",
  "regionSlug": ""
}
```

- Roles are `COUNTRY_REP`, `REGIONAL_COORDINATOR`, and `SUPER_ADMIN`.
- `COUNTRY_REP` requires a valid `countrySlug`; `REGIONAL_COORDINATOR` requires a valid `regionSlug`; `SUPER_ADMIN` must not have a geographic scope. Validate these rules server-side.
- Username is required, unique, and normalized consistently; password is required and must meet the configured password policy (current UI enforces a minimum of 8 characters; production should use a stronger policy).
- Hash passwords using a modern adaptive password hash (for example BCrypt/Argon2). Never store or log plaintext passwords. Never return the password or hash.
- Return `201 Created` with `id`, `username`, `role`, `countrySlug`, and `regionSlug` only.
- The UI's password is an initial credential. A secure reset/change flow is a release decision before real user provisioning.

## 7. Data Model (MVP)

Use migrations (Flyway or Liquibase) for all schema changes. Avoid relying on Hibernate schema auto-update outside local experiments.

### `regions`

- `id` primary key
- `slug` unique, non-null
- `name` non-null
- `display_order` non-null
- `created_at`, `updated_at`

### `countries`

- `id` primary key
- `region_id` foreign key to `regions`
- `slug` unique, non-null
- `name` non-null
- `iso_code` unique, non-null; lowercase alpha-2
- `display_order` non-null
- `created_at`, `updated_at`

### `users`

- `id` primary key
- `username` unique, non-null
- `password_hash` non-null
- `role` non-null enum/string
- nullable `country_id` and `region_id` foreign keys
- `enabled` boolean
- `created_at`, `updated_at`, optional `last_login_at`
- Database/service constraint: only the scope appropriate to the role may be populated.

### `applications`

- `id` primary key
- applicant `full_name`, `email`, optional `phone`, `motivation`
- `country_id` foreign key
- `status` default `PENDING`
- `created_at`, `updated_at`
- Index by `(country_id, status, created_at)`.

### `posts`

- `id` primary key
- `country_id` foreign key
- `author_user_id` foreign key to `users`
- `title`, `body`, `category`
- `created_at`, `updated_at`
- Index by `(country_id, created_at)`.

### `messages`

- `id` primary key
- `name`, `email`, `body`
- `created_at`
- Index by `created_at`.

Do not seed example applicant, message, or user data in production. Seed verified region/country reference data and provide a safe, documented first-super-admin bootstrap procedure.

## 8. Validation and Error Contract

- `400 Bad Request`: malformed JSON, missing/invalid fields, invalid enum, invalid role/scope combination.
- `401 Unauthorized`: missing or invalid authentication.
- `403 Forbidden`: authenticated user lacks role or geographic scope.
- `404 Not Found`: region, country, or record does not exist.
- `409 Conflict`: duplicate username/slug, or application is no longer pending for review.
- `413 Payload Too Large`: request body exceeds configured limit.
- `429 Too Many Requests`: configured public submission limit exceeded.
- `500 Internal Server Error`: generic safe message; details only in server logs with a request/correlation ID.

Return field errors in a stable optional form, for example `{ "message": "Validation failed", "errors": { "email": "Must be a valid email" } }`. Keep `message` present because the current React helper reads it.

## 9. Security, Privacy, and Operations

- Require HTTPS outside local development.
- Do not commit production secrets or use the starter's local default database credentials in deployed environments.
- Passwords must be salted and adaptively hashed. Apply least privilege to database credentials.
- Enforce role and region/country access in service/security layers, not only controllers or UI.
- Restrict CORS to known frontend origins; allow required JSON and authorization headers only. Configure the local Vite origin for development.
- If using HTTP Basic to match the current React client, recognize that browser `sessionStorage` contains a Base64-encoded credential, not an encrypted token. Prefer a secure `HttpOnly`, `Secure`, `SameSite` session cookie or short-lived access token with refresh strategy before production; update the React auth helper at the same time. Avoid localStorage for credentials.
- Protect public forms with body-size limits, rate limits, input validation, and safe output handling. Decide retention/access policy for applicant PII and contact messages before production.
- Log authentication and administrative actions without passwords, authorization headers, or unnecessary applicant/message content. Record reviewer, record, transition, and timestamp for application decisions.
- Configure readiness/liveness health checks, structured logs, and backup/restore for MySQL.

## 10. Acceptance Criteria

1. A clean database can be migrated and seeded with configured regions/countries; public endpoints return the documented JSON shapes.
2. The frontend can list regions, view a region's countries, list countries for forms, and render each country's public posts, including an empty-post state.
3. Anonymous application and contact submissions are validated, persisted, and return safe confirmations.
4. Anonymous requests to protected endpoints return `401`; authenticated users lacking permission return `403`.
5. Country reps can post only to their assigned country; coordinators can post and review applications only within their region; super admins can work network-wide.
6. Posts appear on their country page with category, author display name, and creation date.
7. Application status transitions, duplicate usernames, and unknown slugs/IDs produce the documented statuses.
8. Passwords are never persisted or returned in plaintext, and public APIs never expose applicant/message data.
9. Unit/integration tests cover validation, endpoint status codes, data shapes, authentication, role scoping, and region scoping.
10. Frontend and backend use the same `/api/v1` prefix and configured CORS policy in local development and deployment.

## 11. Suggested Implementation Order

1. Confirm API version and authentication approach; add database migrations and region/country seed data.
2. Implement public region/country endpoints and response DTOs.
3. Implement password storage, authentication, `/me`, and role/scope authorization; bootstrap the first super admin safely.
4. Implement posts and country-page responses.
5. Implement application and contact submission endpoints with validation and abuse controls.
6. Implement scoped application review, message inbox, and super-admin user creation.
7. Add frontend API prefix/auth integration, CORS configuration, end-to-end tests, and deployment configuration.

## 12. Open Decisions

Resolve these before production; reasonable MVP defaults are suggested.

1. **Authentication:** Existing frontend uses HTTP Basic on every request. Prefer secure cookie sessions or short-lived tokens for production; choose the intended approach and update frontend/backend together.
2. **Applicant review detail:** Current UI supports only approve/reject. Confirm whether coordinators may see full motivation and email, whether records can be reopened, and whether applicant email notifications are required. MVP default: scoped coordinators and super admins see details; only pending records can transition once.
3. **Application duplicates and retention:** Define duplicate policy and how long rejected/pending applicant data is kept. MVP default: accept multiple submissions, rate-limit, document retention before production.
4. **Region/country data ownership:** Confirm official region groupings, display order, country list, ISO codes, and who may change reference data. MVP default: managed through migrations/seed data, not a public admin UI.
5. **Content workflow:** Confirm whether posts publish immediately and whether edits, deletion, moderation, or future-dated events are needed. MVP default: authorized post creation publishes immediately; no edit/delete API because the frontend does not expose it.
6. **Contact-message workflow:** Current UI only lists messages. Confirm read/archive/delete actions, notifications, and retention. MVP default: super-admin read-only inbox, newest first.
7. **Personal data and legal requirements:** Confirm privacy notice, consent language, data residency, deletion requests, and applicable regional privacy requirements before public launch.
