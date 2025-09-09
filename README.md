# Stage Pass - Event Ticket Platform

![Java](https://img.shields.io/badge/Java-17-red?logo=openjdk)
![SpringBoot](https://img.shields.io/badge/Spring_Boot-3.0-brightgreen?logo=springboot)
![SpringSecurity](https://img.shields.io/badge/Security-Spring_Security-yellowgreen?logo=springsecurity)
![OAuth2](https://img.shields.io/badge/Auth-OAuth2-blueviolet?logo=keycloak)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue?logo=postgresql)
![React](https://img.shields.io/badge/Frontend-React-61DAFB?logo=react)
![TypeScript](https://img.shields.io/badge/Language-TypeScript-3178C6?logo=typescript)
![RESTAPI](https://img.shields.io/badge/API-REST-orange)
![JUnit](https://img.shields.io/badge/Testing-JUnit-green?logo=junit5)
![Tailwind](https://img.shields.io/badge/UI-Tailwind_CSS-38B2AC?logo=tailwindcss)

A full‑stack event ticketing system for organizers and attendees.

- Tech: Java, TypeScript, Spring Boot 3, Spring Security, OAuth2 (Keycloak), Lombok, PostgreSQL, React, REST API, JUnit, MapStruct, ZXing, Vite, Tailwind CSS
- Deployed at SFU’s 2025 Events, processing tickets for 100+ students.
- Secured organizer/attendee workflows with Spring Security and OAuth2.
- Designed REST APIs and PostgreSQL schema for event creation, ticket sales, and validation.

## Monorepo layout

```
backend/   # Spring Boot service (REST API, security, JPA, QR codes)
frontend/  # React + TypeScript app (Vite, Tailwind, OIDC)
```

Key highlights

- RBAC with realm roles (ROLE_ORGANIZER, ROLE_ATTENDEE, ROLE_STAFF)
- Organizer dashboard: create/update/publish events, manage ticket types
- Attendee flow: browse published events, purchase tickets, view tickets
- QR codes for tickets; validation via QR scan or manual entry
- MapStruct DTO mapping; ZXing for QR generation; JPA/Hibernate with PostgreSQL

## Architecture

High‑level flow

```
[React (Vite) UI]
   |  OIDC (react-oidc-context)
   v
[Keycloak  (JWT issuer)] ----> JWT (realm_access.roles => ROLE_*)
   |                                           |
   v                                           v
[Vite dev proxy /api]  ->  [Spring Boot Resource Server]
                             - SecurityConfig (RBAC)
                             - JwtAuthenticationConverter (ROLE_* mapping)
                             - UserProvisioningFilter (auto-create users)
                             - Controllers -> Services -> Repositories
                             - MapStruct mappers
                             - ZXing QR code service
                             - JPA/Hibernate -> PostgreSQL
```

Security

- Public: GET /api/v1/published-events/\*\*
- Organizer-only: /api/v1/events/\*\* (ROLE_ORGANIZER)
- Authenticated: all other API routes
- Roles are read from JWT claim realm*access.roles and must be prefixed with ROLE*

Data model (simplified)

- User
- Event (status: DRAFT, PUBLISHED, CANCELLED, COMPLETED)
- TicketType (name, price, totalAvailable)
- Ticket (status: PURCHASED, CANCELLED)
- QrCode (ACTIVE/…)
- TicketValidation (method: QR_SCAN/MANUAL; status: VALID/INVALID/EXPIRED)

## Backend (Spring Boot)

Notable code

- Security: `config/SecurityConfig.java`, `config/JwtAuthenticationConverter.java`
- Auto user provisioning: `filters/UserProvisioningFilter.java`
- QR codes: `config/QrCodeConfig.java`, `services/QrCodeService*`
- Controllers: `controllers/*` (events, published events, tickets, validations)
- Services: `services/impl/*`
- Persistence: `domain/entities/*`, `repositories/*`
- Mapping: `mappers/*` (MapStruct)

Configuration

- `src/main/resources/application.properties`
  - DB: `jdbc:postgresql://localhost:5432/postgres` (user postgres / password changemeinprod!)
  - OAuth2 issuer: `http://localhost:9090/realms/event-ticket-platform`
  - DDL auto: `update` (development)

Run

```bash
# prerequisites: Java 21, Maven, Docker
cd backend
./mvnw spring-boot:run
```

Tests

```bash
cd backend
./mvnw test
```

## Frontend (React + Vite)

Notable code

- OIDC setup: `src/main.tsx` (react-oidc-context)
- API client: `src/lib/api.ts`
- Domain types: `src/domain/domain.ts`
- Role utilities: `src/hooks/use-roles.tsx`
- Router pages: `src/pages/*`
- UI: Radix UI + Tailwind CSS
- Proxy: `vite.config.ts` forwards `/api` to `http://localhost:8080`

Run

```bash
# prerequisites: Node 20+
cd frontend
npm install
npm run dev
```

## Local development quickstart

1. Start infrastructure (PostgreSQL, Adminer, Keycloak)

```bash
cd backend
docker compose up -d
```

- PostgreSQL: localhost:5432 (user: postgres / password: changemeinprod!)
- Adminer: http://localhost:8888
- Keycloak: http://localhost:9090 (admin / admin)

2. Configure Keycloak (once)

- Create realm: `event-ticket-platform`
- Create realm roles: `ROLE_ORGANIZER`, `ROLE_ATTENDEE`, `ROLE_STAFF`
- Create public client: `event-ticket-platform-app`
  - Redirect URIs: `http://localhost:5173/callback`
  - Web origins: `http://localhost:5173`
- Create users and assign appropriate realm roles

3. Start backend

```bash
cd backend
./mvnw spring-boot:run
```

4. Start frontend

```bash
cd frontend
npm install
npm run dev
```

5. Try it

- Public landing: http://localhost:5173
- Organizer dashboard (requires ROLE_ORGANIZER): http://localhost:5173/dashboard
- Browse published events: list and details pages
- Purchase a ticket (ROLE_ATTENDEE)
- View ticket and QR code; validate via QR or manual code

## API overview

Published events (public)

- GET `/api/v1/published-events` — list (supports `?q=search&page=&size=`)
- GET `/api/v1/published-events/{eventId}` — details

Organizer events (ROLE_ORGANIZER)

- POST `/api/v1/events` — create
- GET `/api/v1/events` — list (pageable)
- GET `/api/v1/events/{eventId}` — get by id
- PUT `/api/v1/events/{eventId}` — update
- DELETE `/api/v1/events/{eventId}` — delete

Tickets (authenticated)

- POST `/api/v1/events/{eventId}/ticket-types/{ticketTypeId}/tickets` — purchase
- GET `/api/v1/tickets` — list current user tickets (pageable)
- GET `/api/v1/tickets/{ticketId}` — details
- GET `/api/v1/tickets/{ticketId}/qr-codes` — QR code image (image/png)

Ticket validation (authenticated)

- POST `/api/v1/ticket-validations` — body: `{ id: string, method: QR_SCAN | MANUAL }`

## Configuration notes

- Backend issuer URI must match Keycloak realm: `spring.security.oauth2.resourceserver.jwt.issuer-uri`
- Frontend OIDC client (`src/main.tsx`):
  - authority: `http://localhost:9090/realms/event-ticket-platform`
  - client_id: `event-ticket-platform-app`
  - redirect_uri: `http://localhost:5173/callback`
- Vite proxy in `vite.config.ts` forwards `/api` to `http://localhost:8080`

## Project structure

Backend

```
src/main/java/com/tickets
  ├─ config/            # Security, JWT converter, QR config
  ├─ controllers/       # REST endpoints
  ├─ domain/
  │   ├─ entities/      # JPA entities (Event, Ticket, TicketType, QrCode, ...)
  │   └─ dtos/          # DTOs
  ├─ exceptions/        # Domain exceptions
  ├─ filters/           # UserProvisioningFilter
  ├─ mappers/           # MapStruct mappers
  ├─ repositories/      # Spring Data JPA repos
  └─ services/impl/     # Business logic
```

Frontend

```
src/
  ├─ components/        # UI components (Radix based)
  ├─ domain/            # TypeScript domain models
  ├─ hooks/             # use-roles, etc.
  ├─ lib/               # api client, utils
  └─ pages/             # routes (landing, dashboard, tickets, validation)
```

## Tech choices

- Spring Boot 3 (Java 21), Spring Security (Resource Server)
- Keycloak for OAuth2/OIDC (JWT)
- MapStruct + Lombok for concise models and mapping
- PostgreSQL + Hibernate/JPA
- ZXing for QR code generation
- React + TypeScript, Vite, Tailwind, Radix UI, react-router

## License

See `LICENSE`.

---

Questions or ideas? Feel free to open an issue or suggest improvements.
