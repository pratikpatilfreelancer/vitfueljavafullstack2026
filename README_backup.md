# BakeryVault — A Modular Bakery Management System in Core Java

A console-based Bakery Management System built entirely on Core Java SE
(no frameworks) to demonstrate OOP design, Collections, Streams, File I/O,
Multithreading, and classic Design Patterns.

## Features

- Catalog bread, pastries, and custom cakes through a shared abstract type
- Register customers and manage purchases / refunds / freshness checks with
  real business rules (purchase limits, stock availability, expiration)
- Search and sort the inventory using Java Streams (by name, baker, price,
  bake date, category)
- Persist the inventory and customer list across runs via Object Serialization
- Human-readable order log written with buffered file I/O
- A live demo of thread-safe concurrent purchases

## Tech Stack

Pure Java SE (targets Java 11+). No Spring, no Maven/Gradle, no external
dependencies — compiles with the JDK alone.

## Project Structure

```
BakeryVault/
├── src/
│   └── com/bakeryvault/
│       ├── model/          # BakedGood hierarchy, Customer, Order
│       ├── interfaces/     # Perishable, PreOrderable
│       ├── exceptions/     # User-defined checked exceptions
│       ├── enums/          # ItemType (dietary classification)
│       ├── repository/     # Generic in-memory CRUD layer
│       ├── factory/        # BakedGoodFactory (Factory pattern)
│       ├── service/        # BakeryInventory (Singleton), BakeryService (Facade), CheckoutTask
│       ├── utils/          # FileManager, Comparators, ReportGenerator
│       └── Main.java        # Console UI
├── data/                    # Generated at runtime: inventory.ser, customers.ser, orders.log
└── README.md
```

## How to Compile & Run

From the project root:

```bash
# Compile
mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

# Run
java -cp bin com.bakeryvault.Main
```

On first run the app seeds a few demo baked goods/customers automatically.
Data is saved to `data/` when you choose **Save & Exit**, and reloaded
automatically on the next run.

## Core Java Concepts Demonstrated

| Category | Where |
|---|---|
| Abstract classes & inheritance | `BakedGood` → `Bread`, `Pastry`, `Cake` |
| Interfaces & multiple inheritance | `Bread implements Perishable, PreOrderable` |
| Polymorphism | `Cake.setAvailable()` override; `displayInfo()` per subtype |
| Encapsulation | All model fields private with controlled accessors |
| `static` / `this` / `super` | ID generators, constructor chaining |
| Static nested (inner) class | `BakeryInventory.Holder` (initialization-on-demand Singleton idiom) |
| Custom exceptions | `exceptions/` package, all extending `BakeryException` |
| try-with-resources | Every stream in `FileManager` |
| Generics | `Repository<T, ID>` interface |
| Collections | `ArrayList`, `HashMap`, `LinkedHashMap` in the repository layer |
| Comparable / Comparator | `Customer implements Comparable`; `ItemComparators` |
| Streams | `map`, `filter`, `reduce`, `limit`, `skip`, `Collectors.groupingBy` in `BakeryService` |
| Functional interfaces | `Predicate`, `Function`, `Consumer`, `Supplier` in `ReportGenerator` |
| Method references | `BakedGood::isAvailable`, `BakedGood::getName` |
| File handling | `FileWriter`/`BufferedWriter`/`PrintWriter` write; `FileReader`/`BufferedReader` read |
| Object Serialization | `inventory.ser`, `customers.ser` |
| Multithreading | `Thread`, `Runnable` (`CheckoutTask`), `synchronized` |
| Design Patterns | Singleton (`BakeryInventory`), Factory (`BakedGoodFactory`), Facade (`BakeryService`) |
| Static import | `import static java.lang.System.out;` in `Main` |

## Known Limitations / Future Improvements

- No GUI — console only, by design (see project constraints)
- Single-user, in-process only (no networking)
- MySQL CRUD integration not yet implemented (optional stretch goal)
- No pagination UI for very large inventories (the `page()` method exists but isn't wired into the menu)
