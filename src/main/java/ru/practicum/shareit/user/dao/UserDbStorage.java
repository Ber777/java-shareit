/*package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
    }

    @Override
    public User createUser(User user) {
        User usr = User.builder()
                .id(getNextId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
        users.put(usr.getId(), usr);

        return usr;
    }

    @Override
    public User updateUserById(User newUser) {
        User oldUser = users.get(newUser.getId());
        oldUser.setEmail(newUser.getEmail() != null ? newUser.getEmail() : oldUser.getEmail());
        oldUser.setName(newUser.getName() != null ? newUser.getName() : oldUser.getName());

        users.put(oldUser.getId(), oldUser);
        return oldUser;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
*/