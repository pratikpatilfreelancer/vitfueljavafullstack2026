package vanguard;

import vanguard.ui.MainFrame;
import vanguard.ui.Theme;

import javax.swing.SwingUtilities;


public class SwingMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.applyGlobalDefaults();
            new MainFrame().setVisible(true);
        });
    }
}
