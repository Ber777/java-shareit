package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_ERR = "Пользователь с id %d не найден";
    private static final String USER_WITH_SAME_EMAIL_ERR = "Пользователь с email %s уже существует";
    private final UserStorage userStorage;

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId));
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (isEmailUnique(userDto)) {
            return UserMapper.toUserDto(userStorage.createUser(UserMapper.toUser(userDto)));
        } else {
            throw new ValidationException(String.format(USER_WITH_SAME_EMAIL_ERR, userDto.getEmail()));
        }
    }

    @Override
    public UserDto updateUserById(UserDto userDto, Long userId) {
        userDto.setId(userId);
        if (userDto.getEmail() != null && !isEmailUnique(userDto)) {
            throw new ValidationException(String.format(USER_WITH_SAME_EMAIL_ERR, userDto.getEmail()));
        }
        if (getUserById(userId) != null) {
            return UserMapper.toUserDto(userStorage.updateUserById(UserMapper.toUser(userDto)));
        } else {
            throw new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId));
        }
    }

    @Override
    public void deleteUser(Long userId) {
        if (getUserById(userId) != null) {
            userStorage.deleteUser(userId);
        } else {
            throw new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId));
        }
    }

    private boolean isEmailUnique(UserDto user) {
        return getAllUsers().stream()
                .noneMatch(u -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId()));

    }
}
