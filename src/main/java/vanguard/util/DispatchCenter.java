package vanguard.util;

import vanguard.exceptions.IncidentNotFoundException;
import vanguard.model.*;
import vanguard.repository.GenericRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton Design Pattern: exactly one DispatchCenter exists for the
 * whole application, reached via getInstance(). This is the single
 * source of truth for incidents and resources, so background threads
 * (DispatchThread, HeartbeatMonitor) and the console menu are always
 * looking at the same shared state.
 */
public class DispatchCenter {

    private static DispatchCenter instance;

    private final GenericRepository<Incident, String> incidents = new GenericRepository<>();
    private final GenericRepository<Resource, String> resources = new GenericRepository<>();

    private DispatchCenter() { }

    public static synchronized DispatchCenter getInstance() {
        if (instance == null) {
            instance = new DispatchCenter();
        }
        return instance;
    }

    // ---- Resources ----

    public synchronized void registerResource(Resource resource) {
        resources.save(resource.getId(), resource);
    }

    public synchronized List<Resource> allResources() {
        return resources.findAll();
    }

    public synchronized long countAvailableResources() {
        return resources.findAll().stream().filter(Resource::isAvailable).count();
    }

    // ---- Incidents ----

    public synchronized void addIncident(Incident incident) {
        incidents.save(incident.getId(), incident);
    }

    public synchronized Incident getIncident(String id) throws IncidentNotFoundException {
        return incidents.findById(id).orElseThrow(() -> new IncidentNotFoundException(id));
    }

    public synchronized List<Incident> allIncidents() {
        return incidents.findAll();
    }

    /**
     * Pending incidents, sorted using Incident's natural ordering
     * (severity desc, then oldest first) via a TreeSet — demonstrates
     * NavigableSet/TreeSet backed by Comparable, built from a Stream.
     */
    public synchronized List<Incident> pendingIncidentsBySeverity() {
        TreeSet<Incident> sorted = incidents.findAll().stream()
                .filter(i -> i.getStatus() == IncidentStatus.REPORTED)
                .collect(Collectors.toCollection(TreeSet::new));
        return new ArrayList<>(sorted);
    }

    public synchronized long countActiveIncidents() {
        return incidents.findAll().stream()
                .filter(i -> i.getStatus() != IncidentStatus.RESOLVED)
                .count();
    }

    /**
     * Called by DispatchThread once a resource "arrives". Synchronized
     * so a background dispatch thread can never race with the console
     * thread reading/writing the same collections.
     */
    public synchronized void resolveIncident(Incident incident, Resource resource) {
        incident.setStatus(IncidentStatus.RESOLVED);
        resource.markAvailable();
    }

    /**
     * Groups active incidents by severity — a Stream/Collectors example
     * (Collectors.groupingBy) distinct from the TreeSet-based sort above.
     */
    public synchronized Map<Severity, Long> countBySeverity() {
        return incidents.findAll().stream()
                .filter(i -> i.getStatus() != IncidentStatus.RESOLVED)
                .collect(Collectors.groupingBy(Incident::getSeverity, Collectors.counting()));
    }

    /**
     * Non-static inner class: it can reach the outer DispatchCenter's
     * private fields directly (via incidents/resources) without them
     * being passed in — the key difference from a static nested class
     * like GenericRepository, which needs no outer instance at all.
     */
    public class StatsView {
        public void printSummary() {
            System.out.println(summaryString());
        }

        /** Same summary as printSummary(), returned as a String for GUI use. */
        public String summaryString() {
            return "=== VANGUARD Summary ===\n"
                    + "Total incidents logged : " + incidents.count() + "\n"
                    + "Active incidents       : " + countActiveIncidents() + "\n"
                    + "Total resources         : " + resources.count() + "\n"
                    + "Available resources     : " + countAvailableResources() + "\n"
                    + "By severity (active)    : " + countBySeverity();
        }
    }

    public StatsView statsView() {
        return new StatsView();
    }
}
