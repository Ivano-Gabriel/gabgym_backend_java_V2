package com.gabgym.api.controllers;

import com.gabgym.api.entities.Exercise;
import com.gabgym.api.entities.MuscleGroup;
import com.gabgym.api.repositories.ExerciseRepository;
import com.gabgym.api.repositories.MuscleGroupRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout")

public class ExerciseController {

    private final ExerciseRepository exerciseRepo;
    private final MuscleGroupRepository muscleGroupRepo;

    public ExerciseController(ExerciseRepository exerciseRepo, MuscleGroupRepository muscleGroupRepo) {
        this.exerciseRepo = exerciseRepo;
        this.muscleGroupRepo = muscleGroupRepo;
    }

    @GetMapping("/groups")
    public ResponseEntity<List<MuscleGroup>> getMuscleGroups() {
        return ResponseEntity.ok(muscleGroupRepo.findAll());
    }

    @GetMapping("/exercises")
    public ResponseEntity<List<Exercise>> getExercises() {
        return ResponseEntity.ok(exerciseRepo.findAll());
    }
}