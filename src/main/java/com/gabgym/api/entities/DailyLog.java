package com.gabgym.api.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_logs")
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate date;

    private int caloriesConsumed = 0;
    private int proteinConsumed = 0;
    private int carbsConsumed = 0;
    private int fatConsumed = 0;
    private int waterMl = 0;
    private double sleepHours = 0;

    public DailyLog() {}

    @PrePersist
    protected void onCreate() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getCaloriesConsumed() { return caloriesConsumed; }
    public void setCaloriesConsumed(int caloriesConsumed) { this.caloriesConsumed = caloriesConsumed; }
    public int getProteinConsumed() { return proteinConsumed; }
    public void setProteinConsumed(int proteinConsumed) { this.proteinConsumed = proteinConsumed; }
    public int getCarbsConsumed() { return carbsConsumed; }
    public void setCarbsConsumed(int carbsConsumed) { this.carbsConsumed = carbsConsumed; }
    public int getFatConsumed() { return fatConsumed; }
    public void setFatConsumed(int fatConsumed) { this.fatConsumed = fatConsumed; }
    public int getWaterMl() { return waterMl; }
    public void setWaterMl(int waterMl) { this.waterMl = waterMl; }
    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }
}