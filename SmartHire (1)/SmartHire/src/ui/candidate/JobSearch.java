package ui.candidate;

import dao.JobDAO;
import model.Job;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Browsing/search screen for open jobs. Selecting a row and clicking Apply
 * opens the ApplyJob dialog, which runs the resume screening service.
 */
public class JobSearch extends JPanel {

    private final JobDAO jobDAO = new JobDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField = new JTextField(20);
    private final CandidateDashboard dashboard;

    public JobSearch(CandidateDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Find Jobs");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Show All");
        searchPanel.add(new JLabel("Keyword:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Job ID", "Title", "Location", "Min Experience", "Required Skills"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn = new JButton("Apply to Selected Job");
        applyBtn.addActionListener(e -> applyToSelected());
        bottomPanel.add(applyBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> loadJobs(searchField.getText().trim()));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadJobs("");
        });

        loadJobs("");
    }

    private void loadJobs(String keyword) {
        tableModel.setRowCount(0);
        List<Job> jobs = keyword.isEmpty() ? jobDAO.getAllOpenJobs() : jobDAO.searchJobs(keyword);
        for (Job job : jobs) {
            tableModel.addRow(new Object[]{
                    job.getJobId(), job.getTitle(), job.getLocation(),
                    job.getMinExperience(), job.getRequiredSkills()
            });
        }
        if (jobs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No open jobs matched your search.",
                    "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void applyToSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a job to apply to.",
                    "No Job Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int jobId = (int) tableModel.getValueAt(row, 0);
        Job job = jobDAO.getJobById(jobId);
        if (job == null) return;

        ApplyJob dialog = new ApplyJob((Frame) SwingUtilities.getWindowAncestor(this), job);
        dialog.setVisible(true);
    }
}
