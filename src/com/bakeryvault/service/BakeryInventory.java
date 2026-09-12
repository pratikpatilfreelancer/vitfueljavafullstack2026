package com.bakeryvault.service;

import com.bakeryvault.repository.CustomerRepository;
import com.bakeryvault.repository.ItemRepository;
import com.bakeryvault.utils.FileManager;

/**
 * Singleton Design Pattern: the entire application shares exactly one
 * inventory (one ItemRepository, one CustomerRepository, one FileManager),
 * accessed through a single global point. Thread-safe lazy initialization
 * via the "initialization-on-demand holder" idiom (no extra synchronization
 * cost on repeated calls).
 */
public final class BakeryInventory {

    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final FileManager fileManager;

    private BakeryInventory() {
        this.itemRepository = new ItemRepository();
        this.customerRepository = new CustomerRepository();
        this.fileManager = new FileManager("data");
    }

    private static class Holder {
        private static final BakeryInventory INSTANCE = new BakeryInventory();
    }

    public static BakeryInventory getInstance() {
        return Holder.INSTANCE;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
