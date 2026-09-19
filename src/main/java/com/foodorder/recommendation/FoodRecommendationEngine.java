package com.foodorder.recommendation;

import com.foodorder.model.Category;
import com.foodorder.model.FoodItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple, predefined-rule recommendation engine (no ML needed).
 * Maps a food name (case-insensitive) to the names of items commonly paired with it.
 * Falls back to "same category, different item" pairing when no explicit rule exists.
 */
public class FoodRecommendationEngine implements RecommendationEngine {

    private final Map<String, List<String>> rules = new HashMap<>();

    public FoodRecommendationEngine() {
        rules.put("pizza", List.of("Coke", "Garlic Bread"));
        rules.put("burger", List.of("French Fries", "Cold Drink"));
        rules.put("biryani", List.of("Raita", "Papad"));
        rules.put("noodles", List.of("Spring Roll", "Manchow Soup"));
        rules.put("coffee", List.of("Brownie"));
    }

    @Override
    public List<FoodItem> recommend(FoodItem selected, List<FoodItem> allItems) {
        List<FoodItem> results = new ArrayList<>();
        if (selected == null || allItems == null) return results;

        List<String> suggestedNames = rules.get(selected.getName().toLowerCase());

        if (suggestedNames != null) {
            for (FoodItem item : allItems) {
                if (suggestedNames.stream().anyMatch(n -> n.equalsIgnoreCase(item.getName()))) {
                    results.add(item);
                }
            }
        }

        // Fallback: recommend up to 2 other available items from the same category.
        if (results.isEmpty()) {
            Category cat = selected.getCategory();
            for (FoodItem item : allItems) {
                if (item.getId() != selected.getId()
                        && item.getCategory() == cat
                        && item.isAvailability()
                        && results.size() < 2) {
                    results.add(item);
                }
            }
        }
        return results;
    }
}
