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

**Database:** PostgreSQL 16 with 8 normalized tables

## Running locally

### Backend
