package vanguard.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vanguard.exceptions.IncidentNotFoundException;
import vanguard.model.Incident;
import vanguard.service.ClassificationResult;
import vanguard.service.IncidentClassifier;
import vanguard.service.DatabaseBackedClassifier;
import vanguard.util.DispatchCenter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final DispatchCenter dc = DispatchCenter.getInstance();
    private final IncidentClassifier classifier = new DatabaseBackedClassifier();
    private final AtomicInteger incidentSeq = new AtomicInteger(1);

    @GetMapping
    public List<Incident> listAll() {
        List<Incident> all = dc.allIncidents();
        Collections.sort(all); // Incident's Comparable ordering
        return all;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getOne(@PathVariable String id) throws IncidentNotFoundException {
        return ResponseEntity.ok(dc.getIncident(id));
    }

    @PostMapping
    public ResponseEntity<Incident> report(@RequestBody IncidentRequest request) {
        ClassificationResult result = classifier.classify(request.getDescription());

        String id = "i" + incidentSeq.getAndIncrement();
        Incident incident = new Incident(id, request.getDescription(), request.getX(), request.getY());
        incident.setType(result.getType());
        incident.setSeverity(result.getSeverity());
        incident.setConfidence(result.getConfidence());
        incident.setReasoning(result.getReasoning());

        dc.addIncident(incident);
        return ResponseEntity.status(HttpStatus.CREATED).body(incident);
    }
}
