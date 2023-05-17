package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public int addUser(User user) {
        users.put(user.getId(), user);
        return user.getId();
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
    public User findUserById(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        return users.get(userId);
    }

    @Override
    public int deleteUserById(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        users.remove(userId);
        return userId;
    }

    @Override
    public void addFriend(int userId, int friendId) {

    }
}
