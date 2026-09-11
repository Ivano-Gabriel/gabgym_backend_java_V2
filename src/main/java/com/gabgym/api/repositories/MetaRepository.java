package com.gabgym.api.repositories;

import com.gabgym.api.entities.Meta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {
    List<Meta> findByUserId(Long userId);
}