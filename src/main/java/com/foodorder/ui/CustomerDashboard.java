package com.foodorder.ui;

import com.foodorder.model.Cart;
import com.foodorder.model.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JPanel {
    private final Cart cart = new Cart();
    private final JTabbedPane tabs = new JTabbedPane();
    private CartPanel cartPanel;
    private OrderHistoryPanel historyPanel;

    public CustomerDashboard(AppFrame appFrame, Customer customer) {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel welcome = new JLabel("Welcome, " + customer.getName() + "!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.add(welcome, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> appFrame.showLogin());
        header.add(logoutBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        MenuPanel menuPanel = new MenuPanel(cart, () -> {
            cartPanel.refresh();
            tabs.setTitleAt(1, "Cart (" + cart.getItems().size() + ")");
        });
        cartPanel = new CartPanel(cart, customer, () -> {
            historyPanel.refresh();
            tabs.setTitleAt(1, "Cart (0)");
            tabs.setSelectedIndex(2);
        });
        historyPanel = new OrderHistoryPanel(customer);

        tabs.addTab("Menu", menuPanel);
        tabs.addTab("Cart (0)", cartPanel);
        tabs.addTab("My Orders", historyPanel);

        add(tabs, BorderLayout.CENTER);
    }
}
