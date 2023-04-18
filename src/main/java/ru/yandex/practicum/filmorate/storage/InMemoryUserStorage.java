package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static Integer id = 0;
    private final Map<Integer, User> users = new HashMap<>();
    @Override
    public Map<Integer, User> findAll() {
        return users;
    }

    @Override
    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User findUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId());
    }
}
