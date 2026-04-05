# Construction Site Labour & Wage Management System

A full-stack web application that digitizes daily wage 
worker attendance, automates wage calculation, and 
generates legally-compliant wage slips for Indian 
construction sites.

## Problem it solves
50 million daily wage construction workers in India 
have no digital attendance records, no payslips, and 
are routinely underpaid. This system fixes that.

## Features
- QR code based attendance marking
- HMAC-SHA256 signed QR codes — tamper proof
- Offline attendance sync when internet returns
- Automated wage calculation with PF and ESI deductions
- State-wise minimum wage compliance checking
- PDF wage slip generation (Form XIV compliant)
- Role-based access — Admin, Contractor, Supervisor, Worker

## Tech Stack
**Backend:** Java 17, Spring Boot 3, Spring Security, 
JWT, PostgreSQL, Flyway, ZXing, Apache PDFBox

**Frontend:** React 18, Vite, Tailwind CSS, 
React Router, Axios

## Live Demo (Coming Soon)
Frontend: http://localhost:5173  
Backend: http://localhost:9999

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/auth/register | Register contractor |
| POST | /api/v1/auth/login | Login |
| POST | /api/v1/workers/register | Register worker |
| GET  | /api/v1/workers/site/{id} | Get site workers |
| GET  | /api/v1/workers/{id}/qr | Download QR code |
| POST | /api/v1/attendance/mark | Mark attendance |
| POST | /api/v1/attendance/sync | Batch sync offline |
| GET  | /api/v1/attendance/site/{id}/today | Today's attendance |
| POST | /api/v1/wage-slips/generate/{id}/{y}/{m} | Generate slip |
| GET  | /api/v1/wage-slips/pdf/{id} | Download PDF |

## Database Schema

The system uses 8 core tables:

- contractors
- sites
- users
- workers
- attendance
- wage_slips
- disputes
- min_wage_rules

## Running Locally

### Prerequisites
- Java 17
- PostgreSQL 16
- Node.js 20

### Backend Setup
```bash
# Create database
psql -U postgres
CREATE DATABASE labour_wage_db;
CREATE USER labour_admin WITH PASSWORD 'labour123';
GRANT ALL PRIVILEGES ON DATABASE labour_wage_db TO labour_admin;

# Run Spring Boot
cd labour-wage-system
./mvnw spring-boot:run
```

### Frontend Setup
```bash
# Switch to frontend branch
git checkout frontend

# Install and run
npm install
npm run dev
```
### Sample Request

POST /api/v1/auth/login

Request:
{
  "username": "contractor1",
  "password": "password123"
}

Response:
{
  "token": "jwt_token_here"
}

Open http://localhost:5173

## Architecture Highlights
- Flyway manages all DB migrations
- JWT tokens for stateless authentication
- HMAC-SHA256 signed QR codes prevent forgery
- UPSERT queries ensure idempotent sync
- PDFBox generates Form XIV compliant wage slips
- Vite proxy forwards API calls to Spring Boot

- This system follows a RESTful architecture with a stateless backend using JWT authentication and a decoupled React frontend.

**Database:** PostgreSQL 16 with 8 normalized tables

## Running locally

### Backend
