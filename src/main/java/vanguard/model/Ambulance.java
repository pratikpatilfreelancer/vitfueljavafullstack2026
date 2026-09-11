package vanguard.model;

public class Ambulance extends Resource {
    private static final long serialVersionUID = 1L;

    public Ambulance(String id, String name, int x, int y) {
        super(id, name, x, y);
    }

    @Override
    public IncidentType getSpecialty() {
        return IncidentType.MEDICAL;
    }

    @Override
    public int getResponseTimeFactor() {
        return 1; // ambulances are fast responders
    }
}
