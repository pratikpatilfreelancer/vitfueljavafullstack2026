package com.foodorder.spring.controller;

import com.foodorder.db.FoodDAO;
import com.foodorder.model.FoodItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/food")
@CrossOrigin
public class FoodController {

    private final FoodDAO foodDAO = new FoodDAO();

    @GetMapping
    public ResponseEntity<?> getFood(@RequestParam(required = false) String search) {
        try {
            List<FoodItem> food = (search == null || search.isBlank())
                    ? foodDAO.getAllFood()
                    : foodDAO.searchFood(search);
            return ResponseEntity.ok(food);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiError("Unable to load food: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addFood(@RequestBody FoodItem food) {
        try {
            foodDAO.addFood(food);
            return ResponseEntity.ok(food);
        } catch (SQLException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiError("Unable to add food: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFood(@PathVariable int id, @RequestBody FoodItem food) {
        try {
            food.setId(id);
            foodDAO.updateFood(food);
            return ResponseEntity.ok(food);
        } catch (SQLException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiError("Unable to update food: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable int id) {
        try {
            foodDAO.deleteFood(id);
            return ResponseEntity.noContent().build();
        } catch (SQLException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiError("Unable to delete food: " + e.getMessage()));
        }
    }

    public record ApiError(String message) {}
}
