package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import static ru.practicum.shareit.user.UserMapper.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("Получение всех пользователей");
        List<User> users = new ArrayList<>(userRepository.findAll());
        log.info("Найдено {} пользователей", users.size());
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Получение пользователя с id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
        log.info("Пользователь найден: {}", user);
        return toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Создание пользователя: {}", userDto);
        User user = toUser(userDto);
        User savedUser = userRepository.save(user);
        log.info("Пользователь создан: {}", savedUser);
        return toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUserById(UserDto userDto, Long userId) {
        log.info("Обновление пользователя с id: {}", userId);
        User updatingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));

        if (Objects.nonNull(userDto.getName())) updatingUser.setName(userDto.getName());
        if (Objects.nonNull(userDto.getEmail())) updatingUser.setEmail(userDto.getEmail());

        userRepository.save(updatingUser);
        log.info("Пользователь обновлен: {}", updatingUser);
        return toUserDto(updatingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с id: {}", userId);
        userRepository.deleteById(userId);
        log.info("Пользователь удален: {}", userId);
    }
}
