package com.gabgym.api.repositories;
import com.gabgym.api.entities.CoachEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CoachEntryRepository extends JpaRepository<CoachEntry, Long> {
    List<CoachEntry> findTop100ByRelationshipIdOrderByCreatedAtDesc(Long relationshipId);
    Optional<CoachEntry> findByIdAndRelationshipId(Long id,Long relationshipId);
}
