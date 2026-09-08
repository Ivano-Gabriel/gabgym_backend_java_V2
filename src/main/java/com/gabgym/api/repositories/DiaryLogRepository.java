package com.gabgym.api.repositories;

import com.gabgym.api.entities.DiaryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryLogRepository extends JpaRepository<DiaryLog, Long> {
    List<DiaryLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);
}