package com.bakeryvault;

import com.bakeryvault.enums.ItemType;
import com.bakeryvault.exceptions.BakeryException;
import com.bakeryvault.factory.BakedGoodFactory;
import com.bakeryvault.model.BakedGood;
import com.bakeryvault.model.Customer;
import com.bakeryvault.model.Order;
import com.bakeryvault.service.BakeryService;
import com.bakeryvault.service.CheckoutTask;
import com.bakeryvault.utils.ItemComparators;
import com.bakeryvault.utils.ReportGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

/**
 * Console entry point for BakeryVault.
 *
 * This class owns the text menu loop and all user I/O (via {@link Scanner}).
 * It deliberately does NOT talk to the repositories or file system directly -
 * every operation is delegated to {@link BakeryService}, which acts as a
 * Facade over the repository/persistence layer. That keeps Main focused on
 * "ask the user for input, print the result" and nothing else.
 */
public class Main {

    // Single shared instances for the whole run of the program.
    // BakeryService internally wraps the Singleton BakeryInventory, so every
    // call here operates on the same in-memory inventory/customer data.
    private static final BakeryService bakeryService = new BakeryService();
    private static final ReportGenerator reportGenerator = new ReportGenerator();

    // One Scanner reads all console input for the entire application lifetime.
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Application entry point. Loads any previously saved data, seeds demo
     * records on a first run, then loops printing the menu and dispatching
     * to the matching handler method until the user chooses to exit.
     */
    public static void main(String[] args) {
        loadOnStartup();
        seedDemoDataIfEmpty();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                // Each case delegates to a dedicated private method - keeps
                // this switch statement as a pure "router" with no business
                // logic of its own.
                switch (choice) {
                    case "1":
                        addItem();
                        break;
                    case "2":
                        registerCustomer();
                        break;
                    case "3":
                        purchaseItem();
                        break;
                    case "4":
                        refundItem();
                        break;
                    case "5":
                        checkFreshness();
                        break;
                    case "6":
                        listInventory();
                        break;
                    case "7":
                        searchInventory();
                        break;
                    case "8":
                        showReports();
                        break;
                    case "9":
                        viewOrderHistory();
                        break;
                    case "10":
                        simulateConcurrentPurchase();
                        break;
                    case "11":
                        saveAndExit();
                        running = false;
                        break;
                    default:
                        out.println("Invalid option, try again.");
                }
            } catch (Exception e) {
                // Catch-all safety net: an unexpected runtime exception in any
                // handler should never crash the whole console session - it's
                // reported and the menu loop simply continues.
                out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    /** Prints the numbered list of menu options and the input prompt. */
    private static void printMenu() {
        out.println("\n===== BakeryVault =====");
        out.println("1.  Add item (Bread/Pastry/Cake)");
        out.println("2.  Register customer");
        out.println("3.  Purchase item");
        out.println("4.  Refund item");
        out.println("5.  Check freshness (expiration)");
        out.println("6.  List full inventory (sorted)");
        out.println("7.  Search inventory");
        out.println("8.  Reports (grouping, counts, streams demo)");
        out.println("9.  View order history (from file)");
        out.println("10. Simulate concurrent purchase (multithreading demo)");
        out.println("11. Save & Exit");
        out.print("Choose an option: ");
    }

    // ---------- Menu actions ----------

    /**
     * Menu option 1. Prompts for the common fields every {@link BakedGood}
     * shares (name, baker, price, dietary type), then branches to collect
     * the extra fields specific to Bread, Pastry, or Cake before handing
     * everything to {@link BakedGoodFactory} to construct the right subclass.
     * The factory is the only place that knows how to validate/build each
     * concrete type, so this method never calls a constructor directly.
     */
    private static void addItem() {
        out.println("Type: 1=Bread  2=Pastry  3=Cake");
        String type = scanner.nextLine().trim();
        out.print("Name: ");
        String name = scanner.nextLine();
        out.print("Baker: ");
        String baker = scanner.nextLine();

        double price;
        try {
            out.print("Price: ");
            price = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            // Bail out early on bad numeric input rather than proceeding
            // with a half-collected item.
            out.println("Invalid price entered: " + e.getMessage());
            return;
        }
        ItemType dietaryType = askDietaryType();

        try {
            BakedGood item;
            switch (type) {
                case "1":
                    // Bread-specific fields: SKU is validated inside the factory.
                    out.print("SKU (6-12 alphanumeric chars): ");
                    String sku = scanner.nextLine();
                    out.print("Bread type (e.g. Sourdough): ");
                    String breadType = scanner.nextLine();
                    out.print("Weight (grams): ");
                    int weightGrams = Integer.parseInt(scanner.nextLine().trim());
                    item = BakedGoodFactory.createBread(name, baker, sku, breadType, weightGrams, price, dietaryType);
                    break;
                case "2":
                    // Pastry-specific fields: no expiration/pre-order interfaces -
                    // pastries are baked fresh daily and sold same-day only.
                    out.print("Batch number: ");
                    String batch = scanner.nextLine();
                    out.print("Flavor: ");
                    String flavor = scanner.nextLine();
                    item = BakedGoodFactory.createPastry(name, baker, batch, flavor, price, dietaryType);
                    break;
                case "3":
                    // Cake-specific fields: made to order, so it's always in
                    // stock regardless of what gets purchased.
                    out.print("Cake size (e.g. 8-inch): ");
                    String size = scanner.nextLine();
                    out.print("Weight (kg): ");
                    double weightKg = Double.parseDouble(scanner.nextLine().trim());
                    item = BakedGoodFactory.createCake(name, baker, size, weightKg, price, dietaryType);
                    break;
                default:
                    out.println("Invalid type.");
                    return;
            }
            bakeryService.registerItem(item);
            out.println("Added: " + item.displayInfo() + " [id=" + item.getItemId() + "]");
        } catch (BakeryException e) {
            // Thrown by the factory when, e.g., the SKU fails validation.
            out.println("Could not add item: " + e.getMessage());
        } catch (NumberFormatException e) {
            // Thrown if weight/size fields aren't parseable numbers.
            out.println("Invalid number entered: " + e.getMessage());
        }
    }

    /**
     * Prompts for a dietary classification and maps the raw menu choice to
     * an {@link ItemType} enum value. Any unrecognized input quietly falls
     * back to STANDARD rather than rejecting the whole "add item" flow.
     */
    private static ItemType askDietaryType() {
        out.println("Dietary type: 1=Standard  2=Vegan  3=Gluten-Free");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "2":
                return ItemType.VEGAN;
            case "3":
                return ItemType.GLUTEN_FREE;
            default:
                return ItemType.STANDARD;
        }
    }

    /** Menu option 2. Collects name/email and registers a new {@link Customer}. */
    private static void registerCustomer() {
        out.print("Name: ");
        String name = scanner.nextLine();
        out.print("Email: ");
        String email = scanner.nextLine();
        Customer customer = new Customer(name, email);
        bakeryService.registerCustomer(customer);
        out.println("Registered: " + customer + " [id=" + customer.getCustomerId() + "]");
    }

    /**
     * Menu option 3. Attempts to purchase one item for one customer.
     * {@link BakeryService#purchaseItem} is synchronized internally, which
     * matters most when this same logic runs concurrently - see
     * {@link #simulateConcurrentPurchase()} below.
     */
    private static void purchaseItem() {
        out.print("Item id: ");
        String itemId = scanner.nextLine().trim();
        out.print("Customer id: ");
        String customerId = scanner.nextLine().trim();
        try {
            Order order = bakeryService.purchaseItem(itemId, customerId);
            // Non-perishable items (e.g. Pastry) have no expiration date, so
            // guard against printing "null".
            out.println("Purchased. Best before: "
                    + (order.getExpirationDate() == null ? "N/A" : order.getExpirationDate()));
        } catch (BakeryException e) {
            // Covers ItemNotFoundException, CustomerNotFoundException,
            // ItemOutOfStockException, and CustomerLimitExceededException -
            // all of which extend BakeryException.
            out.println("Purchase failed: " + e.getMessage());
        }
    }

    /**
     * Menu option 4. Reverses a purchase: puts the item back in stock and
     * removes it from the customer's purchase history.
     */
    private static void refundItem() {
        out.print("Item id: ");
        String itemId = scanner.nextLine().trim();
        out.print("Customer id: ");
        String customerId = scanner.nextLine().trim();
        try {
            bakeryService.refundItem(itemId, customerId);
            out.println("Refunded successfully.");
        } catch (BakeryException e) {
            out.println("Refund failed: " + e.getMessage());
        }
    }

    /**
     * Menu option 5. Reports how many days remain before an item expires
     * (or how many days ago it expired, if that's already happened). Only
     * meaningful for items implementing {@code Perishable}; the service
     * layer throws a {@link BakeryException} for anything else (e.g. Pastry).
     */
    private static void checkFreshness() {
        out.print("Item id: ");
        String itemId = scanner.nextLine().trim();
        out.print("Customer id: ");
        String customerId = scanner.nextLine().trim();
        try {
            long daysLeft = bakeryService.checkFreshness(itemId, customerId);
            if (daysLeft < 0) {
                out.println("This item expired " + (-daysLeft) + " day(s) ago.");
            } else {
                out.println("Fresh for " + daysLeft + " more day(s).");
            }
        } catch (BakeryException e) {
            out.println("Freshness check failed: " + e.getMessage());
        }
    }

    /**
     * Menu option 6. Lists every item currently in the inventory, sorted by
     * whichever {@link ItemComparators} constant the user picks. Demonstrates
     * swapping in different Comparator strategies against the same data.
     */
    private static void listInventory() {
        out.println("Sort by: 1=Name  2=Baker  3=Category then Name  4=Price  5=Bake date");
        String choice = scanner.nextLine().trim();
        List<BakedGood> items;
        switch (choice) {
            case "2":
                items = bakeryService.sortedBy(ItemComparators.BY_BAKER);
                break;
            case "3":
                items = bakeryService.sortedBy(ItemComparators.BY_CATEGORY_THEN_NAME);
                break;
            case "4":
                items = bakeryService.sortedBy(ItemComparators.BY_PRICE);
                break;
            case "5":
                items = bakeryService.sortedBy(ItemComparators.BY_BAKE_DATE);
                break;
            default:
                items = bakeryService.sortedBy(ItemComparators.BY_NAME);
        }
        if (items.isEmpty()) {
            out.println("Inventory is empty.");
        }
        items.forEach(item -> out.println(" - " + item.displayInfo() + " [id=" + item.getItemId() + "]"));
    }

    /**
     * Menu option 7. Filters the inventory using one of a few predicate-based
     * searches exposed by {@link BakeryService} (name keyword, exact baker
     * match, or in-stock only).
     */
    private static void searchInventory() {
        out.println("Search by: 1=Name keyword  2=Baker  3=In stock only");
        String choice = scanner.nextLine().trim();
        List<BakedGood> results;
        switch (choice) {
            case "1":
                out.print("Keyword: ");
                results = bakeryService.searchByNameContains(scanner.nextLine().trim());
                break;
            case "2":
                out.print("Baker: ");
                results = bakeryService.searchByBaker(scanner.nextLine().trim());
                break;
            case "3":
                results = bakeryService.findAvailableItems();
                break;
            default:
                out.println("Invalid option.");
                return;
        }
        if (results.isEmpty()) {
            out.println("No matches found.");
        }
        results.forEach(item -> out.println(" - " + item.displayInfo() + " [id=" + item.getItemId() + "]"));
    }

    /**
     * Menu option 8. Prints summary counts (total, in-stock, grouped by
     * category) followed by a full availability report generated by
     * {@link ReportGenerator}, which itself demonstrates named Predicate/
     * Function/Consumer/Supplier fields rather than only inline lambdas.
     */
    private static void showReports() {
        List<BakedGood> all = bakeryService.findAll();
        out.println("Total items: " + all.size());
        out.println("In-stock items: " + bakeryService.countAvailable());
        out.println("Count by category: " + bakeryService.countByCategory());
        out.println();
        reportGenerator.printAvailabilityReport(all);
    }

    /**
     * Menu option 9. Reads back the plain-text order log written to
     * data/orders.log by every purchase/refund/freshness-check, one line
     * per event, oldest first.
     */
    private static void viewOrderHistory() {
        try {
            List<String> lines = bakeryService.readOrderHistory();
            if (lines.isEmpty()) {
                out.println("No orders recorded yet.");
            } else {
                lines.forEach(out::println);
            }
        } catch (IOException e) {
            out.println("Could not read order log: " + e.getMessage());
        }
    }

    /**
     * Menu option 10. Multithreading demo: spins up to 3 {@link Thread}s,
     * each running a {@link CheckoutTask} that races to purchase the SAME
     * item for a different customer. Because
     * {@link BakeryService#purchaseItem} is synchronized, exactly one
     * thread should win and the rest should fail with
     * "currently out of stock" - proving the availability check-then-act
     * is atomic even under contention.
     */
    private static void simulateConcurrentPurchase() {
        List<BakedGood> available = bakeryService.findAvailableItems();
        List<Customer> customers = bakeryService.findAllCustomers();
        if (available.isEmpty() || customers.size() < 2) {
            out.println("Need at least 1 in-stock item and 2 customers for this demo. "
                    + "(Add more via options 1 and 2 first.)");
            return;
        }
        // Deliberately contest the SAME item across all threads to
        // demonstrate the race condition being prevented.
        String contestedItemId = available.get(0).getItemId();
        out.println("Simulating 3 customers racing to buy the last unit of item " + contestedItemId + " ...");

        int contenders = Math.min(3, customers.size());
        Thread[] threads = new Thread[contenders];
        for (int i = 0; i < contenders; i++) {
            String customerId = customers.get(i).getCustomerId();
            threads[i] = new Thread(new CheckoutTask(bakeryService, contestedItemId, customerId),
                    "Thread-" + (i + 1));
        }
        // Start all contenders as close to simultaneously as possible...
        for (Thread t : threads)
            t.start();
        // ...then wait for every thread to finish before printing the result.
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        out.println("Demo complete - exactly one thread should have succeeded, "
                + "which the synchronized purchaseItem() method guarantees.");
    }

    /**
     * Menu option 11. Persists the in-memory inventory and customer list to
     * disk (object serialization) and stops the main loop.
     */
    private static void saveAndExit() {
        try {
            bakeryService.saveToDisk();
            out.println("Inventory and customers saved to /data. Goodbye!");
        } catch (IOException e) {
            out.println("Failed to save: " + e.getMessage());
        }
    }

    /**
     * Runs once at startup: tries to reload a previously saved inventory
     * and customer list from disk. A missing/corrupt save file is treated
     * as "nothing to load yet" rather than a fatal error.
     */
    private static void loadOnStartup() {
        try {
            bakeryService.loadFromDisk();
            out.println("Loaded " + bakeryService.findAll().size() + " items and "
                    + bakeryService.findAllCustomers().size() + " customers from disk.");
        } catch (IOException | ClassNotFoundException e) {
            out.println("No prior saved data found - starting fresh.");
        }
    }

    /**
     * Seeds a couple of demo records so the console menu isn't empty on
     * first run. No-ops if data was already loaded from disk (or added
     * earlier in this session).
     */
    private static void seedDemoDataIfEmpty() {
        if (!bakeryService.findAll().isEmpty()) {
            return;
        }
        try {
            bakeryService.registerItem(BakedGoodFactory.createBread(
                    "Sourdough Loaf", "Maria Costa", "SRD001A", "Sourdough", 700, 180.0, ItemType.STANDARD));
            bakeryService.registerItem(BakedGoodFactory.createBread(
                    "Whole Wheat Loaf", "Maria Costa", "WWT002B", "Whole Wheat", 650, 150.0, ItemType.STANDARD));
            bakeryService.registerItem(BakedGoodFactory.createPastry(
                    "Butter Croissant", "Jean Luc", "B-312", "Classic Butter", 90.0, ItemType.STANDARD));
            bakeryService.registerItem(BakedGoodFactory.createCake(
                    "Custom Birthday Cake", "Anita Rao", "8-inch", 1.5, 950.0, ItemType.GLUTEN_FREE));
            bakeryService.registerCustomer(new Customer("Asha Verma", "asha@example.com"));
            bakeryService.registerCustomer(new Customer("Rohan Mehta", "rohan@example.com"));
            bakeryService.registerCustomer(new Customer("Priya Nair", "priya@example.com"));
        } catch (BakeryException e) {
            out.println("Seed data error: " + e.getMessage());
        }
    }
}