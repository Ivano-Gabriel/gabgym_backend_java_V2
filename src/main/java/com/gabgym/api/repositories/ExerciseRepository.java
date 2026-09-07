package com.gabgym.api.repositories;

import com.gabgym.api.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    // Para buscar todos os exercícios de Peito, por exemplo
    List<Exercise> findByMuscleGroupId(Long muscleGroupId);
}