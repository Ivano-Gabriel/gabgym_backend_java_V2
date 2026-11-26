package com.gabgym.api.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_experience_preferences")
public class UserExperiencePreferences {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "experience_mode", nullable = false, length = 16)
    private String mode = "normal";
    @Column(name = "food_enabled", nullable = false)
    private boolean food = true;
    @Column(name = "workout_enabled", nullable = false)
    private boolean workout = true;
    @Column(name = "weight_enabled", nullable = false)
    private boolean weight = true;
    @Column(name = "coach_enabled", nullable = false)
    private boolean coach = true;
    @Column(name = "language_code", nullable = false, length = 5)
    private String language = "pt";
    @Column(nullable = false)
    private boolean completed = false;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Long getUserId(){return userId;}
    public void setUserId(Long value){userId=value;}
    public String getMode(){return mode;}
    public void setMode(String value){mode=value;}
    public boolean isFood(){return food;}
    public void setFood(boolean value){food=value;}
    public boolean isWorkout(){return workout;}
    public void setWorkout(boolean value){workout=value;}
    public boolean isWeight(){return weight;}
    public void setWeight(boolean value){weight=value;}
    public boolean isCoach(){return coach;}
    public void setCoach(boolean value){coach=value;}
    public String getLanguage(){return language;}
    public void setLanguage(String value){language=value;}
    public boolean isCompleted(){return completed;}
    public void setCompleted(boolean value){completed=value;}
    public Instant getUpdatedAt(){return updatedAt;}
    @PreUpdate @PrePersist
    public void refreshTimestamp(){updatedAt=Instant.now();}
}
