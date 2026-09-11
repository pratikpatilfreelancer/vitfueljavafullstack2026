package vanguard.model;

public class FireTruck extends Resource {
    private static final long serialVersionUID = 1L;

    public FireTruck(String id, String name, int x, int y) {
        super(id, name, x, y);
    }

    @Override
    public IncidentType getSpecialty() {
        return IncidentType.FIRE;
    }

    @Override
    public int getResponseTimeFactor() {
        return 2; // fire trucks are heavy, slightly slower per unit distance
    }
}
