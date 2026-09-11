package vanguard.concurrent;

import vanguard.model.Incident;
import vanguard.model.IncidentStatus;
import vanguard.model.Resource;
import vanguard.util.DispatchCenter;

/**
 * Simulates a resource physically travelling to and resolving an
 * incident. Created by extending java.lang.Thread directly — one of
 * the two standard ways to create a thread in Java (see
 * HeartbeatMonitor for the other: implementing Runnable).
 */
public class DispatchThread extends Thread {

    private final Incident incident;
    private final Resource resource;
    private final long travelMillis;

    public DispatchThread(Incident incident, Resource resource, long travelMillis) {
        super("Dispatch-" + resource.getId());
        this.incident = incident;
        this.resource = resource;
        this.travelMillis = travelMillis;
    }

    @Override
    public void run() {
        try {
            System.out.printf("  -> %s en route to incident %s (ETA %dms)%n",
                    resource.getName(), incident.getId(), travelMillis);
            Thread.sleep(travelMillis);

            // Mutating shared state from a background thread, so it goes
            // through DispatchCenter's synchronized resolve() method to
            // avoid two threads corrupting the same collections at once.
            DispatchCenter.getInstance().resolveIncident(incident, resource);

            System.out.printf("  -> %s arrived. Incident %s marked RESOLVED.%n",
                    resource.getName(), incident.getId());
        } catch (InterruptedException e) {
            // Restore the interrupt status rather than swallowing it —
            // standard practice when catching InterruptedException.
            Thread.currentThread().interrupt();
            System.out.println("  -> Dispatch to " + incident.getId() + " was interrupted.");
        }
    }
}
