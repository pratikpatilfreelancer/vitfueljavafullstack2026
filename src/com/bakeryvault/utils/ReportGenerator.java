package com.bakeryvault.utils;

import com.bakeryvault.model.BakedGood;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Deliberately demonstrates Predicate, Function, Consumer, and Supplier
 * as named, reusable fields rather than only as inline lambdas buried in
 * streams elsewhere - useful to point to directly in an interview.
 * Generates the bakery's daily sales / availability reports.
 */
public class ReportGenerator {

    /** Predicate<T>: T -> boolean */
    public static final Predicate<BakedGood> IS_AVAILABLE = BakedGood::isAvailable;
    public static final Predicate<BakedGood> IS_BREAD = item -> "BREAD".equals(item.getItemType());

    /** Function<T,R>: T -> R (transforms a BakedGood into a short label) */
    public static final Function<BakedGood, String> TO_LABEL =
            item -> item.getItemType() + ": " + item.getName();

    /** Consumer<T>: T -> void (performs a side effect, e.g. printing) */
    public static final Consumer<BakedGood> PRINT_ITEM =
            item -> System.out.println(" - " + item.displayInfo());

    /** Supplier<T>: () -> T (produces a value with no input) */
    public static final Supplier<String> REPORT_HEADER =
            () -> "===== BakeryVault Daily Sales Report =====";

    public void printAvailabilityReport(List<BakedGood> items) {
        System.out.println(REPORT_HEADER.get());
        items.stream()
                .filter(IS_AVAILABLE)
                .map(TO_LABEL)
                .forEach(System.out::println);
    }

    public void printFullInventory(List<BakedGood> items) {
        System.out.println(REPORT_HEADER.get());
        items.forEach(PRINT_ITEM);
    }
}
