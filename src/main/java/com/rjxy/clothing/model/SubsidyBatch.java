package com.rjxy.clothing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SubsidyBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer year;
    @Column(nullable = false)
    private String difficultyLevel;
    @Column(nullable = false)
    private Integer difficultyYear;
    @Column(nullable = false)
    private LocalDateTime openTime;
    @Column(nullable = false)
    private LocalDateTime applicationDeadline;
    @Column
    private LocalDateTime selectionDeadline;
    @Column(nullable = false)
    private Boolean finalAuditCompleted = false;

    public Long getId() {
        return id;
    }
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    public String getDifficultyLevel() {
        return difficultyLevel;
    }
    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    public Integer getDifficultyYear() {
        return difficultyYear;
    }
    public void setDifficultyYear(Integer difficultyYear) {
        this.difficultyYear = difficultyYear;
    }
    public LocalDateTime getOpenTime() {
        return openTime;
    }
    public void setOpenTime(LocalDateTime openTime) {
        this.openTime = openTime;
    }
    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }
    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }
    public LocalDateTime getSelectionDeadline() {
        return selectionDeadline;
    }
    public void setSelectionDeadline(LocalDateTime selectionDeadline) {
        this.selectionDeadline = selectionDeadline;
    }
    public Boolean getFinalAuditCompleted() {
        return finalAuditCompleted;
    }
    public void setFinalAuditCompleted(Boolean finalAuditCompleted) {
        this.finalAuditCompleted = finalAuditCompleted;
    }
}
