package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public Collection<User> findAll();
    public User addUser(User user);
    public User updateUser(User user);
    public User findUserById(Long userId);
    public User deleteUser(User user);
}
