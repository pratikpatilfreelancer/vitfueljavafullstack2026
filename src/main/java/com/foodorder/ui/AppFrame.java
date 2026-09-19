package com.foodorder.ui;

import com.foodorder.model.Admin;
import com.foodorder.model.Customer;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {
    public static final String LOGIN_CARD = "LOGIN";
    public static final String CUSTOMER_CARD = "CUSTOMER";
    public static final String ADMIN_CARD = "ADMIN";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel();

    private CustomerDashboard customerDashboard;
    private AdminDashboard adminDashboard;

    public AppFrame() {
        setTitle("Online Food Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);

        container.setLayout(cardLayout);
        container.add(new LoginPanel(this), LOGIN_CARD);
        container.add(new JPanel(), CUSTOMER_CARD); // placeholder, replaced on login
        container.add(new JPanel(), ADMIN_CARD);    // placeholder, replaced on login

        add(container);
    }

    public void showLogin() {
        cardLayout.show(container, LOGIN_CARD);
    }

    public void showCustomerDashboard(Customer customer) {
        customerDashboard = new CustomerDashboard(this, customer);
        container.add(customerDashboard, CUSTOMER_CARD);
        cardLayout.show(container, CUSTOMER_CARD);
    }

    public void showAdminDashboard(Admin admin) {
        adminDashboard = new AdminDashboard(this, admin);
        container.add(adminDashboard, ADMIN_CARD);
        cardLayout.show(container, ADMIN_CARD);
    }
}
