package com.rjxy.clothing.controller;

import com.rjxy.clothing.model.*;
import com.rjxy.clothing.repo.SubsidyBatchRepository;
import com.rjxy.clothing.service.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/student")
@Validated
public class StudentController {
    private final StudentService studentService;
    private final SubsidyBatchRepository batchRepo;
    private final ApplicationService applicationService;
    private final StyleService styleService;
    private final SelectionService selectionService;
    private final NotificationService notificationService;

    public StudentController(StudentService studentService,
                             SubsidyBatchRepository batchRepo,
                             ApplicationService applicationService,
                             StyleService styleService,
                             SelectionService selectionService,
                             NotificationService notificationService) {
        this.studentService = studentService;
        this.batchRepo = batchRepo;
        this.applicationService = applicationService;
        this.styleService = styleService;
        this.selectionService = selectionService;
        this.notificationService = notificationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String studentNumber, @RequestParam String idLastSix) {
        return studentService.login(studentNumber, idLastSix)
                .map(s -> ResponseEntity.ok(Map.of("token", s.getToken(), "name", s.getName())))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestHeader("X-Student-Token") String token,
                                   @RequestParam Long batchId,
                                   @RequestParam(required = false) String reason) {
        Optional<Student> s = studentService.byToken(token);
        if (s.isEmpty()) return ResponseEntity.status(401).build();
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().body("batch_not_found");
        SubsidyBatch batch = b.get();
        boolean isNew = s.get().getEntryYear().equals(batch.getYear());
        if (!isNew && (reason == null || reason.isBlank())) return ResponseEntity.badRequest().body("reason_required");
        return applicationService.createIfEligible(s.get(), batch, reason)
                .<ResponseEntity<?>>map(a -> ResponseEntity.ok(Map.of("applicationId", a.getId())))
                .orElseGet(() -> ResponseEntity.badRequest().body("not_allowed"));
    }

    @GetMapping("/batches")
    public ResponseEntity<?> batches(@RequestHeader("X-Student-Token") String token) {
        Optional<Student> s = studentService.byToken(token);
        if (s.isEmpty()) return ResponseEntity.status(401).build();
        List<SubsidyBatch> list = batchRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(list.stream()
                .map(b -> Map.of(
                        "id", b.getId(),
                        "difficultyLevel", b.getDifficultyLevel(),
                        "difficultyYear", b.getDifficultyYear(),
                        "year", b.getYear(),
                        "applicationDeadline", b.getApplicationDeadline(),
                        "selectionDeadline", b.getSelectionDeadline(),
                        "finalAuditCompleted", b.getFinalAuditCompleted()
                ))
                .toList());
    }

    @GetMapping("/styles")
    public ResponseEntity<?> styles(@RequestHeader("X-Student-Token") String token) {
        Optional<Student> s = studentService.byToken(token);
        if (s.isEmpty()) return ResponseEntity.status(401).build();
        List<ClothingStyle> list = styleService.listByGender(s.get().getGender());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/select")
    public ResponseEntity<?> select(@RequestHeader("X-Student-Token") String token,
                                    @RequestParam Long batchId,
                                    @RequestParam Long variantId) {
        Optional<Student> s = studentService.byToken(token);
        if (s.isEmpty()) return ResponseEntity.status(401).build();
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().body("batch_not_found");
        Optional<ClothingVariant> v = styleService.variantById(variantId);
        if (v.isEmpty()) return ResponseEntity.badRequest().body("variant_not_found");
        return selectionService.select(s.get(), b.get(), v.get())
                .<ResponseEntity<?>>map(sel -> ResponseEntity.ok(Map.of("selectionId", sel.getId())))
                .orElseGet(() -> ResponseEntity.badRequest().body("not_allowed"));
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> notifications(@RequestHeader("X-Student-Token") String token) {
        Optional<Student> s = studentService.byToken(token);
        if (s.isEmpty()) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificationService.list(s.get()).stream()
                .map(n -> Map.of(
                        "id", n.getId(),
                        "message", n.getMessage(),
                        "createdAt", n.getCreatedAt()
                ))
                .toList());
    }
}
