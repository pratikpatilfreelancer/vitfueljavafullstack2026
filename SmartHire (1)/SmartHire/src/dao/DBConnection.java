package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place for MySQL JDBC connectivity.
 *
 * Update DB_URL / DB_USER / DB_PASSWORD to match your local MySQL setup,
 * then run database/schema.sql (and optionally database/sample_data.sql)
 * before starting the application.
 */
public class DBConnection {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/smarthire?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found on classpath. "
                    + "Make sure lib/mysql-connector-j.jar is added to the build path.");
        }
    }

    private DBConnection() {
        // utility class, no instances
    }

    /**
     * Returns a fresh JDBC connection. Callers are responsible for closing it
     * (try-with-resources is used throughout the DAO layer).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /** Quick connectivity check used by the login screen at startup. */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
