package ui.recruiter;

import model.Application;
import model.Job;
import dao.JobDAO;
import service.ApplicationService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Simple summary statistics screen: job counts, applications per status,
 * and average match score per job. No charting library is used to keep
 * the project dependency-free — figures are shown as text/labels.
 */
public class RecruitmentReports extends JPanel {

    private final int recruiterId;
    private final JobDAO jobDAO = new JobDAO();
    private final ApplicationService applicationService = new ApplicationService();

    public RecruitmentReports(int recruiterId) {
        this.recruiterId = recruiterId;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Recruitment Reports");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel content = buildReportPanel();
        add(new JScrollPane(content), BorderLayout.CENTER);
    }

    private JPanel buildReportPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 4, 12, 4));

        List<Job> jobs = jobDAO.getJobsByRecruiter(recruiterId);
        List<Application> applications = applicationService.getApplicationsForRecruiter(recruiterId);

        long openJobs = jobs.stream().filter(j -> "OPEN".equals(j.getStatus())).count();
        long closedJobs = jobs.size() - openJobs;

        panel.add(sectionLabel("Overview"));
        panel.add(statLine("Total Jobs Posted", String.valueOf(jobs.size())));
        panel.add(statLine("Open Jobs", String.valueOf(openJobs)));
        panel.add(statLine("Closed Jobs", String.valueOf(closedJobs)));
        panel.add(statLine("Total Applications Received", String.valueOf(applications.size())));
        panel.add(Box.createVerticalStrut(16));

        panel.add(sectionLabel("Applications by Status"));
        Map<String, Long> statusCounts = new TreeMap<>();
        for (Application app : applications) {
            statusCounts.merge(app.getStatus(), 1L, Long::sum);
        }
        if (statusCounts.isEmpty()) {
            panel.add(statLine("No applications yet", ""));
        } else {
            for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
                panel.add(statLine(entry.getKey(), String.valueOf(entry.getValue())));
            }
        }
        panel.add(Box.createVerticalStrut(16));

        panel.add(sectionLabel("Average Match Score by Job"));
        if (jobs.isEmpty()) {
            panel.add(statLine("No jobs posted yet", ""));
        } else {
            for (Job job : jobs) {
                List<Application> jobApps = applicationService.getApplicationsForJob(job.getJobId());
                double avg = jobApps.stream().mapToDouble(Application::getMatchScore).average().orElse(0.0);
                panel.add(statLine(job.getTitle() + " (" + jobApps.size() + " applicants)",
                        String.format("%.1f%%", avg)));
            }
        }

        return panel;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel statLine(String label, String value) {
        JPanel line = new JPanel(new BorderLayout());
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        line.add(new JLabel("  " + label), BorderLayout.WEST);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        line.add(valueLabel, BorderLayout.EAST);
        return line;
    }
}
