# EventCloud — Cloud-Based Event Management System

EventCloud adapts the TeamPulse Spring Boot/React foundation into a cloud-ready event platform aligned with the supplied Cloud Computing project presentation.

## Implemented scope
- Public account registration and JWT login; registration always creates a `USER`.
- `USER`, `ORGANIZER`, and `ADMIN` roles. Organizer/Admin role elevation is deliberately not exposed to public registration.
- Organizer-owned event drafts, publishing, editing, cancellation, and capacity.
- Public published-event discovery and user registration.
- Capacity-safe registration using a PostgreSQL pessimistic row lock and transaction, plus a unique `(event_id, user_id)` database constraint.
- Unique ticket codes, QR image endpoint, registration cancellation, organizer check-in with timestamps.
- In-app notifications and event/registration/attendance/capacity summary analytics.
- Poster upload endpoint and local development file storage.
- Dockerfile, PostgreSQL Docker Compose setup, environment-based configuration.

## Stack
- Java 17, Spring Boot 3.2.3, Spring Security, JWT, Spring Data JPA, Bean Validation
- PostgreSQL
- React 18 + Vite, React Router, Axios
- ZXing QR generation
- Docker

## Run locally
Requirements: Java 17, Maven 3.8+, Node 18+, PostgreSQL 14+.

### 1. Database
Create a PostgreSQL database:
```sql
CREATE DATABASE eventcloud;
```
Configure `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` if the defaults do not match your local database.

### 2. Backend
```bash
cd backend
# Windows PowerShell: set a strong secret before running
$env:JWT_SECRET = "replace-with-a-random-secret-at-least-32-bytes-long"
mvn spring-boot:run
```
API runs at `http://localhost:8080`. Set `FRONTEND_ORIGIN=http://localhost:3000
# Optional cloud object storage
STORAGE_MODE=local
S3_BUCKET=
AWS_REGION=ap-south-1` if needed.

### 3. Frontend
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:3000`. To use a deployed backend, set `VITE_API_URL=https://your-api-host/api` before building.

## Docker
At the repository root:
```bash
cp .env.example .env
# Replace every sample secret in .env before use
docker compose up --build
```
The backend will listen on port 8080. The compose setup starts PostgreSQL and persists the database and uploaded files in named volumes. The React development app is run separately using the steps above; deploy the built `frontend/dist` behind a web server or static hosting service.

## Important setup: create the first organizer/admin
For security, public registration cannot set elevated roles. After registering the first account, elevate it using a trusted database admin connection, for example:
```sql
UPDATE app_users SET role = 'ADMIN' WHERE email = 'your-admin@example.com';
```
Organizer accounts can similarly be set to `ORGANIZER`. Do not expose this SQL capability through an unauthenticated API.

## Main REST endpoints
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login / JWT |
| GET | `/api/events` | Published event discovery |
| GET | `/api/events/{id}` | Event details |
| POST | `/api/events` | Create draft (Organizer/Admin) |
| PUT | `/api/events/{id}` | Edit managed event |
| POST | `/api/events/{id}/publish` | Publish managed event |
| POST | `/api/events/{id}/cancel` | Cancel managed event |
| GET | `/api/events/managed` | Managed events |
| POST | `/api/events/{id}/registrations` | Register current user |
| GET | `/api/events/{id}/registrations` | Organizer attendee list |
| GET | `/api/registrations/mine` | Current user's tickets |
| DELETE | `/api/registrations/{id}` | Cancel own registration |
| POST | `/api/registrations/check-in` | Organizer validates a ticket code |
| GET | `/api/tickets/{registrationId}/qr` | QR PNG (authenticated) |
| GET | `/api/notifications` | Current user's notifications |
| GET | `/api/analytics/summary` | Organizer/admin analytics |
| POST | `/api/events/{id}/poster` | Upload JPEG/PNG/WebP poster |

## Cloud object storage
Poster uploads use a storage interface with two implementations:
- `STORAGE_MODE=local` (default): saves to `UPLOAD_DIR` and serves `/uploads/...`, suitable for local development.
- `STORAGE_MODE=s3`: uploads to AWS S3 using the AWS SDK default credential chain and stores the object URL on the event. Set `S3_BUCKET` and `AWS_REGION`; supply credentials via an instance role or the standard AWS credential chain. Never commit cloud credentials. The S3 bucket must allow the intended poster-read access (or replace public object URLs with signed URLs for private buckets).

## Cloud VM deployment checklist
1. Provision a VM and managed PostgreSQL or a secured PostgreSQL instance.
2. Open only required ports; prefer HTTPS through a reverse proxy.
3. Build and run the backend container, set DB/JWT/frontend-origin variables, and persist uploads.
4. Build the frontend with `VITE_API_URL` set to the HTTPS API URL and serve `dist`.
5. Configure cloud object storage for posters before using multiple backend instances.
6. Replace `ddl-auto=update` with a schema migration tool such as Flyway before production.
7. Add integration tests, structured audit logs, backups, rate limiting, email/SMS notifications, and monitoring before real-world use.

## Notes / limitations
- In-app notifications are persisted, but email/SMS delivery is not implemented.
- The frontend includes a ticket-code check-in flow; it does not yet use the device camera to scan QR codes.
- Poster upload uses local storage only until a provider-specific object-storage adapter is implemented.
- This is an academic project starter, not a security-audited production service.
