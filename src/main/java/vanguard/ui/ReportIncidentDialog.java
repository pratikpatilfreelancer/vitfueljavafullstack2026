package vanguard.ui;

import javax.swing.*;
import java.awt.*;

public class ReportIncidentDialog extends JDialog {

    private final JTextArea descriptionArea = new JTextArea(4, 30);
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private boolean confirmed = false;

    public ReportIncidentDialog(Frame owner, int defaultX, int defaultY) {
        super(owner, "Report Incident", true);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Theme.PANEL);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        form.add(themedLabel("Description:"), c);
        c.gridx = 1; c.gridy = 0;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Theme.PANEL_LIGHT);
        descriptionArea.setForeground(Theme.TEXT);
        descriptionArea.setCaretColor(Theme.TEXT);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        form.add(descScroll, c);

        xSpinner = new JSpinner(new SpinnerNumberModel(defaultX, 0, 400, 1));
        ySpinner = new JSpinner(new SpinnerNumberModel(defaultY, 0, 260, 1));

        c.gridx = 0; c.gridy = 1;
        form.add(themedLabel("X coordinate:"), c);
        c.gridx = 1; c.gridy = 1;
        form.add(xSpinner, c);

        c.gridx = 0; c.gridy = 2;
        form.add(themedLabel("Y coordinate:"), c);
        c.gridx = 1; c.gridy = 2;
        form.add(ySpinner, c);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        JButton ok = new JButton("Analyze & Report");
        ok.setBackground(Theme.CYAN);
        ok.setForeground(Theme.BG);
        ok.setFocusPainted(false);
        ok.setOpaque(true);
        ok.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JButton cancel = new JButton("Cancel");
        cancel.setBackground(Theme.PANEL_LIGHT);
        cancel.setForeground(Theme.TEXT);
        cancel.setFocusPainted(false);
        cancel.setOpaque(true);
        cancel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        ok.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });
        cancel.addActionListener(e -> setVisible(false));
        buttons.add(cancel);
        buttons.add(ok);
        add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private JLabel themedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.TEXT);
        return label;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getDescriptionText() {
        return descriptionArea.getText();
    }

    public int getIncidentX() {
        return (Integer) xSpinner.getValue();
    }

    public int getIncidentY() {
        return (Integer) ySpinner.getValue();
    }
}
