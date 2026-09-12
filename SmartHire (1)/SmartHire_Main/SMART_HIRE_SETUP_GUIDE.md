# SmartHire - Spring Boot + JDBC + MySQL

Your original SmartHire frontend files have been preserved. Spring Boot/JDBC/MySQL support has been added alongside them.

## Requirements
- JDK 21
- MySQL 8+
- Maven (or Maven Wrapper if you add one)
- VS Code

## 1. Create database
Open MySQL Workbench and run:
database/smarthire_database.sql

## 2. Configure MySQL
Edit:
src/main/resources/smarthire-db.properties

Change:
spring.datasource.password=CHANGE_ME

to your actual MySQL root password.

## 3. Run
From the folder containing pom.xml:

mvn spring-boot:run

Then open:
http://localhost:8080

The existing `web` folder is served as static content.

## 4. Test JDBC
Open:
http://localhost:8080/api/health

Expected:
{"status":"OK","database":"CONNECTED"}

## Important
The existing frontend's localStorage/data.js has NOT been deleted. This package adds the Spring Boot/JDBC/MySQL foundation without removing your original UI. To move each existing CRUD operation from localStorage into MySQL, connect the existing JavaScript calls to REST controllers/DAO methods for each module.
