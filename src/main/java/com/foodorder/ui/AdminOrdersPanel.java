package com.foodorder.ui;

import com.foodorder.spring.client.SpringApiClient;
import com.foodorder.model.Order;
import com.foodorder.model.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminOrdersPanel extends JPanel {
    private static final String[] STATUSES = {"PLACED", "PREPARING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"};

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Order ID", "Customer ID", "Date", "Items", "Total (₹)", "Status", "Payment"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private List<Order> currentOrders;
    private final JComboBox<String> statusBox = new JComboBox<>(STATUSES);

    public AdminOrdersPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        bottom.add(refreshBtn);
        bottom.add(new JLabel("Set status:"));
        bottom.add(statusBox);
        JButton updateBtn = new JButton("Update Status");
        updateBtn.addActionListener(e -> updateStatus());
        bottom.add(updateBtn);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    private void refresh() {
        try {
            currentOrders = SpringApiClient.getAllOrders();
            model.setRowCount(0);
            for (Order o : currentOrders) {
                StringBuilder items = new StringBuilder();
                for (OrderItem item : o.getOrderItems()) {
                    items.append(item.getFoodName()).append(" x").append(item.getQuantity()).append(", ");
                }
                String itemsStr = items.length() > 0 ? items.substring(0, items.length() - 2) : "";
                model.addRow(new Object[]{
                        o.getOrderId(), o.getCustomerId(), o.getOrderDate(), itemsStr,
                        String.format("%.2f", o.getTotalAmount()), o.getStatus(), o.getPaymentMethod()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not load orders.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first.", "No selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Order order = currentOrders.get(row);
        String newStatus = (String) statusBox.getSelectedItem();
        try {
            SpringApiClient.updateOrderStatus(order.getOrderId(), newStatus);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not update status.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
