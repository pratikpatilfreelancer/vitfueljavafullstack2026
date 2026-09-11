package vanguard.ui;

import vanguard.concurrent.DispatchThread;
import vanguard.db.IncidentDAO;
import vanguard.db.ResourceDAO;
import vanguard.exceptions.InvalidReportException;
import vanguard.exceptions.NoAvailableResourceException;
import vanguard.io.AuditLogger;
import vanguard.io.StateSerializer;
import vanguard.model.*;
import vanguard.service.AllocationService;
import vanguard.service.ClassificationResult;
import vanguard.service.IncidentClassifier;
import vanguard.service.DatabaseBackedClassifier;
import vanguard.util.DispatchCenter;
import vanguard.util.GeoUtils;
import vanguard.util.ResourceFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Swing desktop UI for VANGUARD. A thin presentation layer — every
 * button here calls into the same backend classes the console
 * version (Main.java) uses: DispatchCenter, AllocationService,
 * RuleBasedClassifier, DispatchThread, AuditLogger, StateSerializer,
 * and now IncidentDAO/ResourceDAO for real MySQL persistence.
 */
public class MainFrame extends JFrame {

    private final DispatchCenter dc = DispatchCenter.getInstance();
    private final IncidentClassifier classifier = new DatabaseBackedClassifier();
    private final AllocationService allocationService = new AllocationService();
    private final AuditLogger logger = new AuditLogger();
    private final StateSerializer serializer = new StateSerializer();
    private final IncidentDAO incidentDAO = new IncidentDAO();
    private final ResourceDAO resourceDAO = new ResourceDAO();
    private final AtomicInteger incidentSeq = new AtomicInteger(1);
    private final Random random = new Random();

    private MapPanel mapPanel;
    private DefaultTableModel resourceTableModel;
    private DefaultTableModel incidentTableModel;
    private JLabel statusLabel;
    private JToggleButton pinButton;
    private Point pendingPin;

    public MainFrame() {
        super("VANGUARD  —  AI-Style Disaster Response & Resource Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        seedResources();
        buildUI();
        startRefreshTimer();

        setSize(1250, 720);
        setMinimumSize(new Dimension(980, 600));
        setLocationRelativeTo(null);
    }

    private void seedResources() {
        dc.registerResource(ResourceFactory.create(IncidentType.FIRE, "r1", "Engine 12", 60, 60));
        dc.registerResource(ResourceFactory.create(IncidentType.MEDICAL, "r2", "Medic 3", 120, 90));
        dc.registerResource(ResourceFactory.create(IncidentType.STRUCTURAL, "r3", "Rescue Alpha", 200, 130));
        dc.registerResource(ResourceFactory.create(IncidentType.FLOOD, "r4", "Flood Boat 2", 340, 40));
        dc.registerResource(ResourceFactory.create(IncidentType.OTHER, "r5", "Volunteer Unit A", 180, 220));
    }

    private void buildUI() {
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildResourcePanel(), BorderLayout.WEST);
        add(buildIncidentPanel(), BorderLayout.EAST);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);
        refreshLists();
    }

    // ---------------------------------------------------------------
    // Top bar
    // ---------------------------------------------------------------
    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Theme.PANEL);
        top.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        JLabel title = new JLabel("VANGUARD");
        title.setForeground(Theme.TEXT);
        title.setFont(Theme.FONT_TITLE);
        JLabel subtitle = new JLabel("Disaster Response & Resource Grid");
        subtitle.setForeground(Theme.TEXT_DIM);
        subtitle.setFont(Theme.FONT_BODY);
        titleBlock.add(title);
        titleBlock.add(subtitle);

        statusLabel = new JLabel("● SYSTEM READY");
        statusLabel.setForeground(Theme.GREEN);
        statusLabel.setFont(Theme.FONT_HEADING);

        top.add(titleBlock, BorderLayout.WEST);
        top.add(statusLabel, BorderLayout.EAST);
        return top;
    }

    // ---------------------------------------------------------------
    // Resource table (left)
    // ---------------------------------------------------------------
    private JPanel buildResourcePanel() {
        resourceTableModel = new DefaultTableModel(
                new Object[]{"Name", "Type", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(resourceTableModel);
        styleTable(table);
        table.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        JPanel wrap = titledPanel("RESOURCE FLEET");
        wrap.setPreferredSize(new Dimension(260, 0));
        wrap.add(scrollOf(table), BorderLayout.CENTER);
        return wrap;
    }

    // ---------------------------------------------------------------
    // Incident table (right)
    // ---------------------------------------------------------------
    private JPanel buildIncidentPanel() {
        incidentTableModel = new DefaultTableModel(
                new Object[]{"ID", "Type", "Severity", "Status", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(incidentTableModel);
        styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setCellRenderer(new SeverityCellRenderer());
        table.getColumnModel().getColumn(4).setPreferredWidth(220);

        JPanel wrap = titledPanel("LIVE INCIDENT FEED");
        wrap.setPreferredSize(new Dimension(420, 0));
        wrap.add(scrollOf(table), BorderLayout.CENTER);
        return wrap;
    }

    // ---------------------------------------------------------------
    // Map (center)
    // ---------------------------------------------------------------
    private JPanel buildCenterPanel() {
        mapPanel = new MapPanel();
        mapPanel.setPinListener((x, y) -> {
            if (!pinButton.isSelected()) return;
            pendingPin = new Point(x, y);
            mapPanel.setPin(pendingPin);
            statusLabel.setText("● PINNED (" + x + ", " + y + ")");
            statusLabel.setForeground(Theme.CYAN);
        });

        JPanel wrap = titledPanel("TACTICAL GRID  —  toggle Pin Mode below, then click to place an incident");
        wrap.add(mapPanel, BorderLayout.CENTER);
        wrap.add(buildLegend(), BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        legend.setBackground(Theme.PANEL);
        legend.add(legendItem("Critical", Theme.RED));
        legend.add(legendItem("High", Theme.ORANGE));
        legend.add(legendItem("Medium", Theme.AMBER));
        legend.add(legendItem("Low", Theme.GREEN));
        legend.add(legendItem("Resource", Theme.CYAN));
        return legend;
    }

    private JPanel legendItem(String label, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setOpaque(false);
        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        JLabel text = new JLabel(label);
        text.setForeground(Theme.TEXT_DIM);
        text.setFont(Theme.FONT_BODY.deriveFont(11f));
        item.add(dot);
        item.add(text);
        return item;
    }

    // ---------------------------------------------------------------
    // Controls (bottom)
    // ---------------------------------------------------------------
    private JPanel buildControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        controls.setBackground(Theme.PANEL);
        controls.setBorder(new EmptyBorder(4, 16, 4, 16));

        pinButton = styledToggle("Pin Location Mode");
        JButton reportButton = styledButton("Report Incident", Theme.CYAN, true);
        JButton allocateButton = styledButton("Run Allocation", Theme.GREEN, true);
        JButton statsButton = styledButton("Stats", Theme.PANEL_LIGHT, false);
        JButton logButton = styledButton("Audit Log", Theme.PANEL_LIGHT, false);
        JButton saveButton = styledButton("Save File", Theme.PANEL_LIGHT, false);
        JButton loadButton = styledButton("Load File", Theme.PANEL_LIGHT, false);
        JButton saveDbButton = styledButton("Save to MySQL", Theme.PANEL_LIGHT, false);
        JButton loadDbButton = styledButton("Load from MySQL", Theme.PANEL_LIGHT, false);

        reportButton.addActionListener(e -> onReportIncident());
        allocateButton.addActionListener(e -> onRunAllocation());
        statsButton.addActionListener(e -> onShowStats());
        logButton.addActionListener(e -> onViewLog());
        saveButton.addActionListener(e -> onSaveState());
        loadButton.addActionListener(e -> onLoadState());
        saveDbButton.addActionListener(e -> onSaveToDatabase());
        loadDbButton.addActionListener(e -> onLoadFromDatabase());

        controls.add(pinButton);
        controls.add(reportButton);
        controls.add(allocateButton);
        controls.add(statsButton);
        controls.add(logButton);
        controls.add(separatorLabel());
        controls.add(saveButton);
        controls.add(loadButton);
        controls.add(separatorLabel());
        controls.add(saveDbButton);
        controls.add(loadDbButton);
        return controls;
    }

    private JLabel separatorLabel() {
        JLabel sep = new JLabel("|");
        sep.setForeground(Theme.BORDER);
        return sep;
    }

    // ---------------------------------------------------------------
    // Styling helpers
    // ---------------------------------------------------------------
    private JPanel titledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.PANEL);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER), title);
        border.setTitleColor(Theme.TEXT_DIM);
        border.setTitleFont(Theme.FONT_HEADING.deriveFont(11f));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 8, 8, 8), border));
        return panel;
    }

    private JScrollPane scrollOf(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private void styleTable(JTable table) {
        table.setBackground(Theme.PANEL);
        table.setForeground(Theme.TEXT);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(24);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(Theme.PANEL_LIGHT);
        table.setSelectionForeground(Theme.TEXT);
        table.getTableHeader().setBackground(Theme.PANEL_LIGHT);
        table.getTableHeader().setForeground(Theme.TEXT_DIM);
        table.getTableHeader().setFont(Theme.FONT_HEADING.deriveFont(11f));
        table.setFillsViewportHeight(true);
    }

    private JButton styledButton(String text, Color accent, boolean prominent) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(Theme.FONT_HEADING.deriveFont(12f));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(prominent ? accent : Theme.BORDER),
                new EmptyBorder(7, 14, 7, 14)));
        if (prominent) {
            button.setBackground(accent);
            button.setForeground(Theme.BG);
        } else {
            button.setBackground(Theme.PANEL_LIGHT);
            button.setForeground(Theme.TEXT);
        }
        button.setOpaque(true);
        return button;
    }

    private JToggleButton styledToggle(String text) {
        JToggleButton button = new JToggleButton(text);
        button.setFocusPainted(false);
        button.setFont(Theme.FONT_HEADING.deriveFont(12f));
        button.setBackground(Theme.PANEL_LIGHT);
        button.setForeground(Theme.TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER), new EmptyBorder(7, 14, 7, 14)));
        button.setOpaque(true);
        return button;
    }

    // ---------------------------------------------------------------
    // Data refresh
    // ---------------------------------------------------------------
    private void startRefreshTimer() {
        // Swing Timer callbacks run on the EDT automatically, which is
        // what keeps this safe even though DispatchThread mutates the
        // same DispatchCenter data from a background thread.
        Timer timer = new Timer(500, e -> refreshLists());
        timer.start();
    }

    private void refreshLists() {
        List<Resource> resources = dc.allResources();
        List<Incident> incidents = dc.allIncidents();

        resourceTableModel.setRowCount(0);
        for (Resource r : resources) {
            resourceTableModel.addRow(new Object[]{r.getName(), r.getSpecialty(), r.getStatus()});
        }

        incidentTableModel.setRowCount(0);
        List<Incident> sorted = new java.util.ArrayList<>(incidents);
        Collections.sort(sorted); // Incident's Comparable ordering: severity desc, oldest first
        for (Incident i : sorted) {
            incidentTableModel.addRow(new Object[]{
                    i.getId(), i.getType(), i.getSeverity(), i.getStatus(), i.getDescription()});
        }

        mapPanel.updateData(resources, incidents);
    }

    // ---------------------------------------------------------------
    // Actions
    // ---------------------------------------------------------------
    private void onReportIncident() {
        int defaultX = pendingPin != null ? pendingPin.x : random.nextInt(360) + 20;
        int defaultY = pendingPin != null ? pendingPin.y : random.nextInt(220) + 20;

        ReportIncidentDialog dialog = new ReportIncidentDialog(this, defaultX, defaultY);
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) return;

        try {
            ClassificationResult result = classifier.classify(dialog.getDescriptionText());

            String id = "i" + incidentSeq.getAndIncrement();
            Incident incident = new Incident(id, dialog.getDescriptionText(),
                    dialog.getIncidentX(), dialog.getIncidentY());
            incident.setType(result.getType());
            incident.setSeverity(result.getSeverity());
            incident.setConfidence(result.getConfidence());
            incident.setReasoning(result.getReasoning());

            dc.addIncident(incident);
            logger.log("REPORTED " + incident);
            statusLabel.setText("● REPORTED " + id + " — " + result.getSeverity());
            statusLabel.setForeground(Theme.severityColor(result.getSeverity()));
        } catch (InvalidReportException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Invalid report", JOptionPane.WARNING_MESSAGE);
        } finally {
            pendingPin = null;
            pinButton.setSelected(false);
            mapPanel.setPin(null);
            refreshLists();
        }
    }

    private void onRunAllocation() {
        List<Incident> pending = dc.pendingIncidentsBySeverity();
        if (pending.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pending incidents to allocate.");
            return;
        }

        StringBuilder summary = new StringBuilder();
        for (Incident incident : pending) {
            try {
                Resource assigned = allocationService.allocate(incident, dc.allResources());
                double dist = GeoUtils.distance(incident, assigned);
                long travelMillis = (long) (dist * assigned.getResponseTimeFactor() * 5);

                logger.log("ASSIGNED " + incident.getId() + " -> " + assigned.getId());
                summary.append(assigned.getName()).append(" -> incident ")
                        .append(incident.getId()).append('\n');

                new DispatchThread(incident, assigned, travelMillis).start();
            } catch (NoAvailableResourceException ex) {
                summary.append("(unmatched) ").append(ex.getMessage()).append('\n');
            }
        }

        JOptionPane.showMessageDialog(this, summary.toString(),
                "Allocation results", JOptionPane.INFORMATION_MESSAGE);
        refreshLists();
    }

    private void onShowStats() {
        JOptionPane.showMessageDialog(this, dc.statsView().summaryString(),
                "VANGUARD Summary", JOptionPane.PLAIN_MESSAGE);
    }

    private void onViewLog() {
        JTextArea area = new JTextArea(logger.readLog(), 20, 60);
        area.setEditable(false);
        area.setFont(Theme.FONT_MONO);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Audit Log", JOptionPane.PLAIN_MESSAGE);
    }

    private void onSaveState() {
        serializer.save(dc.allIncidents(), dc.allResources());
        JOptionPane.showMessageDialog(this, "State saved to disk (vanguard_state.ser).");
    }

    private void onLoadState() {
        StateSerializer.SavedState state = serializer.load();
        if (state == null) {
            JOptionPane.showMessageDialog(this, "No saved state found.");
            return;
        }
        for (Incident i : state.incidents) dc.addIncident(i);
        for (Resource r : state.resources) dc.registerResource(r);
        refreshLists();
        JOptionPane.showMessageDialog(this, "Restored " + state.incidents.size()
                + " incidents and " + state.resources.size() + " resources.");
    }

    /**
     * Real JDBC write against MySQL. Requires sql/schema.sql to have
     * been run and db/DatabaseConnection.java's credentials to be
     * correct — if MySQL isn't reachable, this fails gracefully with
     * a dialog instead of crashing the app.
     */
    private void onSaveToDatabase() {
        try {
            for (Resource r : dc.allResources()) resourceDAO.save(r);
            for (Incident i : dc.allIncidents()) incidentDAO.save(i);
            JOptionPane.showMessageDialog(this, "Saved "
                    + dc.allResources().size() + " resources and "
                    + dc.allIncidents().size() + " incidents to MySQL.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save to MySQL:\n" + ex.getMessage()
                            + "\n\nCheck that MySQL is running, sql/schema.sql has been "
                            + "executed, and db/DatabaseConnection.java has the right credentials.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onLoadFromDatabase() {
        try {
            List<Resource> resources = resourceDAO.findAll();
            List<Incident> incidents = incidentDAO.findAll();
            for (Resource r : resources) dc.registerResource(r);
            for (Incident i : incidents) dc.addIncident(i);
            refreshLists();
            JOptionPane.showMessageDialog(this, "Loaded " + resources.size()
                    + " resources and " + incidents.size() + " incidents from MySQL.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load from MySQL:\n" + ex.getMessage()
                            + "\n\nCheck that MySQL is running, sql/schema.sql has been "
                            + "executed, and db/DatabaseConnection.java has the right credentials.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------------------------------------------------
    // Cell renderers — static nested classes (no outer-instance needed)
    // ---------------------------------------------------------------
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int col) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
            if (!isSelected) {
                boolean available = "AVAILABLE".equals(String.valueOf(value));
                label.setForeground(available ? Theme.GREEN : Theme.TEXT_DIM);
            }
            return label;
        }
    }

    private static class SeverityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int col) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
            if (!isSelected && value instanceof Severity) {
                label.setForeground(Theme.severityColor((Severity) value));
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            }
            return label;
        }
    }
}
