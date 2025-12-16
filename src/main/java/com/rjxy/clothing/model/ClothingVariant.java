package com.rjxy.clothing.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class ClothingVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JsonBackReference
    private ClothingStyle style;
    @Column(nullable = false)
    private String sizeLabel;
    @Column(nullable = false)
    private String productCode;

    public Long getId() {
        return id;
    }
    public ClothingStyle getStyle() {
        return style;
    }
    public void setStyle(ClothingStyle style) {
        this.style = style;
    }
    public String getSizeLabel() {
        return sizeLabel;
    }
    public void setSizeLabel(String sizeLabel) {
        this.sizeLabel = sizeLabel;
    }
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
