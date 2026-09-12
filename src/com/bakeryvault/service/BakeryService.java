package com.bakeryvault.service;

import com.bakeryvault.exceptions.BakeryException;
import com.bakeryvault.exceptions.CustomerLimitExceededException;
import com.bakeryvault.exceptions.CustomerNotFoundException;
import com.bakeryvault.exceptions.ItemNotFoundException;
import com.bakeryvault.exceptions.ItemOutOfStockException;
import com.bakeryvault.interfaces.Perishable;
import com.bakeryvault.model.BakedGood;
import com.bakeryvault.model.Customer;
import com.bakeryvault.model.Order;
import com.bakeryvault.repository.CustomerRepository;
import com.bakeryvault.repository.ItemRepository;
import com.bakeryvault.utils.FileManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Facade Design Pattern: this is the ONLY class the rest of the application
 * (Main) talks to for bakery operations. It hides the repositories, the
 * exception plumbing, and the file persistence details behind a small,
 * readable API - "purchaseItem", "refundItem", "search" - instead of
 * making callers juggle ItemRepository + CustomerRepository + FileManager
 * directly.
 */
public class BakeryService {

    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final FileManager fileManager;

    public BakeryService() {
        BakeryInventory inventory = BakeryInventory.getInstance();
        this.itemRepository = inventory.getItemRepository();
        this.customerRepository = inventory.getCustomerRepository();
        this.fileManager = inventory.getFileManager();
    }

    // ---------- Registration ----------

    public void registerItem(BakedGood item) {
        itemRepository.save(item);
    }

    public void registerCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    // ---------- Purchase / Refund / Freshness ----------

    /**
     * Synchronized because check-then-act on item availability (read
     * isAvailable(), then setAvailable(false)) is not atomic. Without this,
     * two threads racing on the same item could both pass the availability
     * check before either flips the flag - see CheckoutTask / Main's
     * concurrent-purchase demo.
     */
    public synchronized Order purchaseItem(String itemId, String customerId)
            throws ItemNotFoundException, CustomerNotFoundException,
                   ItemOutOfStockException, CustomerLimitExceededException {

        BakedGood item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        if (!item.isAvailable()) {
            throw new ItemOutOfStockException(itemId);
        }
        if (customer.isAtPurchaseLimit()) {
            throw new CustomerLimitExceededException(customerId, Customer.MAX_PURCHASE_LIMIT);
        }

        item.setAvailable(false); // no-op for Cake due to its override - polymorphism at work
        customer.addPurchasedItem(itemId);

        LocalDate expirationDate = (item instanceof Perishable)
                ? ((Perishable) item).getExpirationDate()
                : null;
        Order order = new Order(itemId, customerId, Order.Type.PURCHASE, expirationDate);
        logOrder(order);
        return order;
    }

    public Order refundItem(String itemId, String customerId)
            throws ItemNotFoundException, CustomerNotFoundException {

        BakedGood item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        item.setAvailable(true);
        customer.removePurchasedItem(itemId);

        Order order = new Order(itemId, customerId, Order.Type.REFUND, null);
        logOrder(order);
        return order;
    }

    /** Returns days remaining before the item expires; throws if it isn't a Perishable type. */
    public long checkFreshness(String itemId, String customerId) throws ItemNotFoundException, BakeryException {
        BakedGood item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (!(item instanceof Perishable)) {
            throw new BakeryException(item.getItemType()
                    + " items are baked fresh and sold same-day - no expiration to track.");
        }
        Perishable perishable = (Perishable) item;
        logOrder(new Order(itemId, customerId, Order.Type.FRESHNESS_CHECK, perishable.getExpirationDate()));
        return perishable.getDaysUntilExpiration();
    }

    private void logOrder(Order order) {
        try {
            fileManager.appendOrderLog(order);
        } catch (IOException e) {
            // Logging failure shouldn't crash a purchase - surface it, don't propagate.
            System.err.println("Warning: could not write order log - " + e.getMessage());
        }
    }

    // ---------- Search & Reporting (Streams + Functional Interfaces) ----------

    public List<BakedGood> findAll() {
        return itemRepository.findAll();
    }

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    /** Generic predicate-based search - reusable for any filtering criterion. */
    public List<BakedGood> search(Predicate<BakedGood> criteria) {
        return itemRepository.findAll().stream()
                .filter(criteria)
                .collect(Collectors.toList());
    }

    public List<BakedGood> searchByNameContains(String keyword) {
        return search(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()));
    }

    public List<BakedGood> searchByBaker(String baker) {
        return search(item -> item.getBaker().equalsIgnoreCase(baker));
    }

    public List<BakedGood> findAvailableItems() {
        return search(BakedGood::isAvailable); // method reference
    }

    public List<BakedGood> sortedByName() {
        return itemRepository.findAll().stream()
                .sorted(Comparator.comparing(BakedGood::getName))
                .collect(Collectors.toList());
    }

    public List<BakedGood> sortedBy(Comparator<BakedGood> comparator) {
        return itemRepository.findAll().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /** Groups the inventory by category using Collectors.groupingBy. */
    public Map<String, List<BakedGood>> groupByCategory() {
        return itemRepository.findAll().stream()
                .collect(Collectors.groupingBy(BakedGood::getItemType));
    }

    /** Counts items per category using Collectors.groupingBy + counting. */
    public Map<String, Long> countByCategory() {
        return itemRepository.findAll().stream()
                .collect(Collectors.groupingBy(BakedGood::getItemType, Collectors.counting()));
    }

    public long countAvailable() {
        return itemRepository.findAll().stream()
                .filter(BakedGood::isAvailable)
                .count();
    }

    /** Demonstrates reduce(): builds a single combined summary string. */
    public String buildInventorySummary() {
        return itemRepository.findAll().stream()
                .map(BakedGood::getName)
                .reduce("", (acc, name) -> acc.isEmpty() ? name : acc + ", " + name);
    }

    /** Demonstrates limit()/skip() - simple pagination over the inventory. */
    public List<BakedGood> page(int pageNumber, int pageSize) {
        return itemRepository.findAll().stream()
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    // ---------- Persistence ----------

    public void saveToDisk() throws IOException {
        fileManager.saveItems(itemRepository.findAll());
        fileManager.saveCustomers(customerRepository.findAll());
    }

    public void loadFromDisk() throws IOException, ClassNotFoundException {
        itemRepository.replaceAll(fileManager.loadItems());
        customerRepository.replaceAll(fileManager.loadCustomers());
    }

    public List<String> readOrderHistory() throws IOException {
        return fileManager.readOrderLog();
    }
}
