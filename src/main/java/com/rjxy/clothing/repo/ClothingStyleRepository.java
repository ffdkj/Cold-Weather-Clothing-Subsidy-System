package com.rjxy.clothing.repo;

import com.rjxy.clothing.model.ClothingStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClothingStyleRepository extends JpaRepository<ClothingStyle, Long> {
    List<ClothingStyle> findByGender(String gender);
}
