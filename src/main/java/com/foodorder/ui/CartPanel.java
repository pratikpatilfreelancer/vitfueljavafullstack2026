package com.foodorder.ui;

import com.foodorder.model.Cart;
import com.foodorder.model.CartItem;
import com.foodorder.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CartPanel extends JPanel {
    private final Cart cart;
    private final Customer customer;
    private final Runnable onOrderPlaced;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Item", "Price (₹)", "Qty", "Subtotal (₹)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 2; // quantity editable
        }
    };
    private final JTable table = new JTable(model);
    private final JLabel subtotalLabel = new JLabel();
    private final JLabel discountLabel = new JLabel();
    private final JLabel taxLabel = new JLabel();
    private final JLabel deliveryLabel = new JLabel();
    private final JLabel totalLabel = new JLabel();

    public CartPanel(Cart cart, Customer customer, Runnable onOrderPlaced) {
        this.cart = cart;
        this.customer = customer;
        this.onOrderPlaced = onOrderPlaced;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        model.addTableModelListener(e -> {
            if (e.getColumn() == 2) {
                int row = e.getFirstRow();
                if (row >= 0 && row < cart.getItems().size()) {
                    try {
                        int newQty = Integer.parseInt(model.getValueAt(row, 2).toString());
                        CartItem item = cart.getItems().get(row);
                        cart.updateQuantity(item.getFoodItem().getId(), newQty);
                        refresh();
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        });

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

        JPanel totalsPanel = new JPanel(new GridLayout(5, 1));
        totalsPanel.add(subtotalLabel);
        totalsPanel.add(discountLabel);
        totalsPanel.add(taxLabel);
        totalsPanel.add(deliveryLabel);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalsPanel.add(totalLabel);
        south.add(totalsPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.addActionListener(e -> removeSelected());
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.addActionListener(e -> checkout());
        buttonPanel.add(removeBtn);
        buttonPanel.add(checkoutBtn);
        south.add(buttonPanel);

        add(south, BorderLayout.SOUTH);
        refresh();
    }

    public void refresh() {
        model.setRowCount(0);
        for (CartItem item : cart.getItems()) {
            model.addRow(new Object[]{
                    item.getFoodItem().getName(),
                    String.format("%.2f", item.getFoodItem().getPrice()),
                    item.getQuantity(),
                    String.format("%.2f", item.getSubtotal())
            });
        }
        subtotalLabel.setText("Subtotal: ₹" + String.format("%.2f", cart.getSubtotal()));
        discountLabel.setText("Discount: -₹" + String.format("%.2f", cart.getDiscount()));
        taxLabel.setText("Tax (5%): ₹" + String.format("%.2f", cart.getTax()));
        deliveryLabel.setText("Delivery Fee: ₹" + String.format("%.2f", cart.getDeliveryFee()));
        totalLabel.setText("TOTAL: ₹" + String.format("%.2f", cart.getTotal()));
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        CartItem item = cart.getItems().get(row);
        cart.removeItem(item.getFoodItem().getId());
        refresh();
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Nothing to checkout",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        CheckoutDialog dialog = new CheckoutDialog(SwingUtilities.getWindowAncestor(this), cart, customer);
        dialog.setVisible(true);
        if (dialog.wasOrderPlaced()) {
            cart.clear();
            refresh();
            onOrderPlaced.run();
        }
    }
}
