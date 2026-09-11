package vanguard.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base for every kind of emergency resource (fire truck,
 * ambulance, etc). Concrete behaviour that differs per resource type
 * (which incident type it specialises in, how fast it responds) is
 * left abstract here and implemented by each subclass — a direct
 * demonstration of abstraction + polymorphism.
 */
public abstract class Resource implements Locatable, Serializable {

    private static final long serialVersionUID = 1L;

    // Encapsulation: fields are private, exposed only through getters
    // and the controlled mutators markBusy()/markAvailable().
    private final String id;
    private final String name;
    private final int x;
    private final int y;
    private ResourceStatus status;

    protected Resource(String id, String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.status = ResourceStatus.AVAILABLE;
    }

    // Abstract methods — every subclass MUST define these.
    public abstract IncidentType getSpecialty();
    public abstract int getResponseTimeFactor(); // lower = faster responder

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }

    public ResourceStatus getStatus() { return status; }

    public boolean isAvailable() {
        return status == ResourceStatus.AVAILABLE;
    }

    public void markBusy() { this.status = ResourceStatus.BUSY; }
    public void markAvailable() { this.status = ResourceStatus.AVAILABLE; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource other = (Resource) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) @ (%d,%d) - %s",
                id, name, getSpecialty(), x, y, status);
    }
}
