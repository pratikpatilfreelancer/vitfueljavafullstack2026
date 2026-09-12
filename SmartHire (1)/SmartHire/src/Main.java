import dao.DBConnection;
import ui.LoginFrame;

import javax.swing.*;

/**
 * Application entry point. Verifies the MySQL connection is reachable (with
 * a friendly warning if not) and then launches the login screen.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // fall back to default look and feel
            }

            if (!DBConnection.testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to the MySQL database.\n\n"
                                + "Please check that MySQL is running and that the credentials in "
                                + "dao/DBConnection.java match your setup, then run database/schema.sql.",
                        "Database Connection Warning",
                        JOptionPane.WARNING_MESSAGE);
            }

            new LoginFrame().setVisible(true);
        });
    }
}
