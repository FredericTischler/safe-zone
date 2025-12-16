package com.ecommerce.product.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRequestTest {

    @Test
    void allArgsConstructorShouldExposeValues() {
        ProductRequest request = new ProductRequest("Phone", "Desc", 999.0, "Tech", 5);

        assertThat(request.getName()).isEqualTo("Phone");
        assertThat(request.getStock()).isEqualTo(5);
        assertThat(request.getCategory()).isEqualTo("Tech");
    }

    @Test
    void settersShouldWorkWithNoArgsConstructor() {
        ProductRequest request = new ProductRequest();
        request.setName("Tablet");
        request.setDescription("New");
        request.setPrice(500.0);
        request.setCategory("Gadgets");
        request.setStock(10);

        assertThat(request.getDescription()).isEqualTo("New");
        assertThat(request.getPrice()).isEqualTo(500.0);
    }

    @Test
    void equalsAndHashCodeShouldDependOnFields() {
        ProductRequest first = new ProductRequest("Phone", "Desc", 100.0, "Tech", 5);
        ProductRequest second = new ProductRequest("Phone", "Desc", 100.0, "Tech", 5);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("Phone");
        second.setName("Tablet");
        assertThat(first).isNotEqualTo(second);
    }
}
