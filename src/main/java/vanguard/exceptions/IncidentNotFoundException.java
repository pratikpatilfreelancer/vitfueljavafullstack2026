package vanguard.exceptions;

public class IncidentNotFoundException extends VanguardException {
    public IncidentNotFoundException(String incidentId) {
        super("No incident found with id: " + incidentId);
    }
}
