package ui.candidate;

import model.Application;
import service.ApplicationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Shows every application a candidate has submitted, along with the current
 * status and match score computed at application time.
 */
public class ApplicationStatus extends JPanel {

    private final ApplicationService applicationService = new ApplicationService();

    public ApplicationStatus(int candidateId) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("My Applications");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Job Title", "Applied On", "Status", "Match Score (%)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);

        List<Application> applications = applicationService.getApplicationsForCandidate(candidateId);
        for (Application app : applications) {
            model.addRow(new Object[]{
                    app.getJobTitle(),
                    app.getAppliedDate(),
                    app.getStatus(),
                    String.format("%.1f", app.getMatchScore())
            });
        }

        if (applications.isEmpty()) {
            JLabel empty = new JLabel("You haven't applied to any jobs yet.", SwingConstants.CENTER);
            add(empty, BorderLayout.SOUTH);
        }
    }
}
