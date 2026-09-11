package vanguard.repository;

import java.util.*;

/**
 * A minimal generic, in-memory repository. <T> is the entity type,
 * <ID> is its key type. Writing our own generic class (rather than
 * just using ArrayList<Incident> directly) is the clearest way to
 * demonstrate Java's generics syntax: the type parameters are
 * declared once here and enforced everywhere this class is used.
 */
public class GenericRepository<T, ID> {

    private final Map<ID, T> store = new LinkedHashMap<>();

    public void save(ID id, T item) {
        store.put(id, item);
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(ID id) {
        store.remove(id);
    }

    public int count() {
        return store.size();
    }
}
