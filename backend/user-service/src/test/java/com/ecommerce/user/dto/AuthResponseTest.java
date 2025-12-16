package com.ecommerce.user.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseTest {

    @Test
    void fullConstructorShouldPopulateFields() {
        AuthResponse response = new AuthResponse(
            "token",
            "Bearer",
            "user-1",
            "alice@mail.com",
            "Alice",
            "CLIENT",
            "/avatars/a.png"
        );

        assertThat(response.getToken()).isEqualTo("token");
        assertThat(response.getRole()).isEqualTo("CLIENT");
        assertThat(response.getAvatar()).contains(".png");
    }

    @Test
    void customConstructorShouldDefaultTypeToBearer() {
        AuthResponse response = new AuthResponse("token", "user-1", "alice@mail.com", "Alice", "SELLER", "/a.png");

        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo("user-1");
    }

    @Test
    void equalsAndHashCodeShouldUseAllFields() {
        AuthResponse first = new AuthResponse("token", "Bearer", "user-1", "alice@mail.com", "Alice", "SELLER", "/a.png");
        AuthResponse second = new AuthResponse("token", "Bearer", "user-1", "alice@mail.com", "Alice", "SELLER", "/a.png");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("alice@mail.com");
        second.setRole("CLIENT");
        assertThat(first).isNotEqualTo(second);
    }
}
