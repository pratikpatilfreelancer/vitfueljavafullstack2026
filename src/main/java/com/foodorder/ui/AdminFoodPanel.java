package com.foodorder.ui;

import com.foodorder.spring.client.SpringApiClient;
import com.foodorder.model.Category;
import com.foodorder.model.FoodItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminFoodPanel extends JPanel {
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Name", "Category", "Price", "Qty", "Available"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private List<FoodItem> currentItems;

    private final JTextField nameField = new JTextField(15);
    private final JComboBox<Category> categoryBox = new JComboBox<>(Category.values());
    private final JTextField priceField = new JTextField(8);
    private final JTextField qtyField = new JTextField(6);
    private final JCheckBox availableBox = new JCheckBox("Available", true);

    private Integer editingFoodId = null;

    public AdminFoodPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedIntoForm());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Category:"));
        form.add(categoryBox);
        form.add(new JLabel("Price:"));
        form.add(priceField);
        form.add(new JLabel("Qty:"));
        form.add(qtyField);
        form.add(availableBox);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add New");
        addBtn.addActionListener(e -> addFood());
        JButton updateBtn = new JButton("Update Selected");
        updateBtn.addActionListener(e -> updateFood());
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteFood());
        JButton clearBtn = new JButton("Clear Form");
        clearBtn.addActionListener(e -> clearForm());
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(form);
        south.add(buttons);
        add(south, BorderLayout.SOUTH);

        refresh();
    }

    private void refresh() {
        try {
            currentItems = SpringApiClient.getFood("");
            model.setRowCount(0);
            for (FoodItem item : currentItems) {
                model.addRow(new Object[]{
                        item.getId(), item.getName(), item.getCategory(),
                        item.getPrice(), item.getQuantity(), item.isAvailability()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not load food list.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0 || currentItems == null) return;
        FoodItem item = currentItems.get(row);
        editingFoodId = item.getId();
        nameField.setText(item.getName());
        categoryBox.setSelectedItem(item.getCategory());
        priceField.setText(String.valueOf(item.getPrice()));
        qtyField.setText(String.valueOf(item.getQuantity()));
        availableBox.setSelected(item.isAvailability());
    }

    private void clearForm() {
        editingFoodId = null;
        nameField.setText("");
        priceField.setText("");
        qtyField.setText("");
        availableBox.setSelected(true);
        table.clearSelection();
    }

    private FoodItem buildFoodFromForm() throws NumberFormatException {
        String name = nameField.getText().trim();
        Category category = (Category) categoryBox.getSelectedItem();
        double price = Double.parseDouble(priceField.getText().trim());
        int qty = Integer.parseInt(qtyField.getText().trim());
        boolean available = availableBox.isSelected();
        return new FoodItem(editingFoodId == null ? 0 : editingFoodId, name, category, price, qty, available);
    }

    private void addFood() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a food name.", "Missing name", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            FoodItem item = buildFoodFromForm();
            item.setId(0);
            SpringApiClient.addFood(item);
            refresh();
            clearForm();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Price and quantity must be numbers.", "Invalid input",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not add food item.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFood() {
        if (editingFoodId == null) {
            JOptionPane.showMessageDialog(this, "Select a row to update first.", "No selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            FoodItem item = buildFoodFromForm();
            SpringApiClient.updateFood(item);
            refresh();
            clearForm();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Price and quantity must be numbers.", "Invalid input",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not update food item.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFood() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete first.", "No selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this food item?", "Confirm delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            SpringApiClient.deleteFood(currentItems.get(row).getId());
            refresh();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not delete food item.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
