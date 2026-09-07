package ui;

import dao.CandidateDAO;
import dao.RecruiterDAO;
import dao.UserDAO;
import model.Candidate;
import model.Recruiter;
import model.User;

import javax.swing.*;
import java.awt.*;
import util.ValidationUtil;

public class RegisterFrame extends JFrame {

    private final JTextField fullNameField = new JTextField(18);
    private final JTextField usernameField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JPasswordField confirmPasswordField = new JPasswordField(18);
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"CANDIDATE", "RECRUITER"});

    // candidate-only
    private final JTextField phoneField = new JTextField(18);
    // recruiter-only
    private final JTextField companyField = new JTextField(18);

    private final UserDAO userDAO = new UserDAO();

    public RegisterFrame() {
        setTitle("SmartHire - Create Account");
        setSize(460, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(panel, gc, row++, "Full Name:", fullNameField);
        addRow(panel, gc, row++, "Username:", usernameField);
        addRow(panel, gc, row++, "Email:", emailField);
        addRow(panel, gc, row++, "Password:", passwordField);
        addRow(panel, gc, row++, "Confirm Password:", confirmPasswordField);
        addRow(panel, gc, row++, "Register as:", roleCombo);
        addRow(panel, gc, row++, "Phone (candidate):", phoneField);
        addRow(panel, gc, row++, "Company (recruiter):", companyField);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back to Login");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);

        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        panel.add(buttonPanel, gc);

        add(panel);

        registerBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        panel.add(new JLabel(label), gc);
        gc.gridx = 1;
        panel.add(field, gc);
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (ValidationUtil.isEmpty(fullName) || ValidationUtil.isEmpty(username)
                || ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
            showError("Please fill in all required fields.");
            return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            showError("Please enter a valid email address.");
            return;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            showError("Password must be at least 6 characters long.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }
        if (userDAO.usernameExists(username)) {
            showError("That username is already taken.");
            return;
        }
        if ("CANDIDATE".equals(role) && !ValidationUtil.isEmpty(phoneField.getText())
                && !ValidationUtil.isValidPhone(phoneField.getText())) {
            showError("Please enter a valid phone number, or leave it blank.");
            return;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        int userId = userDAO.registerUser(user);
        if (userId == -1) {
            showError("Registration failed. Please try again.");
            return;
        }

        if ("CANDIDATE".equals(role)) {
            Candidate candidate = new Candidate();
            candidate.setUserId(userId);
            candidate.setPhone(phoneField.getText().trim());
            candidate.setSkills("");
            candidate.setEducation("");
            candidate.setExperienceYears(0);
            candidate.setResumeText("");
            new CandidateDAO().createCandidate(candidate);
        } else {
            Recruiter recruiter = new Recruiter();
            recruiter.setUserId(userId);
            recruiter.setCompanyName(companyField.getText().trim());
            recruiter.setDepartment("");
            new RecruiterDAO().createRecruiter(recruiter);
        }

        JOptionPane.showMessageDialog(this, "Account created successfully! Please log in.",
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
        new LoginFrame().setVisible(true);
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
}
