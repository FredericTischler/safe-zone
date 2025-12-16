package com.ecommerce.media.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MediaResponseTest {

    @Test
    void allArgsConstructorShouldPopulateFields() {
        LocalDateTime now = LocalDateTime.now();
        MediaResponse response = new MediaResponse("id", "product-1", "file.png", "image/png",
                100L, "seller", "/api/media/file/product-1/file.png", now);

        assertThat(response.getId()).isEqualTo("id");
        assertThat(response.getUploadedAt()).isEqualTo(now);
        assertThat(response.getUrl()).contains("file.png");
    }

    @Test
    void settersShouldWorkWithNoArgsConstructor() {
        MediaResponse response = new MediaResponse();
        response.setId("id-2");
        response.setFilename("file.webp");
        response.setSize(200L);

        assertThat(response.getId()).isEqualTo("id-2");
        assertThat(response.getFilename()).isEqualTo("file.webp");
        assertThat(response.getSize()).isEqualTo(200L);
    }

    @Test
    void equalsAndHashCodeShouldConsiderAllFields() {
        LocalDateTime now = LocalDateTime.now();
        MediaResponse first = new MediaResponse("id", "product-1", "file.png", "image/png",
            100L, "seller", "/api/media/file/product-1/file.png", now);
        MediaResponse second = new MediaResponse("id", "product-1", "file.png", "image/png",
            100L, "seller", "/api/media/file/product-1/file.png", now);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("product-1");
        second.setFilename("different.png");
        assertThat(first).isNotEqualTo(second);
    }
}
