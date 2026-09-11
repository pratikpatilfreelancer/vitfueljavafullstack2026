package vanguard;

import vanguard.concurrent.DispatchThread;
import vanguard.concurrent.HeartbeatMonitor;
import vanguard.exceptions.IncidentNotFoundException;
import vanguard.exceptions.InvalidReportException;
import vanguard.exceptions.NoAvailableResourceException;
import vanguard.exceptions.VanguardException;
import vanguard.io.AuditLogger;
import vanguard.io.StateSerializer;
import vanguard.model.*;
import vanguard.service.AllocationService;
import vanguard.service.ClassificationResult;
import vanguard.service.IncidentClassifier;
import vanguard.service.DatabaseBackedClassifier;
import vanguard.util.AnnotationScanner;
import vanguard.util.DispatchCenter;
import vanguard.util.GeoUtils;
import vanguard.util.Loggable;
import vanguard.util.ResourceFactory;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {

    private static final DispatchCenter dc = DispatchCenter.getInstance();
    private static final IncidentClassifier classifier = new DatabaseBackedClassifier();
    private static final AllocationService allocationService = new AllocationService();
    private static final AuditLogger logger = new AuditLogger();
    private static final StateSerializer serializer = new StateSerializer();
    private static final AtomicInteger incidentSeq = new AtomicInteger(1);

    private static HeartbeatMonitor heartbeatMonitor;
    private static Thread heartbeatThread;
    private static final Object heartbeatLock = new Object();

    public static void main(String[] args) {
        seedResources();
        startHeartbeat();

        // A lambda satisfying the same functional interface as
        // RuleBasedClassifier, shown here purely to demonstrate that
        // IncidentClassifier can be implemented either way.
        IncidentClassifier quickLambdaClassifier = description ->
                new ClassificationResult(IncidentType.OTHER, Severity.LOW, 10, "Unclassified stub");
        // (not used by the menu — kept as a syllabus demonstration point)

        AnnotationScanner.scan(AllocationService.class);

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1": reportIncident(scanner); break;
                        case "2": runAllocation(); break;
                        case "3": listIncidents(); break;
                        case "4": listResources(); break;
                        case "5": logger.printLog(); break;
                        case "6": saveState(); break;
                        case "7": loadState(); break;
                        case "8": dc.statsView().printSummary(); break;
                        case "9": running = false; break;
                        default: System.out.println("Unrecognized option.");
                    }
                } catch (InvalidReportException e) {
                    // Unchecked exception example — caught here at the
                    // boundary of the app rather than forced on every caller.
                    System.out.println("Invalid input: " + e.getMessage());
                } catch (VanguardException e) {
                    // Catches IncidentNotFoundException / NoAvailableResourceException
                    // and any other checked VanguardException subtype generically.
                    System.out.println("Operation failed: " + e.getMessage());
                } finally {
                    System.out.println(); // always print a spacer, success or failure
                }
            }
        } finally {
            shutdownHeartbeat();
            System.out.println("VANGUARD shutting down. Stay safe.");
        }
    }

    private static void printMenu() {
        System.out.println("===== VANGUARD =====");
        System.out.println("1. Report incident");
        System.out.println("2. Run allocation");
        System.out.println("3. List incidents");
        System.out.println("4. List resources");
        System.out.println("5. View audit log");
        System.out.println("6. Save state to disk");
        System.out.println("7. Load state from disk");
        System.out.println("8. Show summary stats");
        System.out.println("9. Exit");
        System.out.print("Choose an option: ");
    }

    private static void seedResources() {
        dc.registerResource(ResourceFactory.create(IncidentType.FIRE, "r1", "Engine 12", 60, 60));
        dc.registerResource(ResourceFactory.create(IncidentType.MEDICAL, "r2", "Medic 3", 120, 90));
        dc.registerResource(ResourceFactory.create(IncidentType.STRUCTURAL, "r3", "Rescue Alpha", 200, 130));
        dc.registerResource(ResourceFactory.create(IncidentType.FLOOD, "r4", "Flood Boat 2", 340, 40));
        dc.registerResource(ResourceFactory.create(IncidentType.OTHER, "r5", "Volunteer Unit A", 180, 220));
        System.out.println("Seeded 5 resources.\n");
    }

    private static void startHeartbeat() {
        heartbeatMonitor = new HeartbeatMonitor(heartbeatLock);
        // Thread created from a Runnable (see HeartbeatMonitor) — the
        // second of the two standard thread-creation styles in this project.
        heartbeatThread = new Thread(heartbeatMonitor, "Heartbeat");
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }

    private static void shutdownHeartbeat() {
        if (heartbeatMonitor != null) heartbeatMonitor.stop();
    }

    private static void reportIncident(Scanner scanner) {
        System.out.print("Describe the incident: ");
        String description = scanner.nextLine();

        System.out.print("X coordinate (0-400): ");
        int x = readInt(scanner, 0, 400);
        System.out.print("Y coordinate (0-260): ");
        int y = readInt(scanner, 0, 260);

        // classify() throws InvalidReportException (unchecked) on bad input.
        ClassificationResult result = classifier.classify(description);

        String id = "i" + incidentSeq.getAndIncrement();
        Incident incident = new Incident(id, description, x, y);
        incident.setType(result.getType());
        incident.setSeverity(result.getSeverity());
        incident.setConfidence(result.getConfidence());
        incident.setReasoning(result.getReasoning());

        dc.addIncident(incident);
        logger.log("REPORTED " + incident);
        System.out.println("Logged: " + incident);
    }

    @Loggable("resource-allocation")
    private static void runAllocation() throws VanguardException {
        List<Incident> pending = dc.pendingIncidentsBySeverity();
        if (pending.isEmpty()) {
            System.out.println("No pending incidents to allocate.");
            return;
        }

        for (Incident incident : pending) {
            try {
                Resource assigned = allocationService.allocate(incident, dc.allResources());
                double dist = GeoUtils.distance(incident, assigned);
                long travelMillis = (long) (dist * assigned.getResponseTimeFactor() * 5);

                logger.log("ASSIGNED " + incident.getId() + " -> " + assigned.getId());
                System.out.printf("Assigned %s to incident %s (distance %.1f)%n",
                        assigned.getName(), incident.getId(), dist);

                // Extending Thread directly (see DispatchThread) — the first
                // of the two standard thread-creation styles in this project.
                new DispatchThread(incident, assigned, travelMillis).start();

            } catch (NoAvailableResourceException e) {
                // Caught per-incident so one failed allocation doesn't stop
                // the rest of the batch from being processed.
                System.out.println("  " + e.getMessage());
            }
        }
    }

    private static void listIncidents() throws IncidentNotFoundException {
        List<Incident> all = dc.allIncidents();
        if (all.isEmpty()) {
            System.out.println("No incidents reported yet.");
            return;
        }
        for (Incident i : all) {
            System.out.println(i);
        }
    }

    private static void listResources() {
        List<Resource> all = dc.allResources();
        for (Resource r : all) {
            System.out.println(r);
        }
    }

    private static void saveState() {
        serializer.save(dc.allIncidents(), dc.allResources());
    }

    private static void loadState() {
        StateSerializer.SavedState state = serializer.load();
        if (state == null) return;
        for (Incident i : state.incidents) dc.addIncident(i);
        for (Resource r : state.resources) dc.registerResource(r);
        System.out.println("Restored " + state.incidents.size() + " incidents and "
                + state.resources.size() + " resources.");
    }

    private static int readInt(Scanner scanner, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.printf("Enter a value between %d and %d: ", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Not a number, try again: ");
            }
        }
    }
}
