package com.rjxy.clothing.service;

import com.rjxy.clothing.model.Student;
import com.rjxy.clothing.repo.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    @Transactional
    public Optional<Student> login(String studentNumber, String idLastSix) {
        Optional<Student> opt = studentRepository.findByStudentNumber(studentNumber);
        if (opt.isEmpty()) return Optional.empty();
        Student s = opt.get();
        String lastSix = s.getIdCardNumber().substring(Math.max(0, s.getIdCardNumber().length() - 6));
        if (!lastSix.equalsIgnoreCase(idLastSix)) return Optional.empty();
        s.setToken(UUID.randomUUID().toString());
        s.setTokenIssuedAt(LocalDateTime.now());
        studentRepository.save(s);
        return Optional.of(s);
    }
    public Optional<Student> byToken(String token) {
        return studentRepository.findByToken(token);
    }
}
