package vanguard.exceptions;

public class NoAvailableResourceException extends VanguardException {
    public NoAvailableResourceException(String incidentId) {
        super("No available resource could be matched to incident: " + incidentId);
    }
}
