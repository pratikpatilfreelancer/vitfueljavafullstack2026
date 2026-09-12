package com.bakeryvault.repository;

import com.bakeryvault.model.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerRepository implements Repository<Customer, String> {

    private final Map<String, Customer> customers = new HashMap<>();

    @Override
    public synchronized void save(Customer entity) {
        customers.put(entity.getCustomerId(), entity);
    }

    @Override
    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public synchronized boolean deleteById(String id) {
        return customers.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return customers.containsKey(id);
    }

    public synchronized void replaceAll(List<Customer> newCustomers) {
        customers.clear();
        for (Customer c : newCustomers) {
            customers.put(c.getCustomerId(), c);
        }
    }
}
