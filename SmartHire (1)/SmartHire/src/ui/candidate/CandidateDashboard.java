package ui.candidate;

import dao.CandidateDAO;
import model.Candidate;
import ui.LoginFrame;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Landing screen for a logged-in candidate. Hosts the other candidate
 * screens as cards in a CardLayout so navigation stays within one window.
 */
public class CandidateDashboard extends JFrame {

    private final JPanel cards = new JPanel(new CardLayout());
    private static final String PROFILE = "PROFILE";
    private static final String SEARCH = "SEARCH";
    private static final String STATUS = "STATUS";

    public CandidateDashboard() {
        setTitle("SmartHire - Candidate Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);

        int candidateId = SessionManager.getCurrentCandidateId();
        Candidate candidate = new CandidateDAO().getCandidateById(candidateId);

        cards.add(new CandidateProfile(candidate), PROFILE);
        cards.add(new JobSearch(this), SEARCH);
        cards.add(new ApplicationStatus(candidateId), STATUS);

        showCard(SEARCH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel welcome = new JLabel("Welcome, " + SessionManager.getCurrentUser().getFullName());
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton profileBtn = new JButton("My Profile");
        JButton searchBtn = new JButton("Find Jobs");
        JButton statusBtn = new JButton("My Applications");
        JButton logoutBtn = new JButton("Logout");

        profileBtn.addActionListener(e -> showCard(PROFILE));
        searchBtn.addActionListener(e -> showCard(SEARCH));
        statusBtn.addActionListener(e -> refreshAndShowStatus());
        logoutBtn.addActionListener(e -> logout());

        navPanel.add(profileBtn);
        navPanel.add(searchBtn);
        navPanel.add(statusBtn);
        navPanel.add(logoutBtn);

        header.add(welcome, BorderLayout.WEST);
        header.add(navPanel, BorderLayout.EAST);
        return header;
    }

    private void refreshAndShowStatus() {
        cards.remove(2);
        ApplicationStatus statusPanel = new ApplicationStatus(SessionManager.getCurrentCandidateId());
        cards.add(statusPanel, STATUS);
        showCard(STATUS);
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
