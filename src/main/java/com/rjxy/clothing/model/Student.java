package com.rjxy.clothing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String studentNumber;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String idCardNumber;
    @Column(nullable = false)
    private Integer entryYear;
    @Column(nullable = false)
    private String collegeName;
    @Column(nullable = false)
    private String difficultyLevel;
    @Column(nullable = false)
    private Integer difficultyYear;
    @Column
    private String token;
    @Column
    private LocalDateTime tokenIssuedAt;

    public Long getId() {
        return id;
    }
    public String getStudentNumber() {
        return studentNumber;
    }
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getIdCardNumber() {
        return idCardNumber;
    }
    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }
    public Integer getEntryYear() {
        return entryYear;
    }
    public void setEntryYear(Integer entryYear) {
        this.entryYear = entryYear;
    }
    public String getCollegeName() {
        return collegeName;
    }
    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
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
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public LocalDateTime getTokenIssuedAt() {
        return tokenIssuedAt;
    }
    public void setTokenIssuedAt(LocalDateTime tokenIssuedAt) {
        this.tokenIssuedAt = tokenIssuedAt;
    }
}
