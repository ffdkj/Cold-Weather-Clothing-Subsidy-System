package com.rjxy.clothing.repo;

import com.rjxy.clothing.model.StudentApplication;
import com.rjxy.clothing.model.SubsidyBatch;
import com.rjxy.clothing.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentApplicationRepository extends JpaRepository<StudentApplication, Long> {
    Optional<StudentApplication> findByStudentAndBatch(Student student, SubsidyBatch batch);
    Page<StudentApplication> findByBatch(SubsidyBatch batch, Pageable pageable);
    List<StudentApplication> findByBatchAndSchoolStatus(SubsidyBatch batch, String status);
}
