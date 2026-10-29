package com.gabgym.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 80)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UserRole role = UserRole.ALUNO;

    private Boolean active = true;
    private Integer sessionVersion = 0;

    private String name;
    private Integer age;
    private Double weight;
    private Double height;
    private String gender;
    private String objective;
    private String activityLevel;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DailyLog> logs;

    public User() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role == null ? UserRole.ALUNO : role; }
    public void setRole(UserRole role) { this.role = role; }
    public boolean isActive() { return active == null || active; }
    public void setActive(boolean active) { this.active = active; }
    public int getSessionVersion() { return sessionVersion == null ? 0 : sessionVersion; }
    public void setSessionVersion(int version) { this.sessionVersion = version; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    public List<DailyLog> getLogs() { return logs; }
    public void setLogs(List<DailyLog> logs) { this.logs = logs; }
}
