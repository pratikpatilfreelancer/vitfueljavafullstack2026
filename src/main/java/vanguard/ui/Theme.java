package vanguard.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Centralized color and font palette so every panel/component in the
 * GUI looks consistent instead of default Swing gray. Kept as plain
 * constants (no external look-and-feel library needed) so the project
 * still compiles and runs with nothing beyond the JDK.
 */
public final class Theme {

    private Theme() { }

    public static final Color BG = new Color(15, 18, 26);
    public static final Color PANEL = new Color(24, 29, 41);
    public static final Color PANEL_LIGHT = new Color(32, 38, 52);
    public static final Color BORDER = new Color(45, 52, 68);
    public static final Color TEXT = new Color(228, 233, 242);
    public static final Color TEXT_DIM = new Color(140, 149, 168);

    public static final Color CYAN = new Color(76, 201, 240);
    public static final Color GREEN = new Color(46, 213, 115);
    public static final Color AMBER = new Color(244, 161, 0);
    public static final Color ORANGE = new Color(255, 107, 53);
    public static final Color RED = new Color(230, 57, 70);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);

    public static Color severityColor(vanguard.model.Severity severity) {
        switch (severity) {
            case CRITICAL: return RED;
            case HIGH: return ORANGE;
            case MEDIUM: return AMBER;
            default: return GREEN;
        }
    }

    /**
     * Pushes the palette into Swing's UIManager defaults so built-in
     * components we didn't hand-style ourselves — JOptionPane dialogs,
     * scroll bars, table headers — pick up the same dark theme instead
     * of clashing with it.
     */
    public static void applyGlobalDefaults() {
        javax.swing.UIManager.put("Panel.background", PANEL);
        javax.swing.UIManager.put("OptionPane.background", PANEL);
        javax.swing.UIManager.put("OptionPane.messageForeground", TEXT);
        javax.swing.UIManager.put("Label.foreground", TEXT);
        javax.swing.UIManager.put("Label.font", FONT_BODY);
        javax.swing.UIManager.put("ScrollPane.background", PANEL);
        javax.swing.UIManager.put("Viewport.background", PANEL);
        javax.swing.UIManager.put("TextArea.background", PANEL_LIGHT);
        javax.swing.UIManager.put("TextArea.foreground", TEXT);
        javax.swing.UIManager.put("TextField.background", PANEL_LIGHT);
        javax.swing.UIManager.put("TextField.foreground", TEXT);
        javax.swing.UIManager.put("Spinner.background", PANEL_LIGHT);
        javax.swing.UIManager.put("Table.background", PANEL);
        javax.swing.UIManager.put("Table.foreground", TEXT);
        javax.swing.UIManager.put("Table.gridColor", BORDER);
        javax.swing.UIManager.put("TableHeader.background", PANEL_LIGHT);
        javax.swing.UIManager.put("TableHeader.foreground", TEXT_DIM);
        javax.swing.UIManager.put("TitledBorder.titleColor", TEXT_DIM);
        javax.swing.UIManager.put("Button.background", PANEL_LIGHT);
        javax.swing.UIManager.put("Button.foreground", TEXT);
    }
}
