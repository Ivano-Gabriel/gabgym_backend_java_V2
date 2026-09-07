package com.gabgym.api.repositories;

import com.gabgym.api.entities.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;


public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    // Para buscar todo o histórico de um usuário específico

    Optional<DailyLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<DailyLog> findByUserId(Long userId);
}

