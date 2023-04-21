package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
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
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(String.format(
                    "Пользователь с id %s не найден.",
                    user.getId()
            ));
        }
        users.put(user.getId(), user);
    }

    @Override
    public User findUserById(Integer userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId());
    }
}
