package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

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
            log.error("Пользователь с id " + user.getId() + " не найден.");
            throw new NotFoundException(String.format(
                    "Пользователь с id %s не найден.",
                    user.getId()
            ));
        }
        users.put(user.getId(), user);
    }

    @Override
    public User findUserById(Integer userId) {
        if (!users.containsKey(userId)) {
            log.error("Пользователь с id " + userId + " не найден.");
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        return users.get(userId);
    }

    @Override
    public void deleteUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id " + user.getId() + " не найден.");
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        users.remove(user.getId());
    }
}
