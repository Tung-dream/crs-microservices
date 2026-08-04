# BLUEPRINT API

## 1. auth-service (Port 8081)
- POST /auth/login (Public)
- POST /auth/register (Public)

## 2. course-service (Port 8082)
- GET /courses (Public)
- GET /courses/{id} (Public)
- POST /courses (ADMIN)
- PUT /courses/{id} (ADMIN)
- DELETE /courses/{id} (ADMIN)
- PATCH /internal/courses/{id}/reserve-seat (Internal)
- PATCH /internal/courses/{id}/release-seat (Internal)

## 3. registration-service (Port 8083)
- POST /registrations (STUDENT)
- GET /registrations/my (STUDENT)
- DELETE /registrations/{id} (STUDENT/ADMIN)