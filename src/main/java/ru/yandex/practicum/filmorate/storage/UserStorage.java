package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public List<User> findAll();

    public int addUser(User user);

    public void updateUser(User user);

    public User findUserById(int userId);

    public int deleteUserById(int userId);

    public int deleteFriendFromUser(int userId, int friendId);

    public void addFriend(int userId, int friendId);
}
