# Online Food Ordering System — Java Swing + Spring Boot

This project combines the original Java Swing/MySQL application with a Spring Boot REST backend.

## Run the application

### Option A — Start the complete desktop application

From the project folder:

```powershell
mvn exec:java
```

`MainApp` starts the Spring Boot backend on `http://localhost:8080` and then opens the Swing UI. The Swing UI communicates with the backend through REST APIs for login, registration, food/menu operations, checkout, order history, and admin order updates.

### Option B — Start only the backend

```powershell
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

## REST endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/admin/login`
- `GET /api/food`
- `GET /api/food?search=pizza`
- `POST /api/food`
- `PUT /api/food/{id}`
- `DELETE /api/food/{id}`
- `POST /api/orders`
- `GET /api/orders/customer/{customerId}`
- `GET /api/orders`
- `PATCH /api/orders/{orderId}/status?status=DELIVERED`

## Database

Make sure MySQL is running and configure the existing `DatabaseConfig.java` credentials before using the application.
