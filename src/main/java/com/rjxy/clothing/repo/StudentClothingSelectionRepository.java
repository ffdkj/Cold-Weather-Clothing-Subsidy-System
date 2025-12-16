package com.rjxy.clothing.repo;

import com.rjxy.clothing.model.StudentClothingSelection;
import com.rjxy.clothing.model.Student;
import com.rjxy.clothing.model.SubsidyBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface StudentClothingSelectionRepository extends JpaRepository<StudentClothingSelection, Long> {
    Optional<StudentClothingSelection> findByStudentAndBatch(Student student, SubsidyBatch batch);
    List<StudentClothingSelection> findByBatch(SubsidyBatch batch);
}
