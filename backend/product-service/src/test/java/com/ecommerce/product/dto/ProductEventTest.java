package com.ecommerce.product.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProductEventTest {

    @Test
    void allArgsConstructorShouldExposeValues() {
        LocalDateTime now = LocalDateTime.now();
        ProductEvent event = new ProductEvent("CREATED", "product-1", "Phone", "seller-1", now);

        assertThat(event.getEventType()).isEqualTo("CREATED");
        assertThat(event.getProductName()).isEqualTo("Phone");
        assertThat(event.getTimestamp()).isEqualTo(now);
    }

    @Test
    void settersShouldWorkWithDefaultConstructor() {
        ProductEvent event = new ProductEvent();
        event.setEventType("DELETED");
        event.setProductId("product-2");
        event.setSellerId("seller-2");

        assertThat(event.getEventType()).isEqualTo("DELETED");
        assertThat(event.getProductId()).isEqualTo("product-2");
        assertThat(event.getSellerId()).isEqualTo("seller-2");
    }

    @Test
    void equalsAndHashCodeShouldConsiderValues() {
        LocalDateTime now = LocalDateTime.now();
        ProductEvent first = new ProductEvent("CREATED", "product-1", "Phone", "seller-1", now);
        ProductEvent second = new ProductEvent("CREATED", "product-1", "Phone", "seller-1", now);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("Phone");
        second.setProductName("Tablet");
        assertThat(first).isNotEqualTo(second);
    }
}
