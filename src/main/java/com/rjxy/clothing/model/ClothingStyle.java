package com.rjxy.clothing.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class ClothingStyle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String imageUrl;
    @OneToMany(mappedBy = "style", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ClothingVariant> variants = new ArrayList<>();

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public List<ClothingVariant> getVariants() {
        return variants;
    }
    public void setVariants(List<ClothingVariant> variants) {
        this.variants = variants;
    }
}
