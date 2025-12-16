package com.rjxy.clothing.service;

import com.rjxy.clothing.model.ClothingStyle;
import com.rjxy.clothing.model.ClothingVariant;
import com.rjxy.clothing.repo.ClothingStyleRepository;
import com.rjxy.clothing.repo.ClothingVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class StyleService {
    private final ClothingStyleRepository styleRepo;
    private final ClothingVariantRepository variantRepo;
    public StyleService(ClothingStyleRepository styleRepo, ClothingVariantRepository variantRepo) {
        this.styleRepo = styleRepo;
        this.variantRepo = variantRepo;
    }
    @Transactional
    public ClothingStyle createStyle(String name, String gender, String imageUrl, List<ClothingVariant> variants) {
        ClothingStyle s = new ClothingStyle();
        s.setName(name);
        s.setGender(gender);
        s.setImageUrl(imageUrl);
        ClothingStyle saved = styleRepo.save(s);
        for (ClothingVariant v : variants) {
            v.setStyle(saved);
            variantRepo.save(v);
        }
        return saved;
    }
    public List<ClothingStyle> listByGender(String gender) {
        return styleRepo.findByGender(gender);
    }
    public Optional<ClothingVariant> variantById(Long id) {
        return variantRepo.findById(id);
    }
}
