package com.rjxy.clothing.service;

import com.rjxy.clothing.model.SubsidyBatch;
import com.rjxy.clothing.repo.SubsidyBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BatchService {
    private final SubsidyBatchRepository repo;
    public BatchService(SubsidyBatchRepository repo) {
        this.repo = repo;
    }
    @Transactional
    public SubsidyBatch openBatch(Integer year, Integer difficultyYear, String difficultyLevel, LocalDateTime applicationDeadline) {
        SubsidyBatch b = new SubsidyBatch();
        b.setYear(year);
        b.setDifficultyYear(difficultyYear);
        b.setDifficultyLevel(difficultyLevel);
        b.setOpenTime(LocalDateTime.now());
        b.setApplicationDeadline(applicationDeadline);
        return repo.save(b);
    }
    @Transactional
    public Optional<SubsidyBatch> updateApplicationDeadline(Long batchId, LocalDateTime deadline) {
        Optional<SubsidyBatch> opt = repo.findById(batchId);
        if (opt.isEmpty()) return Optional.empty();
        SubsidyBatch b = opt.get();
        b.setApplicationDeadline(deadline);
        return Optional.of(repo.save(b));
    }
    @Transactional
    public Optional<SubsidyBatch> updateSelectionDeadline(Long batchId, LocalDateTime deadline) {
        Optional<SubsidyBatch> opt = repo.findById(batchId);
        if (opt.isEmpty()) return Optional.empty();
        SubsidyBatch b = opt.get();
        b.setSelectionDeadline(deadline);
        return Optional.of(repo.save(b));
    }
    public Optional<SubsidyBatch> get(Long id) {
        return repo.findById(id);
    }
}
