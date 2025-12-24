package com.rjxy.clothing.controller;

import com.rjxy.clothing.model.*;
import com.rjxy.clothing.repo.SubsidyBatchRepository;
import com.rjxy.clothing.repo.StudentRepository;
import com.rjxy.clothing.service.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminAuthService auth;
    private final BatchService batchService;
    private final ApplicationService applicationService;
    private final SubsidyBatchRepository batchRepo;
    private final StyleService styleService;
    private final SelectionService selectionService;
    private final NotificationService notificationService;
    private final StudentRepository studentRepository;

    public AdminController(AdminAuthService auth,
                           BatchService batchService,
                           ApplicationService applicationService,
                           SubsidyBatchRepository batchRepo,
                           StyleService styleService,
                           SelectionService selectionService,
                           NotificationService notificationService,
                           StudentRepository studentRepository) {
        this.auth = auth;
        this.batchService = batchService;
        this.applicationService = applicationService;
        this.batchRepo = batchRepo;
        this.styleService = styleService;
        this.selectionService = selectionService;
        this.notificationService = notificationService;
        this.studentRepository = studentRepository;
    }

    private boolean authorized(String userId) {
        return auth.isAuthorized(userId);
    }
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestParam String username, @RequestParam String password) {
        return auth.login(username, password)
                .map(u -> ResponseEntity.ok(Map.of("username", u.getUsername(), "role", u.getRole())))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @PostMapping("/batch/open")
    public ResponseEntity<?> openBatch(@RequestHeader("X-Admin-User") String userId,
                                       @RequestParam Integer year,
                                       @RequestParam Integer difficultyYear,
                                       @RequestParam String difficultyLevel,
                                       @RequestParam String applicationDeadlineIso) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "counselor")) return ResponseEntity.status(403).body("counselor_only_access");
        LocalDateTime deadline = LocalDateTime.parse(applicationDeadlineIso);
        SubsidyBatch b = batchService.openBatch(year, difficultyYear, difficultyLevel, deadline);
        return ResponseEntity.ok(Map.of("batchId", b.getId()));
    }

    @PostMapping("/batch/deadline/application")
    public ResponseEntity<?> extendApply(@RequestHeader("X-Admin-User") String userId,
                                         @RequestParam Long batchId,
                                         @RequestParam String deadlineIso) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "counselor")) return ResponseEntity.status(403).body("counselor_only_access");
        return batchService.updateApplicationDeadline(batchId, LocalDateTime.parse(deadlineIso))
                .<ResponseEntity<?>>map(b -> ResponseEntity.ok(Map.of("batchId", b.getId())))
                .orElseGet(() -> ResponseEntity.badRequest().body("batch_not_found"));
    }

    @PostMapping("/batch/deadline/selection")
    public ResponseEntity<?> extendSelect(@RequestHeader("X-Admin-User") String userId,
                                          @RequestParam Long batchId,
                                          @RequestParam String deadlineIso) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "counselor")) return ResponseEntity.status(403).body("counselor_only_access");
        return batchService.updateSelectionDeadline(batchId, LocalDateTime.parse(deadlineIso))
                .<ResponseEntity<?>>map(b -> ResponseEntity.ok(Map.of("batchId", b.getId())))
                .orElseGet(() -> ResponseEntity.badRequest().body("batch_not_found"));
    }

    @PostMapping("/styles/create")
    public ResponseEntity<?> createStyle(@RequestHeader("X-Admin-User") String userId,
                                         @RequestParam String name,
                                         @RequestParam String gender,
                                         @RequestParam String imageUrl,
                                         @RequestBody List<Map<String, String>> variants) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "counselor")) return ResponseEntity.status(403).body("counselor_only_access");
        List<ClothingVariant> list = variants.stream().map(m -> {
            ClothingVariant v = new ClothingVariant();
            v.setSizeLabel(m.get("sizeLabel"));
            v.setProductCode(m.get("productCode"));
            return v;
        }).collect(Collectors.toList());
        ClothingStyle s = styleService.createStyle(name, gender, imageUrl, list);
        return ResponseEntity.ok(Map.of("styleId", s.getId()));
    }

    @PostMapping("/students/create")
    public ResponseEntity<?> createStudent(@RequestHeader("X-Admin-User") String userId,
                                           @RequestParam String studentNumber,
                                           @RequestParam String name,
                                           @RequestParam String gender,
                                           @RequestParam String idCardNumber,
                                           @RequestParam Integer entryYear,
                                           @RequestParam String collegeName,
                                           @RequestParam String difficultyLevel,
                                           @RequestParam Integer difficultyYear) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "counselor")) return ResponseEntity.status(403).body("counselor_only_access");
        if (studentRepository.findByStudentNumber(studentNumber).isPresent()) {
            return ResponseEntity.status(409).body("student_number_exists");
        }
        Student s = new Student();
        s.setStudentNumber(studentNumber);
        s.setName(name);
        s.setGender(gender);
        s.setIdCardNumber(idCardNumber);
        s.setEntryYear(entryYear);
        s.setCollegeName(collegeName);
        s.setDifficultyLevel(difficultyLevel);
        s.setDifficultyYear(difficultyYear);
        studentRepository.save(s);
        return ResponseEntity.ok(Map.of("studentId", s.getId()));
    }

    @PostMapping("/review/counselor")
    public ResponseEntity<?> reviewCounselor(@RequestHeader("X-Admin-User") String userId,
                                             @RequestParam List<Long> applicationIds,
                                             @RequestParam String status,
                                             @RequestParam(required = false) String rejectReason) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "counselor")) return ResponseEntity.status(403).body("counselor_only_access");
        applicationService.bulkReviewCounselor(applicationIds, status, rejectReason);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/review/college")
    public ResponseEntity<?> reviewCollege(@RequestHeader("X-Admin-User") String userId,
                                           @RequestParam List<Long> applicationIds,
                                           @RequestParam String status,
                                           @RequestParam(required = false) String rejectReason) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "college")) return ResponseEntity.status(403).body("college_only_access");
        applicationService.bulkReviewCollege(applicationIds, status, rejectReason);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/review/school")
    public ResponseEntity<?> reviewSchool(@RequestHeader("X-Admin-User") String userId,
                                          @RequestParam List<Long> applicationIds,
                                          @RequestParam String status,
                                          @RequestParam(required = false) String rejectReason) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "school")) return ResponseEntity.status(403).body("school_only_access");
        applicationService.bulkReviewSchool(applicationIds, status, rejectReason);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/submit/college")
    public ResponseEntity<?> submitCollege(@RequestHeader("X-Admin-User") String userId,
                                           @RequestParam Long batchId) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "counselor")) return ResponseEntity.status(403).body("counselor_only_access");
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().body("batch_not_found");
        applicationService.submitToCollegeAfterDeadline(b.get());
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/submit/school")
    public ResponseEntity<?> submitSchool(@RequestHeader("X-Admin-User") String userId,
                                          @RequestParam Long batchId) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "college")) return ResponseEntity.status(403).body("college_only_access");
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().body("batch_not_found");
        applicationService.submitToSchool(b.get());
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/finalize")
    public ResponseEntity<?> finalizeAndNotify(@RequestHeader("X-Admin-User") String userId,
                                               @RequestParam Long batchId) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "school")) return ResponseEntity.status(403).body("school_only_access");
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().body("batch_not_found");
        SubsidyBatch batch = b.get();
        List<StudentApplication> apps = applicationService.pageByBatch(batch, 0, Integer.MAX_VALUE).getContent();
        for (StudentApplication app : apps) {
            if ("REJECTED".equals(app.getSchoolStatus())) {
                String msg = "寒衣补助未通过" + (app.getRejectReason() != null ? "，理由：" + app.getRejectReason() : "");
                notificationService.push(app.getStudent(), msg);
            } else if ("APPROVED".equals(app.getSchoolStatus())) {
                notificationService.push(app.getStudent(), "寒衣补助通过，请在系统登记衣服款式和尺码");
            }
        }
        batch.setFinalAuditCompleted(true);
        batchRepo.save(batch);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/selection/override")
    public ResponseEntity<?> overrideSelection(@RequestHeader("X-Admin-User") String userId,
                                               @RequestParam Long batchId,
                                               @RequestParam Long studentId,
                                               @RequestParam Long variantId) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!auth.hasRole(userId, "counselor")) return ResponseEntity.status(403).body("counselor_only_access");
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().body("batch_not_found");
        Optional<Student> s = studentRepository.findById(studentId);
        if (s.isEmpty()) return ResponseEntity.badRequest().body("student_not_found");
        Optional<ClothingVariant> v = styleService.variantById(variantId);
        if (v.isEmpty()) return ResponseEntity.badRequest().body("variant_not_found");
        return selectionService.adminOverride(s.get(), b.get(), v.get())
                .<ResponseEntity<?>>map(sel -> ResponseEntity.ok(Map.of("selectionId", sel.getId())))
                .orElseGet(() -> ResponseEntity.badRequest().body("not_allowed"));
    }

    @GetMapping("/applications")
    public ResponseEntity<?> listApplications(@RequestHeader("X-Admin-User") String userId,
                                              @RequestParam Long batchId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        if (!(size == 10 || size == 20 || size == 50)) size = 10;
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().body("batch_not_found");
        Page<StudentApplication> p = applicationService.pageByBatch(b.get(), page, size);
        return ResponseEntity.ok(Map.of("total", p.getTotalElements(), "content", p.getContent()));
    }

    @GetMapping(value = "/export/applications.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportApplications(@RequestHeader("X-Admin-User") String userId,
                                                     @RequestParam Long batchId) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().build();
        List<StudentApplication> apps = applicationService.pageByBatch(b.get(), 0, Integer.MAX_VALUE).getContent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
        out.writeBytes("学号,姓名,学院,辅导员审核,学院审核,学校审核,理由\n".getBytes(StandardCharsets.UTF_8));
        for (StudentApplication a : apps) {
            String row = a.getStudent().getStudentNumber() + "," + a.getStudent().getName() + "," + a.getStudent().getCollegeName() + "," + a.getCounselorStatus() + "," + a.getCollegeStatus() + "," + a.getSchoolStatus() + "," + (a.getRejectReason() == null ? "" : a.getRejectReason()) + "\n";
            out.writeBytes(row.getBytes(StandardCharsets.UTF_8));
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=applications.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(out.toByteArray());
    }

    @GetMapping(value = "/export/summary.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportSummary(@RequestHeader("X-Admin-User") String userId,
                                                @RequestParam Long batchId) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().build();
        List<StudentApplication> apps = applicationService.pageByBatch(b.get(), 0, Integer.MAX_VALUE).getContent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
        out.writeBytes("学号,姓名,学院,最终结果\n".getBytes(StandardCharsets.UTF_8));
        for (StudentApplication a : apps) {
            String finalResult = a.getSchoolStatus();
            String row = a.getStudent().getStudentNumber() + "," + a.getStudent().getName() + "," + a.getStudent().getCollegeName() + "," + finalResult + "\n";
            out.writeBytes(row.getBytes(StandardCharsets.UTF_8));
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=summary.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(out.toByteArray());
    }

    @GetMapping("/stats/style-gender-size")
    public ResponseEntity<?> statsStyleGenderSize(@RequestHeader("X-Admin-User") String userId,
                                                  @RequestParam Long batchId) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().body("batch_not_found");
        return ResponseEntity.ok(selectionService.statsByStyleGenderSize(b.get()));
    }

    @GetMapping("/stats/college-style-gender-size")
    public ResponseEntity<?> statsCollegeStyleGenderSize(@RequestHeader("X-Admin-User") String userId,
                                                         @RequestParam Long batchId) {
        if (!authorized(userId)) return ResponseEntity.status(403).build();
        Optional<SubsidyBatch> b = batchRepo.findById(batchId);
        if (b.isEmpty()) return ResponseEntity.badRequest().body("batch_not_found");
        return ResponseEntity.ok(selectionService.statsByCollegeStyleGenderSize(b.get()));
    }
}
