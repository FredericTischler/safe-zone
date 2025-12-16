package com.ecommerce.media.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProductEventTest {

    @Test
    void allArgsConstructorShouldExposeValues() {
        LocalDateTime now = LocalDateTime.now();
        ProductEvent event = new ProductEvent("DELETED", "product-1", "Phone", "seller-1", now);

        assertThat(event.getEventType()).isEqualTo("DELETED");
        assertThat(event.getProductName()).isEqualTo("Phone");
        assertThat(event.getTimestamp()).isEqualTo(now);
    }

    @Test
    void settersShouldWorkWithNoArgsConstructor() {
        ProductEvent event = new ProductEvent();
        event.setEventType("CREATED");
        event.setProductId("product-2");
        event.setSellerId("seller-2");

        assertThat(event.getEventType()).isEqualTo("CREATED");
        assertThat(event.getProductId()).isEqualTo("product-2");
        assertThat(event.getSellerId()).isEqualTo("seller-2");
    }

    @Test
    void equalsAndHashCodeShouldDependOnFields() {
        LocalDateTime now = LocalDateTime.now();
        ProductEvent first = new ProductEvent("UPDATED", "p1", "Phone", "seller", now);
        ProductEvent second = new ProductEvent("UPDATED", "p1", "Phone", "seller", now);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("Phone");
        second.setEventType("DELETED");
        assertThat(first).isNotEqualTo(second);
    }
}
