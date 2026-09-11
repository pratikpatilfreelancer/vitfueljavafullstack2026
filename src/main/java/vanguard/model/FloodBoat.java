package vanguard.model;

public class FloodBoat extends Resource {
    private static final long serialVersionUID = 1L;

    public FloodBoat(String id, String name, int x, int y) {
        super(id, name, x, y);
    }

    @Override
    public IncidentType getSpecialty() {
        return IncidentType.FLOOD;
    }

    @Override
    public int getResponseTimeFactor() {
        return 2;
    }
}
