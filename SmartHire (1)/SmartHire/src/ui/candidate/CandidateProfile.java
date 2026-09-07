package ui.candidate;

import dao.CandidateDAO;
import model.Candidate;
import util.ValidationUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Lets a candidate edit their skills, education, experience and resume text.
 * The resume_text field doubles as the input to ResumeScreeningService, so
 * candidates are encouraged to paste a plain-text version of their resume.
 */
public class CandidateProfile extends JPanel {

    private final Candidate candidate;
    private final CandidateDAO candidateDAO = new CandidateDAO();

    private final JTextField phoneField = new JTextField(20);
    private final JTextField skillsField = new JTextField(20);
    private final JTextField educationField = new JTextField(20);
    private final JTextField experienceField = new JTextField(20);
    private final JTextArea resumeArea = new JTextArea(10, 30);

    public CandidateProfile(Candidate candidate) {
        this.candidate = candidate;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(formPanel, gc, row++, "Phone:", phoneField);
        addRow(formPanel, gc, row++, "Skills (comma separated):", skillsField);
        addRow(formPanel, gc, row++, "Education:", educationField);
        addRow(formPanel, gc, row++, "Years of Experience:", experienceField);

        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Resume Text:"), gc);
        gc.gridx = 1;
        resumeArea.setLineWrap(true);
        resumeArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(resumeArea), gc);
        row++;

        JButton saveBtn = new JButton("Save Profile");
        saveBtn.addActionListener(e -> saveProfile());
        gc.gridx = 1; gc.gridy = row; gc.anchor = GridBagConstraints.EAST;
        formPanel.add(saveBtn, gc);

        add(formPanel, BorderLayout.CENTER);

        populateFields();
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), gc);
        gc.gridx = 1;
        panel.add(field, gc);
    }

    private void populateFields() {
        if (candidate == null) return;
        phoneField.setText(candidate.getPhone());
        skillsField.setText(candidate.getSkills());
        educationField.setText(candidate.getEducation());
        experienceField.setText(String.valueOf(candidate.getExperienceYears()));
        resumeArea.setText(candidate.getResumeText());
    }

    private void saveProfile() {
        if (candidate == null) {
            JOptionPane.showMessageDialog(this, "No candidate profile found for this account.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!ValidationUtil.isEmpty(phoneField.getText()) && !ValidationUtil.isValidPhone(phoneField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int experience = ValidationUtil.parseIntSafe(experienceField.getText(), -1);
        if (experience < 0) {
            JOptionPane.showMessageDialog(this, "Years of experience must be a non-negative number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        candidate.setPhone(phoneField.getText().trim());
        candidate.setSkills(skillsField.getText().trim());
        candidate.setEducation(educationField.getText().trim());
        candidate.setExperienceYears(experience);
        candidate.setResumeText(resumeArea.getText().trim());

        boolean success = candidateDAO.updateCandidate(candidate);
        if (success) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully.",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save profile. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
