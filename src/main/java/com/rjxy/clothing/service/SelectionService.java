package com.rjxy.clothing.service;

import com.rjxy.clothing.model.*;
import com.rjxy.clothing.repo.StudentClothingSelectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SelectionService {
    private final StudentClothingSelectionRepository repo;
    public SelectionService(StudentClothingSelectionRepository repo) {
        this.repo = repo;
    }
    @Transactional
    public Optional<StudentClothingSelection> select(Student student, SubsidyBatch batch, ClothingVariant variant) {
        if (batch.getSelectionDeadline() == null) return Optional.empty();
        if (LocalDateTime.now().isAfter(batch.getSelectionDeadline())) return Optional.empty();
        if (!variant.getStyle().getGender().equalsIgnoreCase(student.getGender())) return Optional.empty();
        Optional<StudentClothingSelection> existing = repo.findByStudentAndBatch(student, batch);
        StudentClothingSelection s = existing.orElseGet(StudentClothingSelection::new);
        if (Boolean.TRUE.equals(s.getLocked())) return Optional.empty();
        s.setStudent(student);
        s.setBatch(batch);
        s.setVariant(variant);
        return Optional.of(repo.save(s));
    }
    @Transactional
    public Optional<StudentClothingSelection> lock(Student student, SubsidyBatch batch) {
        Optional<StudentClothingSelection> s = repo.findByStudentAndBatch(student, batch);
        if (s.isEmpty()) return Optional.empty();
        StudentClothingSelection sel = s.get();
        sel.setLocked(true);
        return Optional.of(repo.save(sel));
    }
    @Transactional
    public Optional<StudentClothingSelection> adminOverride(Student student, SubsidyBatch batch, ClothingVariant variant) {
        Optional<StudentClothingSelection> existing = repo.findByStudentAndBatch(student, batch);
        StudentClothingSelection s = existing.orElseGet(StudentClothingSelection::new);
        s.setStudent(student);
        s.setBatch(batch);
        s.setVariant(variant);
        s.setLocked(true);
        return Optional.of(repo.save(s));
    }
    public Map<String, Long> statsByStyleGenderSize(SubsidyBatch batch) {
        List<StudentClothingSelection> list = repo.findByBatch(batch);
        Map<String, Long> map = new LinkedHashMap<>();
        for (StudentClothingSelection s : list) {
            String key = s.getVariant().getStyle().getName() + "|" + s.getStudent().getGender() + "|" + s.getVariant().getSizeLabel();
            map.put(key, map.getOrDefault(key, 0L) + 1);
        }
        return map;
    }
    public Map<String, Long> statsByCollegeStyleGenderSize(SubsidyBatch batch) {
        List<StudentClothingSelection> list = repo.findByBatch(batch);
        Map<String, Long> map = new LinkedHashMap<>();
        for (StudentClothingSelection s : list) {
            String key = s.getStudent().getCollegeName() + "|" + s.getVariant().getStyle().getName() + "|" + s.getStudent().getGender() + "|" + s.getVariant().getSizeLabel();
            map.put(key, map.getOrDefault(key, 0L) + 1);
        }
        return map;
    }
}
