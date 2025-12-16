package com.rjxy.clothing.repo;

import com.rjxy.clothing.model.Notification;
import com.rjxy.clothing.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudentOrderByCreatedAtDesc(Student student);
}
