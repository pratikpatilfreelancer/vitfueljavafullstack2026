package com.foodorder.ui;

import com.foodorder.model.Admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JPanel {
    public AdminDashboard(AppFrame appFrame, Admin admin) {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel welcome = new JLabel("Admin Panel — " + admin.getName());
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.add(welcome, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> appFrame.showLogin());
        header.add(logoutBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Food", new AdminFoodPanel());
        tabs.addTab("All Orders", new AdminOrdersPanel());
        add(tabs, BorderLayout.CENTER);
    }
}
