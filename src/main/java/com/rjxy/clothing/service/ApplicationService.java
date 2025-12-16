package com.rjxy.clothing.service;

import com.rjxy.clothing.model.Student;
import com.rjxy.clothing.model.StudentApplication;
import com.rjxy.clothing.model.SubsidyBatch;
import com.rjxy.clothing.repo.StudentApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    private final StudentApplicationRepository repo;
    public ApplicationService(StudentApplicationRepository repo) {
        this.repo = repo;
    }
    @Transactional
    public Optional<StudentApplication> createIfEligible(Student student, SubsidyBatch batch, String reason) {
        if (!student.getDifficultyLevel().equalsIgnoreCase(batch.getDifficultyLevel())) return Optional.empty();
        if (!student.getDifficultyYear().equals(batch.getDifficultyYear())) return Optional.empty();
        if (LocalDateTime.now().isAfter(batch.getApplicationDeadline())) return Optional.empty();
        Optional<StudentApplication> existing = repo.findByStudentAndBatch(student, batch);
        if (existing.isPresent()) return existing;
        StudentApplication app = new StudentApplication();
        app.setStudent(student);
        app.setBatch(batch);
        app.setReason(reason);
        return Optional.of(repo.save(app));
    }
    public Optional<StudentApplication> byId(Long id) {
        return repo.findById(id);
    }
    public Page<StudentApplication> pageByBatch(SubsidyBatch batch, int page, int size) {
        return repo.findByBatch(batch, PageRequest.of(page, size));
    }
    @Transactional
    public void bulkReviewCounselor(List<Long> ids, String status, String rejectReason) {
        for (Long id : ids) {
            repo.findById(id).ifPresent(app -> {
                if (Boolean.TRUE.equals(app.getSubmittedToCollege())) return;
                app.setCounselorStatus(status);
                if ("REJECTED".equalsIgnoreCase(status)) app.setRejectReason(rejectReason);
                repo.save(app);
            });
        }
    }
    @Transactional
    public void bulkReviewCollege(List<Long> ids, String status, String rejectReason) {
        for (Long id : ids) {
            repo.findById(id).ifPresent(app -> {
                if (Boolean.TRUE.equals(app.getSubmittedToSchool())) return;
                app.setCollegeStatus(status);
                if ("REJECTED".equalsIgnoreCase(status)) app.setRejectReason(rejectReason);
                repo.save(app);
            });
        }
    }
    @Transactional
    public void bulkReviewSchool(List<Long> ids, String status, String rejectReason) {
        for (Long id : ids) {
            repo.findById(id).ifPresent(app -> {
                if (app.getBatch() != null && Boolean.TRUE.equals(app.getBatch().getFinalAuditCompleted())) return;
                app.setSchoolStatus(status);
                if ("REJECTED".equalsIgnoreCase(status)) app.setRejectReason(rejectReason);
                repo.save(app);
            });
        }
    }
    @Transactional
    public void submitToCollegeAfterDeadline(SubsidyBatch batch) {
        Page<StudentApplication> page = repo.findByBatch(batch, PageRequest.of(0, Integer.MAX_VALUE));
        for (StudentApplication app : page.getContent()) {
            app.setSubmittedToCollege(true);
            if ("PENDING".equals(app.getCollegeStatus())) app.setCollegeStatus("APPROVED");
            repo.save(app);
        }
    }
    @Transactional
    public void submitToSchool(SubsidyBatch batch) {
        Page<StudentApplication> page = repo.findByBatch(batch, PageRequest.of(0, Integer.MAX_VALUE));
        for (StudentApplication app : page.getContent()) {
            app.setSubmittedToSchool(true);
            if ("PENDING".equals(app.getSchoolStatus())) app.setSchoolStatus("APPROVED");
            repo.save(app);
        }
    }
}
