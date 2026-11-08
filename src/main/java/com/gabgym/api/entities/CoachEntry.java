package com.gabgym.api.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "coach_entries")
public class CoachEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "relationship_id", nullable = false)
    private Long relationshipId;
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    @Column(nullable = false, length = 16)
    private String kind;
    @Column(nullable = false, length = 2000)
    private String content;
    @Column(nullable = false, length = 16)
    private String status = "NONE";
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    public Long getId(){return id;}
    public Long getRelationshipId(){return relationshipId;}
    public void setRelationshipId(Long x){relationshipId=x;}
    public Long getAuthorId(){return authorId;}
    public void setAuthorId(Long x){authorId=x;}
    public String getKind(){return kind;}
    public void setKind(String x){kind=x;}
    public String getContent(){return content;}
    public void setContent(String x){content=x;}
    public String getStatus(){return status;}
    public void setStatus(String x){status=x;}
    public Instant getCreatedAt(){return createdAt;}
}
