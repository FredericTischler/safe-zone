package com.ecommerce.user.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void customConstructorShouldInitializeTimestamps() {
        User user = new User("Alice", "alice@mail.com", "hashed", Role.CLIENT);

        assertThat(user.getName()).isEqualTo("Alice");
        assertThat(user.getRole()).isEqualTo(Role.CLIENT);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    void allArgsConstructorShouldExposeValues() {
        User user = new User("id", "Bob", "bob@mail.com", "pwd", Role.SELLER, "/a.png", null, null);

        assertThat(user.getId()).isEqualTo("id");
        assertThat(user.getAvatar()).isEqualTo("/a.png");
    }

    @Test
    void equalsAndHashCodeShouldDependOnFields() {
        User first = new User("id", "Bob", "bob@mail.com", "pwd", Role.SELLER, "/a.png", null, null);
        User second = new User("id", "Bob", "bob@mail.com", "pwd", Role.SELLER, "/a.png", null, null);

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
        assertThat(first.toString()).contains("bob@mail.com");
        second.setEmail("alice@mail.com");
        assertThat(first).isNotEqualTo(second);
    }
}
