package com.foodorder.db;

/**
 * EDIT THESE VALUES to match your local MySQL setup.
 * The default DB_NAME matches sql/schema.sql — run that script first
 * in MySQL Workbench (or `mysql -u root -p < sql/schema.sql`) before
 * starting the application.
 */
public class DatabaseConfig {
    public static final String HOST = "localhost";
    public static final String PORT = "3306";
    public static final String DB_NAME = "food_ordering_system";

    public static final String USERNAME = "root";
    public static final String PASSWORD = "System123";

    public static String getUrl() {
        return "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }
}
