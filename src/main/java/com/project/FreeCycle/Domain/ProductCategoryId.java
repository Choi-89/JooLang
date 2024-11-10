package com.project.FreeCycle.Domain;

import java.io.Serializable;
import java.util.Objects;

public class ProductCategoryId implements Serializable {
    private Long product;
    private Long category;

    // equals() and hashCode() must be overridden
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCategoryId that = (ProductCategoryId) o;
        return Objects.equals(product, that.product) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, category);
    }
}

