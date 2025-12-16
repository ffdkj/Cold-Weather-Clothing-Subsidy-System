package com.rjxy.clothing.service;

import com.rjxy.clothing.model.Notification;
import com.rjxy.clothing.model.Student;
import com.rjxy.clothing.repo.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;
    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }
    @Transactional
    public Notification push(Student student, String message) {
        Notification n = new Notification();
        n.setStudent(student);
        n.setMessage(message);
        return repo.save(n);
    }
    public List<Notification> list(Student student) {
        return repo.findByStudentOrderByCreatedAtDesc(student);
    }
}
