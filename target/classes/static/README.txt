FoodExpress Frontend

Files:
- index.html
- style.css
- app.js

Spring Boot integration:
Place these three files inside:
src/main/resources/static/

Then run:
mvn spring-boot:run

Open:
http://localhost:8080/

The frontend calls:
GET  /api/food
POST /api/auth/login
