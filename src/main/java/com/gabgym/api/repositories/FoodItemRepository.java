package com.gabgym.api.repositories;

import com.gabgym.api.entities.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByCategoryId(Long categoryId);
}