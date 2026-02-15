package ru.practicum.shareit.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks  // Автоматически создаст UserServiceImpl и внедрит userRepository
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        user = new User();
        user.setId(1L);
        user.setName("Алексей");
        user.setEmail("alex@example.com");

        userDto = UserDto.builder()
                .id(1L)
                .name("Алексей")
                .email("alex@example.com")
                .build();
    }

    @Test
    void shouldGetAllUsers() {
        // Мокируем репозиторий
        when(userRepository.findAll()).thenReturn(List.of(user));

        // Вызываем метод сервиса
        Collection<UserDto> result = userService.getAllUsers();

        // Проверяем результат
        assertEquals(1, result.size());
        UserDto returnedDto = result.iterator().next();
        assertEquals(userDto.getId(), returnedDto.getId());
        assertEquals(userDto.getName(), returnedDto.getName());
        assertEquals(userDto.getEmail(), returnedDto.getEmail());

        // Проверяем, что репозиторий был вызван
        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnUserDtoWhenUserExists() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(eq(999L))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        assertTrue(exception.getMessage().contains("Не найден пользователь с id: 999"));
        verify(userRepository).findById(999L);
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionOnSaveFailure() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            userService.createUser(userDto);
        });

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateUserById() {
        UserDto updatedDto = UserDto.builder().name("Алексей").build();
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUserById(updatedDto, 1L);

        assertEquals("Алексей", result.getName());
        assertEquals("alex@example.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void shouldUpdateUserByIdByEmail() {
        UserDto updatedDto = UserDto.builder().email("alex@example.com").build();
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUserById(updatedDto, 1L);

        assertEquals("alex@example.com", result.getEmail());
        assertEquals("Алексей", result.getName());
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistByUpdateUserId() {
        when(userRepository.findById(eq(999L))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.updateUserById(userDto, 999L);
        });

        assertTrue(exception.getMessage().contains("Не найден пользователь с id: 999"));
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldNotThrowExceptionWhenUserDoesNotExist() {
        doNothing().when(userRepository).deleteById(999L);

        userService.deleteUser(999L);

        verify(userRepository).deleteById(999L);
    }
}
