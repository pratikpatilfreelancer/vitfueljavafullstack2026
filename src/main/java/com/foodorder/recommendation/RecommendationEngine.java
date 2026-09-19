package com.foodorder.recommendation;

import com.foodorder.model.FoodItem;
import java.util.List;

public interface RecommendationEngine {
    /** Given the item the customer just added and the full menu, suggest related items. */
    List<FoodItem> recommend(FoodItem selected, List<FoodItem> allItems);
}
