package ru.practicum.shareit.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.NotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

@SpringBootTest
@ActiveProfiles("test")
@Transactional  // каждый тест в транзакции, после откат
public class UserServiceImplTests {

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager em;

    private UserDto savedUserDto;

    @BeforeEach
    void setUp() {
        // Создаём и сохраняем тестового пользователя перед каждым тестом
        savedUserDto = userService.createUser(makeUserDto("john@example.com", "Иван", "Березин"));
    }

    // Вспомогательный метод для создания DTO
    private UserDto makeUserDto(String email, String firstName, String lastName) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(firstName + " " + lastName);  // в DTO поле name
        return dto;
    }

    @Test
    void shouldGetAllUsers() {
        Collection<UserDto> users = userService.getAllUsers();

        assertThat(users).isNotEmpty();
        assertThat(users).anyMatch(u -> u.getId().equals(savedUserDto.getId()));
        assertThat(users.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldGetUserById() {
        UserDto found = userService.getUserById(savedUserDto.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(savedUserDto.getId());
        assertThat(found.getName()).isEqualTo(savedUserDto.getName());
        assertThat(found.getEmail()).isEqualTo(savedUserDto.getEmail());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistByGetUserById() {
        // Ожидание исключения
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getUserById(999L);  // заведомо несуществующий id
        });
    }

    @Test
    void shouldCreateUser() {
        UserDto newUserDto = makeUserDto("new@example.com", "Пётр", "Петров");

        UserDto created = userService.createUser(newUserDto);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Пётр Петров");
        assertThat(created.getEmail()).isEqualTo("new@example.com");

        // Дополнительно: проверяем в БД через EntityManager
        User userInDb = em.find(User.class, created.getId());
        assertThat(userInDb).isNotNull();
        assertThat(userInDb.getName()).isEqualTo("Пётр Петров");
        assertThat(userInDb.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void shouldUpdateUserById() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Алексей Новиков");
        updateDto.setEmail("updated@example.com");

        UserDto updated = userService.updateUserById(updateDto, savedUserDto.getId());

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(savedUserDto.getId());
        assertThat(updated.getName()).isEqualTo("Алексей Новиков");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");

        // Проверяем в БД
        User userInDb = em.find(User.class, savedUserDto.getId());
        assertThat(userInDb.getName()).isEqualTo("Алексей Новиков");
        assertThat(userInDb.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistByUpdateUserId() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Неважно");


        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.updateUserById(updateDto, 999L);
        });
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(savedUserDto.getId());

        // Проверяем, что пользователь больше не существует
        boolean exists = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.id = :id", Long.class)
                .setParameter("id", savedUserDto.getId())
                .getSingleResult() > 0;

        assertThat(exists).isFalse();

        // Попытка получить удалённого пользователя должна вызвать исключение
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getUserById(savedUserDto.getId());
        });
    }

    @Test
    void shouldNotDeleteUser() {
        // Удаление несуществующего пользователя не должно вызывать ошибку
        Assertions.assertDoesNotThrow(() -> {
            userService.deleteUser(999L);
        });
    }
}
