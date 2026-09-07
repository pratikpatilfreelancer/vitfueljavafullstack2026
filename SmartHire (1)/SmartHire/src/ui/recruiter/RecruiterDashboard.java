package ui.recruiter;

import ui.LoginFrame;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Landing screen for a logged-in recruiter. Hosts the other recruiter
 * screens as cards in a CardLayout so navigation stays within one window.
 */
public class RecruiterDashboard extends JFrame {

    private final JPanel cards = new JPanel(new CardLayout());
    private static final String JOBS = "JOBS";
    private static final String APPLICANTS = "APPLICANTS";
    private static final String SCREENING = "SCREENING";
    private static final String INTERVIEWS = "INTERVIEWS";
    private static final String REPORTS = "REPORTS";

    public RecruiterDashboard() {
        setTitle("SmartHire - Recruiter Dashboard");
        setSize(1000, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);

        int recruiterId = SessionManager.getCurrentRecruiterId();

        cards.add(new JobManagement(recruiterId), JOBS);
        cards.add(new ApplicantManagement(recruiterId), APPLICANTS);
        cards.add(new ResumeScreening(recruiterId), SCREENING);
        cards.add(new InterviewManagement(recruiterId), INTERVIEWS);
        cards.add(new RecruitmentReports(recruiterId), REPORTS);

        showCard(JOBS);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel welcome = new JLabel("Welcome, " + SessionManager.getCurrentUser().getFullName());
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton jobsBtn = new JButton("Manage Jobs");
        JButton applicantsBtn = new JButton("Applicants");
        JButton screeningBtn = new JButton("Resume Screening");
        JButton interviewsBtn = new JButton("Interviews");
        JButton reportsBtn = new JButton("Reports");
        JButton logoutBtn = new JButton("Logout");

        jobsBtn.addActionListener(e -> showCard(JOBS));
        applicantsBtn.addActionListener(e -> showCard(APPLICANTS));
        screeningBtn.addActionListener(e -> showCard(SCREENING));
        interviewsBtn.addActionListener(e -> showCard(INTERVIEWS));
        reportsBtn.addActionListener(e -> showCard(REPORTS));
        logoutBtn.addActionListener(e -> logout());

        navPanel.add(jobsBtn);
        navPanel.add(applicantsBtn);
        navPanel.add(screeningBtn);
        navPanel.add(interviewsBtn);
        navPanel.add(reportsBtn);
        navPanel.add(logoutBtn);

        header.add(welcome, BorderLayout.WEST);
        header.add(navPanel, BorderLayout.EAST);
        return header;
    }

    public void showCard(String name) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, name);
    }

    private void logout() {
        SessionManager.logout();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
