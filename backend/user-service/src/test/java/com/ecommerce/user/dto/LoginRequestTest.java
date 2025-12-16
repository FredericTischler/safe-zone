package com.ecommerce.user.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void allArgsConstructorShouldExposeValues() {
        LoginRequest request = new LoginRequest("alice@mail.com", "secret");

        assertThat(request.getEmail()).isEqualTo("alice@mail.com");
        assertThat(request.getPassword()).isEqualTo("secret");
    }

    @Test
    void settersShouldWorkWithNoArgsConstructor() {
        LoginRequest request = new LoginRequest();
        request.setEmail("bob@mail.com");
        request.setPassword("pwd");

        assertThat(request.getEmail()).isEqualTo("bob@mail.com");
        assertThat(request.getPassword()).isEqualTo("pwd");
    }

    @Test
    void equalsAndHashCodeShouldWork() {
        LoginRequest first = new LoginRequest("alice@mail.com", "pwd");
        LoginRequest second = new LoginRequest("alice@mail.com", "pwd");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("alice@mail.com");
        second.setEmail("bob@mail.com");
        assertThat(first).isNotEqualTo(second);
    }
}
