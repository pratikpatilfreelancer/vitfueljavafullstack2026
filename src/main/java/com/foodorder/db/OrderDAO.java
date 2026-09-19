package com.foodorder.db;

import com.foodorder.model.Order;
import com.foodorder.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /** Inserts the order + its line items inside a single transaction. */
    public int placeOrder(Order order) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO orders (customer_id, order_date, total_amount, status, payment_method) VALUES (?,?,?,?,?)";
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getCustomerId());
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setDouble(3, order.getTotalAmount());
                ps.setString(4, order.getStatus());
                ps.setString(5, order.getPaymentMethod());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    orderId = keys.getInt(1);
                }
            }

            String itemSql = "INSERT INTO order_items (order_id, food_id, food_name, quantity, price) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                for (OrderItem item : order.getOrderItems()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getFoodId());
                    ps.setString(3, item.getFoodName());
                    ps.setInt(4, item.getQuantity());
                    ps.setDouble(5, item.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            FoodDAO foodDAO = new FoodDAO();
            for (OrderItem item : order.getOrderItems()) {
                foodDAO.decrementStock(item.getFoodId(), item.getQuantity());
            }

            conn.commit();
            return orderId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public List<Order> getOrdersByCustomer(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id=? ORDER BY order_date DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }
        for (Order o : orders) attachItems(o);
        return orders;
    }

    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapOrder(rs));
            }
        }
        for (Order o : orders) attachItems(o);
        return orders;
    }

    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status=? WHERE order_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    private void attachItems(Order order) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE order_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, order.getOrderId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    order.addOrderItem(new OrderItem(
                            rs.getInt("food_id"),
                            rs.getString("food_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    ));
                }
            }
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setPaymentMethod(rs.getString("payment_method"));
        return o;
    }
}
