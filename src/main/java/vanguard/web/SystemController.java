package vanguard.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vanguard.concurrent.DispatchThread;
import vanguard.db.IncidentDAO;
import vanguard.db.ResourceDAO;
import vanguard.exceptions.NoAvailableResourceException;
import vanguard.io.AuditLogger;
import vanguard.model.Incident;
import vanguard.model.Resource;
import vanguard.service.AllocationService;
import vanguard.util.DispatchCenter;
import vanguard.util.GeoUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Everything that isn't specifically about one incident or one
 * resource: running the allocation batch, reading stats/logs, and
 * the MySQL save/load actions — the same operations the GUI's bottom
 * toolbar exposes as buttons, here exposed as endpoints instead.
 */
@RestController
@RequestMapping("/api")
public class SystemController {

    private final DispatchCenter dc = DispatchCenter.getInstance();
    private final AllocationService allocationService = new AllocationService();
    private final AuditLogger logger = new AuditLogger();
    private final IncidentDAO incidentDAO = new IncidentDAO();
    private final ResourceDAO resourceDAO = new ResourceDAO();

    @PostMapping("/allocate")
    public List<String> runAllocation() {
        List<Incident> pending = dc.pendingIncidentsBySeverity();
        List<String> results = new ArrayList<>();

        for (Incident incident : pending) {
            try {
                Resource assigned = allocationService.allocate(incident, dc.allResources());
                double dist = GeoUtils.distance(incident, assigned);
                long travelMillis = (long) (dist * assigned.getResponseTimeFactor() * 5);

                logger.log("ASSIGNED " + incident.getId() + " -> " + assigned.getId());
                results.add(assigned.getName() + " -> incident " + incident.getId());

                new DispatchThread(incident, assigned, travelMillis).start();
            } catch (NoAvailableResourceException e) {
                results.add("(unmatched) " + e.getMessage());
            }
        }
        return results;
    }

    @GetMapping("/stats")
    public String stats() {
        return dc.statsView().summaryString();
    }

    @GetMapping("/audit-log")
    public String auditLog() {
        return logger.readLog();
    }

    @PostMapping("/persist/save")
    public ResponseEntity<?> saveToDatabase() {
        try {
            for (Resource r : dc.allResources()) resourceDAO.save(r);
            for (Incident i : dc.allIncidents()) incidentDAO.save(i);
            return ResponseEntity.ok(
                    "Saved " + dc.allResources().size() + " resources and "
                            + dc.allIncidents().size() + " incidents to MySQL.");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Could not save to MySQL: " + e.getMessage()));
        }
    }

    @PostMapping("/persist/load")
    public ResponseEntity<?> loadFromDatabase() {
        try {
            List<Resource> resources = resourceDAO.findAll();
            List<Incident> incidents = incidentDAO.findAll();
            for (Resource r : resources) dc.registerResource(r);
            for (Incident i : incidents) dc.addIncident(i);
            return ResponseEntity.ok(
                    "Loaded " + resources.size() + " resources and "
                            + incidents.size() + " incidents from MySQL.");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Could not load from MySQL: " + e.getMessage()));
        }
    }
}
