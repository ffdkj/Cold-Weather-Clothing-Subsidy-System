package com.rjxy.clothing.model;

import jakarta.persistence.*;

@Entity
public class StudentApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Student student;
    @ManyToOne(optional = false)
    private SubsidyBatch batch;
    @Column
    private String reason;
    @Column(nullable = false)
    private String counselorStatus = "PENDING";
    @Column(nullable = false)
    private String collegeStatus = "PENDING";
    @Column(nullable = false)
    private String schoolStatus = "PENDING";
    @Column
    private String rejectReason;
    @Column(nullable = false)
    private Boolean submittedToCollege = false;
    @Column(nullable = false)
    private Boolean submittedToSchool = false;

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
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getCounselorStatus() {
        return counselorStatus;
    }
    public void setCounselorStatus(String counselorStatus) {
        this.counselorStatus = counselorStatus;
    }
    public String getCollegeStatus() {
        return collegeStatus;
    }
    public void setCollegeStatus(String collegeStatus) {
        this.collegeStatus = collegeStatus;
    }
    public String getSchoolStatus() {
        return schoolStatus;
    }
    public void setSchoolStatus(String schoolStatus) {
        this.schoolStatus = schoolStatus;
    }
    public String getRejectReason() {
        return rejectReason;
    }
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    public Boolean getSubmittedToCollege() {
        return submittedToCollege;
    }
    public void setSubmittedToCollege(Boolean submittedToCollege) {
        this.submittedToCollege = submittedToCollege;
    }
    public Boolean getSubmittedToSchool() {
        return submittedToSchool;
    }
    public void setSubmittedToSchool(Boolean submittedToSchool) {
        this.submittedToSchool = submittedToSchool;
    }
}
