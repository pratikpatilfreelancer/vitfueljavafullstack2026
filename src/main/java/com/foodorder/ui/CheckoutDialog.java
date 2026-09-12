package com.foodorder.ui;

import com.foodorder.spring.client.SpringApiClient;
import com.foodorder.model.*;
import com.foodorder.payment.CardPayment;
import com.foodorder.payment.CashPayment;
import com.foodorder.payment.Payment;
import com.foodorder.payment.UPIPayment;

import javax.swing.*;
import java.awt.*;

public class CheckoutDialog extends JDialog {
    private final Cart cart;
    private final Customer customer;
    private boolean orderPlaced = false;

    private final JRadioButton cashOption = new JRadioButton("Cash on Delivery", true);
    private final JRadioButton cardOption = new JRadioButton("Card");
    private final JRadioButton upiOption = new JRadioButton("UPI");
    private final JTextField detailField = new JTextField(20);

    public CheckoutDialog(Window owner, Cart cart, Customer customer) {
        super(owner, "Checkout", ModalityType.APPLICATION_MODAL);
        this.cart = cart;
        this.customer = customer;

        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel totalLabel = new JLabel("Amount to pay: ₹" + String.format("%.2f", cart.getTotal()));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        add(totalLabel, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        ButtonGroup group = new ButtonGroup();
        group.add(cashOption);
        group.add(cardOption);
        group.add(upiOption);
        center.add(cashOption);
        center.add(cardOption);
        center.add(upiOption);

        JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        detailPanel.add(new JLabel("Card number / UPI ID (if applicable):"));
        detailPanel.add(detailField);
        center.add(detailPanel);

        add(center, BorderLayout.CENTER);

        JButton payBtn = new JButton("Pay & Place Order");
        payBtn.addActionListener(e -> placeOrder());
        add(payBtn, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private void placeOrder() {
        Payment payment;
        if (cardOption.isSelected()) {
            payment = new CardPayment(detailField.getText().trim());
        } else if (upiOption.isSelected()) {
            payment = new UPIPayment(detailField.getText().trim());
        } else {
            payment = new CashPayment();
        }

        if (!payment.processPayment(cart.getTotal())) {
            JOptionPane.showMessageDialog(this,
                    "Payment could not be processed. Please check the details entered.",
                    "Payment failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Order order = new Order(customer.getId(), cart.getTotal(), "PLACED", payment.getMethodName());
        for (CartItem item : cart.getItems()) {
            order.addOrderItem(new OrderItem(
                    item.getFoodItem().getId(),
                    item.getFoodItem().getName(),
                    item.getQuantity(),
                    item.getFoodItem().getPrice()
            ));
        }

        try {
            Order placedOrder = SpringApiClient.placeOrder(customer.getId(), payment.getMethodName(), order.getOrderItems());
            orderPlaced = true;
            JOptionPane.showMessageDialog(this,
                    "Order #" + placedOrder.getOrderId() + " placed successfully via " + payment.getMethodName() + "!\n" +
                            "Estimated delivery: 30-45 minutes.",
                    "Order confirmed", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not place the order.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean wasOrderPlaced() {
        return orderPlaced;
    }
}
