LIBRARY MANAGEMENT SYSTEM
=========================

Technology:
- Java
- Java Swing
- JDBC
- MySQL

MEMBER 1 COMPLETED WORK
=======================

1. Database connection using JDBC
2. Book model and DAO
3. Member model and DAO
4. Book Management GUI
5. Member Management GUI
6. Add Book
7. Delete Book
8. Add Member
9. Delete Member
10. Automatic display of database records
11. Dashboard

DATABASE
========

Database name:
library_db

Tables:
books
members

BOOKS TABLE
===========

book_id
book_name
author
category
quantity

MEMBERS TABLE
=============

member_id
name
email
phone


HOW TO RUN
==========

1. Open MySQL and make sure MySQL Server is running.

2. Create the database and tables.

3. Check the MySQL username and password in:

src/util/DatabaseConnection.java

4. Make sure the MySQL Connector JAR is present inside:

lib/mysql-connector-j-26.7.0.jar

5. Open Terminal in the project root.

6. Compile:

rm -rf out
mkdir out
javac -cp "lib/*" -d out $(find src -name "*.java")

7. Run:

java -cp "out:lib/*" Main


IMPORTANT
=========

Do not run javac Main.java from inside the src folder.

Compile from the project root.

Member 1 modules are already tested and working.