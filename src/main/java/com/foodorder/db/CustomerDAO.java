package com.foodorder.db;

import com.foodorder.exception.InvalidLoginException;
import com.foodorder.model.Admin;
import com.foodorder.model.Customer;

import java.sql.*;

public class CustomerDAO {

    public void register(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (name, email, password, phone, address) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPassword());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) customer.setId(keys.getInt(1));
            }
        }
    }

    public Customer login(String email, String password) throws SQLException, InvalidLoginException {
        String sql = "SELECT * FROM customer WHERE email=? AND password=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("phone"),
                            rs.getString("address")
                    );
                }
            }
        }
        throw new InvalidLoginException("Invalid email or password.");
    }

    public Admin adminLogin(String email, String password) throws SQLException, InvalidLoginException {
        String sql = "SELECT * FROM admin WHERE email=? AND password=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("admin_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                }
            }
        }
        throw new InvalidLoginException("Invalid admin credentials.");
    }
}
