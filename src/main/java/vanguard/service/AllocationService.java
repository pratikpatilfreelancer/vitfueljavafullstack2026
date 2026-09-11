package vanguard.service;

import vanguard.exceptions.NoAvailableResourceException;
import vanguard.model.*;
import vanguard.util.GeoUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Matches a pending incident to the nearest available resource of the
 * right specialty. Built almost entirely with the Stream API:
 * filter() to narrow candidates, sorted()/Comparator to rank by
 * distance, and findFirst() to pick the winner.
 */
public class AllocationService {

    /**
     * @throws NoAvailableResourceException if no resource, of any type,
     *         is currently available to send.
     */
    public Resource allocate(Incident incident, List<Resource> resources)
            throws NoAvailableResourceException {

        // First choice: an available resource matching the incident's type.
        Optional<Resource> bestMatch = resources.stream()
                .filter(Resource::isAvailable)
                .filter(r -> r.getSpecialty() == incident.getType())
                .sorted(Comparator.comparingDouble(r -> GeoUtils.distance(r, incident)))
                .findFirst();

        // Fallback: any available resource at all, nearest first.
        if (bestMatch.isEmpty()) {
            bestMatch = resources.stream()
                    .filter(Resource::isAvailable)
                    .sorted(Comparator.comparingDouble(r -> GeoUtils.distance(r, incident)))
                    .findFirst();
        }

        Resource chosen = bestMatch.orElseThrow(
                () -> new NoAvailableResourceException(incident.getId()));

        chosen.markBusy();
        incident.setAssignedResourceId(chosen.getId());
        incident.setStatus(IncidentStatus.ASSIGNED);
        return chosen;
    }
}
