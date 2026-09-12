# 💖 HeartMate - Retro Pixel Matrimonial Platform

HeartMate is a fullstack retro pixel-art matrimonial web application built with **Spring Boot 3 (JPA/Hibernate + MySQL)** on the backend and **Next.js 16 (React + TypeScript + TailwindCSS)** on the frontend.

---

## 🌟 Key Features

- **Retro Pixel UI**: Pixelated aesthetic featuring retro fonts, custom avatars, and animated Cupids.
- **Physics-Driven Cupids**: Drag & hover interaction on Cupids with elastic spring physics.
- **Interactive Match Experience**:
  - Side-by-side profile comparison ("You" vs "Them").
  - Delayed love-letter (💌) notification pop-up.
  - Confetti explosions & real-time HTML5 Canvas Chroma Keying to remove green screen from dancing cat videos (`/cats.mp4`).
- **Profile Detail Modal**: View complete candidate bio-data, education, occupation, and contact details.
- **Database-Connected Interest Management**: Send, view, accept, or reject interests with real-time updates saved to MySQL.
- **User Registration & Profile Creation**: Create user accounts and construct customized candidate profiles.
- **OpenAPI / Swagger Integration**: Interactive API testing available at `http://localhost:8080/swagger-ui/index.html`.

---

## 🛠️ Tech Stack

### **Backend**
- **Framework:** Spring Boot 3.2.0 (Java 21)
- **Persistence:** Spring Data JPA / Hibernate
- **Database:** MySQL
- **Documentation:** SpringDoc OpenAPI 3.0 (Swagger UI)

### **Frontend**
- **Framework:** Next.js 16.3.4 (App Router)
- **Styling:** CSS3 (Pixel Art Design System)
- **Animations:** HTML5 Canvas Chroma Keying & `canvas-confetti`

---

## 🚀 Getting Started

### 1. Backend Setup (Spring Boot)
Ensure MySQL is running locally on port 3306 with database `matrimonial_jpa_db`.

```bash
cd backend
mvn spring-boot:run
```
The server runs on `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 2. Frontend Setup (Next.js)

```bash
cd frontend
npm install
npm run dev
```
The client runs on `http://localhost:3000`.

---

## 📡 REST API Endpoints Overview

| Module | Method | Endpoint | Description |
| :--- | :---: | :--- | :--- |
| **Users** | `GET` | `/api/users` | List all users |
| **Users** | `POST` | `/api/users` | Register new user |
| **Profiles** | `GET` | `/api/profiles/search` | Search/filter profiles |
| **Profiles** | `POST` | `/api/profiles/user/{userId}` | Create candidate profile |
| **Interests** | `POST` | `/api/interests/send` | Send interest to candidate |
| **Interests** | `GET` | `/api/interests/sent/{senderId}` | Retrieve sent interests |
| **Interests** | `GET` | `/api/interests/received/{receiverId}` | Retrieve received interests |
| **Interests** | `PUT` | `/api/interests/{id}/accept` | Accept interest (match) |

---

## 📄 License
MIT License
