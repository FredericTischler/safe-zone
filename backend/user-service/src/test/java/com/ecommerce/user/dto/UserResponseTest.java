package com.ecommerce.user.dto;

import com.ecommerce.user.model.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void allArgsConstructorShouldExposeValues() {
        LocalDateTime now = LocalDateTime.now();
        UserResponse response = new UserResponse("id", "Alice", "alice@mail.com", Role.CLIENT,
            "/avatars/a.png", now.minusDays(1), now);

        assertThat(response.getId()).isEqualTo("id");
        assertThat(response.getRole()).isEqualTo(Role.CLIENT);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void settersShouldWorkWithNoArgsConstructor() {
        UserResponse response = new UserResponse();
        response.setId("id-2");
        response.setName("Bob");
        response.setAvatar("/a.png");

        assertThat(response.getName()).isEqualTo("Bob");
        assertThat(response.getAvatar()).contains(".png");
    }

    @Test
    void equalsAndHashCodeShouldUseAllFields() {
        LocalDateTime now = LocalDateTime.now();
        UserResponse first = new UserResponse("id", "Alice", "alice@mail.com", Role.CLIENT,
            "/avatars/a.png", now.minusDays(1), now);
        UserResponse second = new UserResponse("id", "Alice", "alice@mail.com", Role.CLIENT,
            "/avatars/a.png", now.minusDays(1), now);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("Alice");
        second.setEmail("other@mail.com");
        assertThat(first).isNotEqualTo(second);
    }
}
