package com.foodorder.db;

import com.foodorder.model.Category;
import com.foodorder.model.FoodItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodDAO {

    public List<FoodItem> getAllFood() throws SQLException {
        List<FoodItem> list = new ArrayList<>();
        String sql = "SELECT * FROM food ORDER BY category, name";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<FoodItem> searchFood(String keyword) throws SQLException {
        List<FoodItem> list = new ArrayList<>();
        String sql = "SELECT * FROM food WHERE name LIKE ? ORDER BY name";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void addFood(FoodItem food) throws SQLException {
        String sql = "INSERT INTO food (name, category, price, quantity, availability) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, food.getName());
            ps.setString(2, food.getCategory().name());
            ps.setDouble(3, food.getPrice());
            ps.setInt(4, food.getQuantity());
            ps.setBoolean(5, food.isAvailability());
            ps.executeUpdate();
        }
    }

    public void updateFood(FoodItem food) throws SQLException {
        String sql = "UPDATE food SET name=?, category=?, price=?, quantity=?, availability=? WHERE food_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, food.getName());
            ps.setString(2, food.getCategory().name());
            ps.setDouble(3, food.getPrice());
            ps.setInt(4, food.getQuantity());
            ps.setBoolean(5, food.isAvailability());
            ps.setInt(6, food.getId());
            ps.executeUpdate();
        }
    }

    public void deleteFood(int foodId) throws SQLException {
        String sql = "DELETE FROM food WHERE food_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, foodId);
            ps.executeUpdate();
        }
    }

    public void decrementStock(int foodId, int amount) throws SQLException {
        String sql = "UPDATE food SET quantity = quantity - ? WHERE food_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, foodId);
            ps.executeUpdate();
        }
    }

    private FoodItem mapRow(ResultSet rs) throws SQLException {
        return new FoodItem(
                rs.getInt("food_id"),
                rs.getString("name"),
                Category.valueOf(rs.getString("category")),
                rs.getDouble("price"),
                rs.getInt("quantity"),
                rs.getBoolean("availability")
        );
    }
}
