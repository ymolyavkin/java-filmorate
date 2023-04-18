package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Map;

public interface UserStorage {
    public Map<Integer, User> findAll();
    public void addUser(User user);
    public void updateUser(User user);
    public User findUserById(Long userId);
    public void deleteUser(User user);
}
