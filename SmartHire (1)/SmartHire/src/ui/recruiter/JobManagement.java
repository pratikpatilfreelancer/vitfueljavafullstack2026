package ui.recruiter;

import dao.JobDAO;
import model.Job;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Post new jobs, edit existing ones, close/reopen them, or delete them.
 */
public class JobManagement extends JPanel {

    private final int recruiterId;
    private final JobDAO jobDAO = new JobDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField titleField = new JTextField(20);
    private final JTextField locationField = new JTextField(20);
    private final JTextField skillsField = new JTextField(20);
    private final JTextField experienceField = new JTextField(20);
    private final JTextArea descriptionArea = new JTextArea(4, 20);
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[]{"OPEN", "CLOSED"});

    private Integer editingJobId = null;

    public JobManagement(int recruiterId) {
        this.recruiterId = recruiterId;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Manage Job Postings");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.55);

        String[] columns = {"Job ID", "Title", "Location", "Min Exp", "Status", "Posted"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        splitPane.setTopComponent(new JScrollPane(table));

        splitPane.setBottomComponent(buildFormPanel());

        add(splitPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedJobIntoForm();
            }
        });

        loadJobs();
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Job Details"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(formPanel, gc, row++, "Title:", titleField);
        addRow(formPanel, gc, row++, "Location:", locationField);
        addRow(formPanel, gc, row++, "Required Skills (comma sep):", skillsField);
        addRow(formPanel, gc, row++, "Min Experience (years):", experienceField);
        addRow(formPanel, gc, row++, "Status:", statusCombo);

        gc.gridx = 0; gc.gridy = row; gc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gc);
        gc.gridx = 1;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gc);
        row++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton newBtn = new JButton("New Job");
        JButton saveBtn = new JButton("Save Job");
        JButton deleteBtn = new JButton("Delete Job");
        buttonPanel.add(newBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(deleteBtn);

        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        formPanel.add(buttonPanel, gc);

        newBtn.addActionListener(e -> clearForm());
        saveBtn.addActionListener(e -> saveJob());
        deleteBtn.addActionListener(e -> deleteJob());

        return formPanel;
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), gc);
        gc.gridx = 1;
        panel.add(field, gc);
    }

    private void loadJobs() {
        tableModel.setRowCount(0);
        List<Job> jobs = jobDAO.getJobsByRecruiter(recruiterId);
        for (Job job : jobs) {
            tableModel.addRow(new Object[]{
                    job.getJobId(), job.getTitle(), job.getLocation(),
                    job.getMinExperience(), job.getStatus(), job.getPostedDate()
            });
        }
    }

    private void loadSelectedJobIntoForm() {
        int row = table.getSelectedRow();
        int jobId = (int) tableModel.getValueAt(row, 0);
        Job job = jobDAO.getJobById(jobId);
        if (job == null) return;

        editingJobId = jobId;
        titleField.setText(job.getTitle());
        locationField.setText(job.getLocation());
        skillsField.setText(job.getRequiredSkills());
        experienceField.setText(String.valueOf(job.getMinExperience()));
        descriptionArea.setText(job.getDescription());
        statusCombo.setSelectedItem(job.getStatus());
    }

    private void clearForm() {
        editingJobId = null;
        titleField.setText("");
        locationField.setText("");
        skillsField.setText("");
        experienceField.setText("");
        descriptionArea.setText("");
        statusCombo.setSelectedItem("OPEN");
        table.clearSelection();
    }

    private void saveJob() {
        if (ValidationUtil.isEmpty(titleField.getText()) || ValidationUtil.isEmpty(locationField.getText())) {
            JOptionPane.showMessageDialog(this, "Title and location are required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int minExp = ValidationUtil.parseIntSafe(experienceField.getText(), -1);
        if (minExp < 0) {
            JOptionPane.showMessageDialog(this, "Minimum experience must be a non-negative number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Job job = new Job();
        job.setRecruiterId(recruiterId);
        job.setTitle(titleField.getText().trim());
        job.setLocation(locationField.getText().trim());
        job.setRequiredSkills(skillsField.getText().trim());
        job.setMinExperience(minExp);
        job.setDescription(descriptionArea.getText().trim());
        job.setStatus((String) statusCombo.getSelectedItem());
        job.setPostedDate(LocalDate.now());

        boolean success;
        if (editingJobId == null) {
            success = jobDAO.createJob(job) != -1;
        } else {
            job.setJobId(editingJobId);
            success = jobDAO.updateJob(job);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Job saved successfully.",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadJobs();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save job.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteJob() {
        if (editingJobId == null) {
            JOptionPane.showMessageDialog(this, "Select a job to delete first.",
                    "No Job Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this job posting? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            jobDAO.deleteJob(editingJobId);
            clearForm();
            loadJobs();
        }
    }
}
