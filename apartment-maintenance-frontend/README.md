# Apartment Maintenance & Complaint Management System — Frontend

React frontend for the Apartment Maintenance & Complaint Management System, built as the capstone project for the Capgemini FUEL Full-Stack Java Development Program. This repository contains the client-side application; the Spring Boot REST API backend lives in a separate repository: [apartment-maintenance-and-complaint-management-system](https://github.com/brindhasuvarna2005-sudo/apartment-maintenance-and-complaint-management-system).

## Overview

A role-based UI for residents to raise maintenance complaints and for staff to manage them through a fixed status lifecycle (`OPEN → IN_PROGRESS → RESOLVED → CLOSED`), consuming the backend's REST API.

## Tech Stack

- **React.js**
- **HTML / CSS**
- REST API integration with the Spring Boot backend

## Getting Started

### Prerequisites
- Node.js and npm
- The [backend](https://github.com/brindhasuvarna2005-sudo/apartment-maintenance-and-complaint-management-system) running locally (this app expects it at `http://localhost:8080` by default)

### Setup
1. Clone the repo:
```bash
   git clone https://github.com/brindhasuvarna2005-sudo/apartment-maintenance-frontend.git
```
2. Install dependencies:
```bash
   npm install
```
3. Run the development server:
```bash
   npm start
```
4. The app will be available at `http://localhost:3000`.

> **Note:** Make sure the backend is running first — this app calls its API directly and won't function correctly without it.

## Author

Brindha Suvarna — [GitHub](https://github.com/brindhasuvarna2005-sudo)