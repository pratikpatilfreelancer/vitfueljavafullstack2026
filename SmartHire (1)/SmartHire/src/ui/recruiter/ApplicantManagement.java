package ui.recruiter;

import dao.JobDAO;
import model.Application;
import model.Job;
import service.ApplicationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Recruiter-facing view of all applicants across their job postings, with
 * shortlist/reject/hire actions and quick interview scheduling.
 */
public class ApplicantManagement extends JPanel {

    private final int recruiterId;
    private final ApplicationService applicationService = new ApplicationService();
    private final JobDAO jobDAO = new JobDAO();

    private final JComboBox<JobItem> jobFilterCombo = new JComboBox<>();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public ApplicantManagement(int recruiterId) {
        this.recruiterId = recruiterId;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Applicant Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Filter by Job:"));
        filterPanel.add(jobFilterCombo);
        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"App ID", "Candidate", "Job", "Applied", "Status", "Match %"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton shortlistBtn = new JButton("Shortlist");
        JButton rejectBtn = new JButton("Reject");
        JButton hireBtn = new JButton("Mark Hired");
        JButton scheduleBtn = new JButton("Schedule Interview");
        actionPanel.add(shortlistBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(hireBtn);
        actionPanel.add(scheduleBtn);
        add(actionPanel, BorderLayout.SOUTH);

        shortlistBtn.addActionListener(e -> updateStatus("SHORTLISTED"));
        rejectBtn.addActionListener(e -> updateStatus("REJECTED"));
        hireBtn.addActionListener(e -> updateStatus("HIRED"));
        scheduleBtn.addActionListener(e -> scheduleInterview());

        jobFilterCombo.addActionListener(e -> loadApplications());

        loadJobFilter();
        loadApplications();
    }

    private void loadJobFilter() {
        jobFilterCombo.removeAllItems();
        jobFilterCombo.addItem(new JobItem(-1, "All Jobs"));
        List<Job> jobs = jobDAO.getJobsByRecruiter(recruiterId);
        for (Job job : jobs) {
            jobFilterCombo.addItem(new JobItem(job.getJobId(), job.getTitle()));
        }
    }

    private void loadApplications() {
        tableModel.setRowCount(0);
        JobItem selected = (JobItem) jobFilterCombo.getSelectedItem();
        List<Application> applications;
        if (selected == null || selected.jobId == -1) {
            applications = applicationService.getApplicationsForRecruiter(recruiterId);
        } else {
            applications = applicationService.getApplicationsForJob(selected.jobId);
        }
        for (Application app : applications) {
            tableModel.addRow(new Object[]{
                    app.getApplicationId(), app.getCandidateName(), app.getJobTitle(),
                    app.getAppliedDate(), app.getStatus(), String.format("%.1f", app.getMatchScore())
            });
        }
    }

    private Integer getSelectedApplicationId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an applicant first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void updateStatus(String status) {
        Integer appId = getSelectedApplicationId();
        if (appId == null) return;

        boolean success;
        switch (status) {
            case "SHORTLISTED": success = applicationService.shortlist(appId); break;
            case "REJECTED": success = applicationService.reject(appId); break;
            case "HIRED": success = applicationService.markHired(appId); break;
            default: success = false;
        }

        if (success) {
            loadApplications();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update application status.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void scheduleInterview() {
        Integer appId = getSelectedApplicationId();
        if (appId == null) return;

        InterviewScheduleDialog dialog = new InterviewScheduleDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), appId);
        dialog.setVisible(true);
        loadApplications();
    }

    /** Simple wrapper so the combo box can display job titles while keeping job IDs. */
    private static class JobItem {
        final int jobId;
        final String title;
        JobItem(int jobId, String title) { this.jobId = jobId; this.title = title; }
        @Override public String toString() { return title; }
    }
}
