package com.rjxy.clothing.model;

import jakarta.persistence.*;

@Entity
public class StudentClothingSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Student student;
    @ManyToOne(optional = false)
    private SubsidyBatch batch;
    @ManyToOne(optional = false)
    private ClothingVariant variant;
    @Column(nullable = false)
    private Boolean locked = false;

    public Long getId() {
        return id;
    }
    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }
    public SubsidyBatch getBatch() {
        return batch;
    }
    public void setBatch(SubsidyBatch batch) {
        this.batch = batch;
    }
    public ClothingVariant getVariant() {
        return variant;
    }
    public void setVariant(ClothingVariant variant) {
        this.variant = variant;
    }
    public Boolean getLocked() {
        return locked;
    }
    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
