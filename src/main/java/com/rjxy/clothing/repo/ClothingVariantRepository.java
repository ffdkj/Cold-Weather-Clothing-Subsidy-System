package com.rjxy.clothing.repo;

import com.rjxy.clothing.model.ClothingVariant;
import com.rjxy.clothing.model.ClothingStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClothingVariantRepository extends JpaRepository<ClothingVariant, Long> {
    List<ClothingVariant> findByStyle(ClothingStyle style);
}
