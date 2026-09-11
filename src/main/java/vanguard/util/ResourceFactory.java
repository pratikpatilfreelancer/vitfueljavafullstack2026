package vanguard.util;

import vanguard.model.*;

/**
 * Factory Design Pattern: callers ask for a resource "by type" without
 * needing to know or import every concrete subclass themselves. Adding
 * a new resource type later means changing this one switch statement,
 * not every place resources get created.
 */
public final class ResourceFactory {

    private ResourceFactory() { }

    public static Resource create(IncidentType type, String id, String name, int x, int y) {
        switch (type) {
            case FIRE:
                return new FireTruck(id, name, x, y);
            case MEDICAL:
                return new Ambulance(id, name, x, y);
            case STRUCTURAL:
                return new RescueTeam(id, name, x, y);
            case FLOOD:
                return new FloodBoat(id, name, x, y);
            case OTHER:
            default:
                return new VolunteerUnit(id, name, x, y);
        }
    }
}
