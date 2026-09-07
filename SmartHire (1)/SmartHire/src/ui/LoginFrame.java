package ui;

import dao.CandidateDAO;
import dao.RecruiterDAO;
import dao.UserDAO;
import model.Candidate;
import model.Recruiter;
import model.User;
import ui.candidate.CandidateDashboard;
import ui.recruiter.RecruiterDashboard;
import util.SessionManager;
import util.ValidationUtil;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("SmartHire - Login");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("SmartHire");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel subtitleLabel = new JLabel("Resume Screening & Recruitment Management");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        panel.add(titleLabel, gc);
        gc.gridy = 1;
        panel.add(subtitleLabel, gc);

        gc.gridwidth = 1;
        gc.gridy = 2; gc.gridx = 0;
        panel.add(new JLabel("Username:"), gc);
        gc.gridx = 1;
        panel.add(usernameField, gc);

        gc.gridy = 3; gc.gridx = 0;
        panel.add(new JLabel("Password:"), gc);
        gc.gridx = 1;
        panel.add(passwordField, gc);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Create Account");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        gc.gridy = 4; gc.gridx = 0; gc.gridwidth = 2;
        panel.add(buttonPanel, gc);

        JLabel hintLabel = new JLabel("<html><center>Demo accounts (after loading sample_data.sql):<br>"
                + "recruiter1 / password123  |  candidate1 / password123</center></html>");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
        gc.gridy = 5;
        panel.add(hintLabel, gc);

        add(panel);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SessionManager.login(user);

        if ("CANDIDATE".equalsIgnoreCase(user.getRole())) {
            Candidate candidate = new CandidateDAO().getCandidateByUserId(user.getUserId());
            if (candidate != null) {
                SessionManager.setCandidateId(candidate.getCandidateId());
            }
            new CandidateDashboard().setVisible(true);
        } else {
            Recruiter recruiter = new RecruiterDAO().getRecruiterByUserId(user.getUserId());
            if (recruiter != null) {
                SessionManager.setRecruiterId(recruiter.getRecruiterId());
            }
            new RecruiterDashboard().setVisible(true);
        }
        dispose();
    }
}
