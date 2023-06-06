package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public List<User> findAll();

    public long addUser(User user);

    public void updateUser(User user);

    public User findUserById(long userId);

    public long deleteUserById(long userId);

    public long deleteFriendFromUser(long userId, long friendId);

    public void addFriend(long userId, long friendId);
}
