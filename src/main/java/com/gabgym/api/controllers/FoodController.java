package com.gabgym.api.controllers;

import com.gabgym.api.entities.FoodCategory;
import com.gabgym.api.entities.FoodItem;
import com.gabgym.api.repositories.FoodCategoryRepository;
import com.gabgym.api.repositories.FoodItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diet")

public class FoodController {

    private final FoodCategoryRepository categoryRepo;
    private final FoodItemRepository itemRepo;

    public FoodController(FoodCategoryRepository categoryRepo, FoodItemRepository itemRepo) {
        this.categoryRepo = categoryRepo;
        this.itemRepo = itemRepo;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<FoodCategory>> getCategories() {
        return ResponseEntity.ok(categoryRepo.findAll());
    }

    @GetMapping("/foods")
    public ResponseEntity<List<FoodItem>> getFoods() {
        return ResponseEntity.ok(itemRepo.findAll());
    }
}