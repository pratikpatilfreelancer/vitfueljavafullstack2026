package vanguard.model;

public class RescueTeam extends Resource {
    private static final long serialVersionUID = 1L;

    public RescueTeam(String id, String name, int x, int y) {
        super(id, name, x, y);
    }

    @Override
    public IncidentType getSpecialty() {
        return IncidentType.STRUCTURAL;
    }

    @Override
    public int getResponseTimeFactor() {
        return 3; // structural rescue requires setup time
    }
}
