package com.gabgym.api.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "coach_relationships")
public class CoachRelationship {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_id", nullable = false, unique = true)
    private Long studentId;
    @Column(name = "coach_id", nullable = false)
    private Long coachId;
    @Column(nullable = false, length = 20)
    private String status = "PENDING";
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
    public Long getId(){return id;}
    public Long getStudentId(){return studentId;}
    public void setStudentId(Long value){studentId=value;}
    public Long getCoachId(){return coachId;}
    public void setCoachId(Long value){coachId=value;}
    public String getStatus(){return status;}
    public void setStatus(String value){status=value;updatedAt=Instant.now();}
    public Instant getCreatedAt(){return createdAt;}
    public Instant getUpdatedAt(){return updatedAt;}
}
