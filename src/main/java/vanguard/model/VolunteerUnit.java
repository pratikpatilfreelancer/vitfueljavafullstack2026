package vanguard.model;

public class VolunteerUnit extends Resource {
    private static final long serialVersionUID = 1L;

    public VolunteerUnit(String id, String name, int x, int y) {
        super(id, name, x, y);
    }

    @Override
    public IncidentType getSpecialty() {
        return IncidentType.OTHER;
    }

    @Override
    public int getResponseTimeFactor() {
        return 1;
    }
}
