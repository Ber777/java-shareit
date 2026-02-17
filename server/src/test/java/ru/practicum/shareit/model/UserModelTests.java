package ru.practicum.shareit.model;

import ru.practicum.shareit.user.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

public class UserModelTests {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    // Тестируем сеттеры и геттеры
    @Test
    void shouldTestSettersAndGetters() {
        user.setId(1L);
        user.setName("John Ber");
        user.setEmail("john@example.com");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John Ber");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldTestNoArgsConstructor() {
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
    }

    // Тестируем equals()
    @Test
    void shouldTestEqualsSameInstance() {
        assertThat(user).isEqualTo(user);
    }

    @Test
    void shouldTestEqualsNull() {
        assertThat(user).isNotEqualTo(null);
    }

    @Test
    void shouldTestEqualsDifferentClass() {
        Object obj = new Object();
        assertThat(user).isNotEqualTo(obj);
    }

    @Test
    void shouldTestEqualsSameId() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(1L);

        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void shouldTestEqualsDifferentId() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void shouldTestEqualsIdIsNull() {
        User user1 = new User();
        user1.setId(null);

        User user2 = new User();
        user2.setId(null);

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void shouldTestHashCodeSameClass() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(1L);

        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void shouldTestHashCodeConsistency() {
        int hashCode = user.hashCode();

        assertThat(user.hashCode()).isEqualTo(hashCode);
    }

    @Test
    void shouldTestToString() {
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        String expected = "User(id=1, name=John, email=john@example.com)";
        assertThat(user.toString()).contains(expected);
    }
}