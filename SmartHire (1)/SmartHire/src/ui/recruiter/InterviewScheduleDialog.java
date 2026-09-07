package ui.recruiter;

import service.InterviewService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Small helper dialog used by both ApplicantManagement and InterviewManagement
 * to schedule an interview for a given application.
 */
public class InterviewScheduleDialog extends JDialog {

    private final int applicationId;
    private final InterviewService interviewService = new InterviewService();

    private final JTextField dateField = new JTextField(LocalDate.now().plusDays(3).toString(), 12);
    private final JTextField timeField = new JTextField("10:00", 12);
    private final JComboBox<String> modeCombo = new JComboBox<>(new String[]{"ONLINE", "IN_PERSON", "PHONE"});

    public InterviewScheduleDialog(Frame owner, int applicationId) {
        super(owner, "Schedule Interview", true);
        this.applicationId = applicationId;
        setSize(360, 240);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gc);
        gc.gridx = 1;
        panel.add(dateField, gc);

        gc.gridx = 0; gc.gridy = 1;
        panel.add(new JLabel("Time (HH:MM, 24h):"), gc);
        gc.gridx = 1;
        panel.add(timeField, gc);

        gc.gridx = 0; gc.gridy = 2;
        panel.add(new JLabel("Mode:"), gc);
        gc.gridx = 1;
        panel.add(modeCombo, gc);

        JButton scheduleBtn = new JButton("Schedule");
        JButton cancelBtn = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(scheduleBtn);

        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        panel.add(buttonPanel, gc);

        add(panel);

        scheduleBtn.addActionListener(e -> submit());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void submit() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime time = LocalTime.parse(timeField.getText().trim());
            String mode = (String) modeCombo.getSelectedItem();

            int id = interviewService.scheduleInterview(applicationId, date, time, mode);
            if (id != -1) {
                JOptionPane.showMessageDialog(this, "Interview scheduled successfully.",
                        "Scheduled", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to schedule interview.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid date (YYYY-MM-DD) and time (HH:MM).",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }
}
