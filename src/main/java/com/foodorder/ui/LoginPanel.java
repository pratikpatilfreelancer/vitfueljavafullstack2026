package com.foodorder.ui;

import com.foodorder.spring.client.SpringApiClient;
import com.foodorder.model.Admin;
import com.foodorder.model.Customer;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final AppFrame appFrame;
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JRadioButton customerRadio = new JRadioButton("Customer", true);
    private final JRadioButton adminRadio = new JRadioButton("Admin");

    public LoginPanel(AppFrame appFrame) {
        this.appFrame = appFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("🍔 Online Food Ordering System");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, gbc);

        gbc.gridy++;
        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        add(subtitle, gbc);

        ButtonGroup group = new ButtonGroup();
        group.add(customerRadio);
        group.add(adminRadio);
        JPanel radioPanel = new JPanel();
        radioPanel.add(customerRadio);
        radioPanel.add(adminRadio);
        gbc.gridy++;
        add(radioPanel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(loginBtn, gbc);

        JButton registerBtn = new JButton("New customer? Register here");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setForeground(new Color(0, 102, 204));
        registerBtn.addActionListener(e -> new RegisterDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true));
        gbc.gridy++;
        add(registerBtn, gbc);

        JLabel hint = new JLabel("<html><i>Default admin login: admin@food.com / admin123</i></html>");
        hint.setForeground(Color.GRAY);
        gbc.gridy++;
        add(hint, gbc);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.",
                    "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (customerRadio.isSelected()) {
                Customer customer = SpringApiClient.login(email, password);
                appFrame.showCustomerDashboard(customer);
            } else {
                Admin admin = SpringApiClient.adminLogin(email, password);
                appFrame.showAdminDashboard(admin);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Login failed. Make sure the Spring Boot server is running on port 8080.\n\n" + ex.getMessage(),
                    "Login failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
