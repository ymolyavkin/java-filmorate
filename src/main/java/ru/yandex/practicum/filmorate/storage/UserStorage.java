package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    public List<User> findAll();

    public void addUser(User user);

    public void updateUser(User user);

    public User findUserById(Integer userId);

    public void deleteUser(User user);
}
