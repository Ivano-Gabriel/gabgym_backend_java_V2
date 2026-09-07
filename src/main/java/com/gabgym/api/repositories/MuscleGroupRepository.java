package com.gabgym.api.repositories;

import com.gabgym.api.entities.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuscleGroupRepository extends JpaRepository<MuscleGroup, Long> {
}