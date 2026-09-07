package ui.recruiter;

import dao.JobDAO;
import model.Job;
import service.ApplicationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Lets a recruiter pick one of their jobs and see candidates ranked by the
 * automated match score, with the option to re-run screening (e.g. after a
 * candidate updates their profile).
 */
public class ResumeScreening extends JPanel {

    private final int recruiterId;
    private final JobDAO jobDAO = new JobDAO();
    private final ApplicationService applicationService = new ApplicationService();

    private final JComboBox<JobItem> jobCombo = new JComboBox<>();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public ResumeScreening(int recruiterId) {
        this.recruiterId = recruiterId;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Resume Screening");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton rescreenBtn = new JButton("Re-run Screening");
        controlPanel.add(new JLabel("Job:"));
        controlPanel.add(jobCombo);
        controlPanel.add(rescreenBtn);
        topPanel.add(controlPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JLabel explainer = new JLabel(
                "<html>Candidates are ranked using a rule-based score: 70% skill-keyword overlap "
                        + "with the job's required skills, 30% experience fit vs. the minimum required.</html>");
        explainer.setFont(new Font("SansSerif", Font.ITALIC, 11));
        explainer.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));
        add(explainer, BorderLayout.PAGE_START);

        String[] columns = {"Rank", "Candidate", "Match %", "Status", "Applied"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);

        jobCombo.addActionListener(e -> loadScreeningResults());
        rescreenBtn.addActionListener(e -> {
            JobItem selected = (JobItem) jobCombo.getSelectedItem();
            if (selected != null) {
                applicationService.rescreenJob(selected.jobId);
                loadScreeningResults();
                JOptionPane.showMessageDialog(this, "Screening re-run for this job.",
                        "Done", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadJobCombo();
    }

    private void loadJobCombo() {
        jobCombo.removeAllItems();
        List<Job> jobs = jobDAO.getJobsByRecruiter(recruiterId);
        for (Job job : jobs) {
            jobCombo.addItem(new JobItem(job.getJobId(), job.getTitle()));
        }
    }

    private void loadScreeningResults() {
        tableModel.setRowCount(0);
        JobItem selected = (JobItem) jobCombo.getSelectedItem();
        if (selected == null) return;

        List<model.Application> applications = applicationService.getApplicationsForJob(selected.jobId);
        int rank = 1;
        for (model.Application app : applications) {
            tableModel.addRow(new Object[]{
                    rank++, app.getCandidateName(), String.format("%.1f", app.getMatchScore()),
                    app.getStatus(), app.getAppliedDate()
            });
        }
    }

    private static class JobItem {
        final int jobId;
        final String title;
        JobItem(int jobId, String title) { this.jobId = jobId; this.title = title; }
        @Override public String toString() { return title; }
    }
}
