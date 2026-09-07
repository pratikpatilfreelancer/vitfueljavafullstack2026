package ui.candidate;

import model.Job;
import service.ApplicationService;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Confirmation dialog shown before a candidate applies to a job. Runs the
 * ApplicationService, which internally invokes ResumeScreeningService, and
 * shows the resulting match score right away.
 */
public class ApplyJob extends JDialog {

    private final Job job;
    private final ApplicationService applicationService = new ApplicationService();

    public ApplyJob(Frame owner, Job job) {
        super(owner, "Apply to Job", true);
        this.job = job;
        setSize(420, 320);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setText(
                "Title: " + job.getTitle() + "\n" +
                "Location: " + job.getLocation() + "\n" +
                "Minimum Experience: " + job.getMinExperience() + " years\n" +
                "Required Skills: " + job.getRequiredSkills() + "\n\n" +
                "Description:\n" + job.getDescription()
        );

        panel.add(new JScrollPane(details), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn = new JButton("Confirm Application");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(cancelBtn);
        buttonPanel.add(applyBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        applyBtn.addActionListener(e -> submitApplication());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void submitApplication() {
        Integer candidateId = SessionManager.getCurrentCandidateId();
        if (candidateId == null) {
            JOptionPane.showMessageDialog(this, "No candidate profile is associated with this account.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int applicationId = applicationService.applyToJob(candidateId, job.getJobId());
        if (applicationId == -1) {
            JOptionPane.showMessageDialog(this,
                    "You have already applied to this job, or the application could not be submitted.",
                    "Application Not Submitted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Application submitted successfully!\n\n"
                        + "Your resume has been automatically screened against this job's requirements. "
                        + "You can check your match score and status under \"My Applications\".",
                "Application Submitted", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
