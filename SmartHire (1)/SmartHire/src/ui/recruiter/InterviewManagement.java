package ui.recruiter;

import model.Interview;
import service.InterviewService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Lists all interviews scheduled by this recruiter and allows recording
 * feedback/rating once an interview has taken place.
 */
public class InterviewManagement extends JPanel {

    private final int recruiterId;
    private final InterviewService interviewService = new InterviewService();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public InterviewManagement(int recruiterId) {
        this.recruiterId = recruiterId;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Interview Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Interview ID", "Candidate", "Job", "Date", "Time", "Mode", "Status", "Rating"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton feedbackBtn = new JButton("Record Feedback / Complete");
        JButton cancelBtn = new JButton("Cancel Interview");
        JButton refreshBtn = new JButton("Refresh");
        bottomPanel.add(refreshBtn);
        bottomPanel.add(cancelBtn);
        bottomPanel.add(feedbackBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        feedbackBtn.addActionListener(e -> recordFeedback());
        cancelBtn.addActionListener(e -> cancelInterview());
        refreshBtn.addActionListener(e -> loadInterviews());

        loadInterviews();
    }

    private void loadInterviews() {
        tableModel.setRowCount(0);
        List<Interview> interviews = interviewService.getInterviewsForRecruiter(recruiterId);
        for (Interview iv : interviews) {
            tableModel.addRow(new Object[]{
                    iv.getInterviewId(), iv.getCandidateName(), iv.getJobTitle(),
                    iv.getInterviewDate(), iv.getInterviewTime(), iv.getMode(),
                    iv.getStatus(), iv.getRating() == 0 ? "-" : iv.getRating()
            });
        }
    }

    private Integer getSelectedInterviewId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an interview first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void recordFeedback() {
        Integer interviewId = getSelectedInterviewId();
        if (interviewId == null) return;

        JTextArea feedbackArea = new JTextArea(5, 25);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.add(new JLabel("Rating (1-5):"));
        ratingPanel.add(ratingCombo);
        panel.add(ratingPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Interview Feedback",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Interview updated = new Interview();
            // preserve date/time/mode by fetching the row's existing values
            int row = table.getSelectedRow();
            updated.setInterviewDate(java.time.LocalDate.parse(tableModel.getValueAt(row, 3).toString()));
            updated.setInterviewTime(java.time.LocalTime.parse(tableModel.getValueAt(row, 4).toString()));
            updated.setMode(tableModel.getValueAt(row, 5).toString());
            updated.setFeedback(feedbackArea.getText().trim());
            updated.setRating((Integer) ratingCombo.getSelectedItem());

            boolean success = interviewService.completeInterview(interviewId, updated);
            if (success) {
                loadInterviews();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save feedback.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelInterview() {
        Integer interviewId = getSelectedInterviewId();
        if (interviewId == null) return;

        int row = table.getSelectedRow();
        Interview iv = new Interview();
        iv.setInterviewId(interviewId);
        iv.setInterviewDate(java.time.LocalDate.parse(tableModel.getValueAt(row, 3).toString()));
        iv.setInterviewTime(java.time.LocalTime.parse(tableModel.getValueAt(row, 4).toString()));
        iv.setMode(tableModel.getValueAt(row, 5).toString());
        iv.setFeedback("");
        iv.setRating(0);

        interviewService.cancelInterview(iv);
        loadInterviews();
    }
}
