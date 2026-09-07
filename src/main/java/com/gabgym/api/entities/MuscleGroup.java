package com.gabgym.api.entities;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "muscle_groups")
public class MuscleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, unique = true, nullable = false)
    private String name;

    // Relacionamento reverso (1 Grupo tem Vários Exercícios)
    @OneToMany(mappedBy = "muscleGroup", cascade = CascadeType.ALL)
    @JsonIgnore // Evita loop infinito na hora de gerar o JSON pro Front-end
    private List<Exercise> exercises;

    public MuscleGroup() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }
}