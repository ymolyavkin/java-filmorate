package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

public class UserDbStorage implements UserStorage {
    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public User findUserById(Integer userId) {
        return null;
    }

    @Override
    public void deleteUser(User user) {

    }
}
