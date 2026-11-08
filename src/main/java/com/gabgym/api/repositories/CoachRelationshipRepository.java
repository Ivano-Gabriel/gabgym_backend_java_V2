package com.gabgym.api.repositories;
import com.gabgym.api.entities.CoachRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CoachRelationshipRepository extends JpaRepository<CoachRelationship, Long> {
    Optional<CoachRelationship> findByStudentId(Long studentId);
    List<CoachRelationship> findByCoachIdOrderByCreatedAtDesc(Long coachId);
    Optional<CoachRelationship> findByCoachIdAndStudentIdAndStatus(Long coachId,Long studentId,String status);
}
