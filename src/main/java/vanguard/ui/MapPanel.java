package vanguard.ui;

import vanguard.model.Incident;
import vanguard.model.IncidentStatus;
import vanguard.model.Resource;
import vanguard.model.Severity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom-painted tactical grid map. All positions in the domain model
 * (Resource/Incident) live on a fixed 400x260 logical grid; this panel
 * scales that grid to whatever pixel size it's actually given, so it
 * resizes cleanly with the window.
 */
public class MapPanel extends JPanel {

    private static final int LOGICAL_W = 400;
    private static final int LOGICAL_H = 260;

    private List<Resource> resources = new ArrayList<>();
    private List<Incident> incidents = new ArrayList<>();
    private Point pin;
    private PinListener pinListener;

    @FunctionalInterface
    public interface PinListener {
        void onPin(int x, int y);
    }

    public MapPanel() {
        setPreferredSize(new Dimension(640, 416));
        setBackground(Theme.BG);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (pinListener == null) return;
                double sx = getWidth() / (double) LOGICAL_W;
                double sy = getHeight() / (double) LOGICAL_H;
                int logicalX = (int) (e.getX() / sx);
                int logicalY = (int) (e.getY() / sy);
                pinListener.onPin(logicalX, logicalY);
            }
        });
    }

    public void setPinListener(PinListener listener) {
        this.pinListener = listener;
    }

    public void setPin(Point pin) {
        this.pin = pin;
        repaint();
    }

    public void updateData(List<Resource> resources, List<Incident> incidents) {
        this.resources = resources;
        this.incidents = incidents;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double sx = getWidth() / (double) LOGICAL_W;
        double sy = getHeight() / (double) LOGICAL_H;

        drawGrid(g2, sx, sy);
        drawResources(g2, sx, sy);
        drawIncidents(g2, sx, sy);
        drawPin(g2, sx, sy);
    }

    private void drawGrid(Graphics2D g2, double sx, double sy) {
        g2.setColor(Theme.BORDER);
        for (int gx = 0; gx <= LOGICAL_W; gx += 40) {
            int px = (int) (gx * sx);
            g2.drawLine(px, 0, px, getHeight());
        }
        for (int gy = 0; gy <= LOGICAL_H; gy += 40) {
            int py = (int) (gy * sy);
            g2.drawLine(0, py, getWidth(), py);
        }
    }

    private void drawResources(Graphics2D g2, double sx, double sy) {
        for (Resource r : resources) {
            int px = (int) (r.getX() * sx);
            int py = (int) (r.getY() * sy);
            g2.setColor(r.isAvailable() ? Theme.CYAN : new Color(58, 67, 86));
            g2.fillRect(px - 5, py - 5, 10, 10);
            g2.setColor(Theme.TEXT_DIM);
            g2.setFont(g2.getFont().deriveFont(10f));
            g2.drawString(r.getName(), px - 20, py - 8);
        }
    }

    private void drawIncidents(Graphics2D g2, double sx, double sy) {
        for (Incident i : incidents) {
            if (i.getStatus() == IncidentStatus.RESOLVED) continue;
            int px = (int) (i.getX() * sx);
            int py = (int) (i.getY() * sy);
            Color color = Theme.severityColor(i.getSeverity());
            // soft glow ring, then solid core — a bit more visually alive
            // than a flat dot, without needing an animation timer.
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
            g2.fillOval(px - 11, py - 11, 22, 22);
            g2.setColor(color);
            g2.fillOval(px - 6, py - 6, 12, 12);
        }
    }

    private void drawPin(Graphics2D g2, double sx, double sy) {
        if (pin == null) return;
        int px = (int) (pin.x * sx);
        int py = (int) (pin.y * sy);
        g2.setColor(Theme.TEXT);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(px - 8, py - 8, 16, 16);
    }
}
