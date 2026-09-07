package com.gabgym.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "food_items")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 50)
    private String unit;

    @Column(name = "serving_desc", length = 100)
    private String servingDesc;

    private double calories = 0;
    private double protein = 0;
    private double carbs = 0;
    private double fat = 0;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private FoodCategory category;

    public FoodItem() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getServingDesc() { return servingDesc; }
    public void setServingDesc(String servingDesc) { this.servingDesc = servingDesc; }
    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }
    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }
    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }
    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public FoodCategory getCategory() { return category; }
    public void setCategory(FoodCategory category) { this.category = category; }
}