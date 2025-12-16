package com.ecommerce.user.dto;

import com.ecommerce.user.model.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestTest {

    @Test
    void allArgsConstructorShouldPopulateFields() {
        RegisterRequest request = new RegisterRequest("Alice", "alice@mail.com", "password123", Role.CLIENT);

        assertThat(request.getName()).isEqualTo("Alice");
        assertThat(request.getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    void settersShouldAllowMutation() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Bob");
        request.setEmail("bob@mail.com");
        request.setPassword("pwd");
        request.setRole(Role.SELLER);

        assertThat(request.getEmail()).isEqualTo("bob@mail.com");
        assertThat(request.getRole()).isEqualTo(Role.SELLER);
    }

    @Test
    void equalsAndHashCodeShouldDependOnValues() {
        RegisterRequest first = new RegisterRequest("Alice", "alice@mail.com", "password123", Role.CLIENT);
        RegisterRequest second = new RegisterRequest("Alice", "alice@mail.com", "password123", Role.CLIENT);

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
        assertThat(first.toString()).contains("Alice");
        second.setRole(Role.SELLER);
        assertThat(first).isNotEqualTo(second);
    }
}
