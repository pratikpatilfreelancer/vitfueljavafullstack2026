package com.bakeryvault.repository;

import com.bakeryvault.model.BakedGood;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory store for BakedGood instances, keyed by itemId.
 * LinkedHashMap preserves insertion order, which makes inventory
 * listings predictable and easy to demo.
 */
public class ItemRepository implements Repository<BakedGood, String> {

    private final Map<String, BakedGood> items = new LinkedHashMap<>();

    @Override
    public synchronized void save(BakedGood entity) {
        items.put(entity.getItemId(), entity);
    }

    @Override
    public Optional<BakedGood> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<BakedGood> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public synchronized boolean deleteById(String id) {
        return items.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return items.containsKey(id);
    }

    public int count() {
        return items.size();
    }

    /** Replaces the entire in-memory store - used when loading from disk. */
    public synchronized void replaceAll(List<BakedGood> newItems) {
        items.clear();
        for (BakedGood item : newItems) {
            items.put(item.getItemId(), item);
        }
    }
}
