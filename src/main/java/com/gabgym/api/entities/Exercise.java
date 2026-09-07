package com.gabgym.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "local_video_path")
    private String localVideoPath;

    @Column(name = "image_path")
    private String imagePath;

    // A Chave Estrangeira (ForeignKey do Django)
    @ManyToOne
    @JoinColumn(name = "muscle_group_id", nullable = false)
    private MuscleGroup muscleGroup;

    public Exercise() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocalVideoPath() { return localVideoPath; }
    public void setLocalVideoPath(String localVideoPath) { this.localVideoPath = localVideoPath; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public MuscleGroup getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(MuscleGroup muscleGroup) { this.muscleGroup = muscleGroup; }
}