package com.bakeryvault.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository contract. T is the entity type, ID is its key type.
 * Demonstrates Generics used meaningfully - both ItemRepository and
 * CustomerRepository implement the same contract with different type args.
 */
public interface Repository<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
    boolean existsById(ID id);
}
