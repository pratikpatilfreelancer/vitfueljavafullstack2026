package vanguard.io;

import vanguard.model.Incident;
import vanguard.model.Resource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Saves/restores the whole system state to disk using Java Object
 * Serialization (ObjectOutputStream/ObjectInputStream). Incident and
 * every Resource subclass implement Serializable for this to work.
 */
public class StateSerializer {

    private static final String STATE_FILE = "vanguard_state.ser";

    public void save(List<Incident> incidents, List<Resource> resources) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(STATE_FILE))) {
            out.writeObject(incidents);
            out.writeObject(resources);
            System.out.println("State saved to " + STATE_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save state: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public SavedState load() {
        Path path = Paths.get(STATE_FILE);
        if (!Files.exists(path)) {
            System.out.println("No saved state found at " + STATE_FILE);
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(STATE_FILE))) {
            List<Incident> incidents = (List<Incident>) in.readObject();
            List<Resource> resources = (List<Resource>) in.readObject();
            System.out.println("State loaded from " + STATE_FILE);
            return new SavedState(incidents, resources);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load state: " + e.getMessage());
            return null;
        }
    }

    /** Simple holder returned by load() bundling both lists together. */
    public static class SavedState {
        public final List<Incident> incidents;
        public final List<Resource> resources;

        public SavedState(List<Incident> incidents, List<Resource> resources) {
            this.incidents = incidents;
            this.resources = resources;
        }
    }
}
