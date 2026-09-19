package com.foodorder.ui;

import com.foodorder.spring.FoodOrderApplication;
import org.springframework.boot.SpringApplication;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SpringApplication.run(FoodOrderApplication.class);
        // Use the system look-and-feel so the Swing UI looks native, not like default grey Java UI.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new AppFrame().setVisible(true));
    }
}
