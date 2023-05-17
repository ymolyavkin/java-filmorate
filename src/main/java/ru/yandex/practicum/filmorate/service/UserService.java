package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public User create(@RequestBody User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пусто");
        }
        int idUser = userStorage.addUser(user);
        user.setId(idUser);
        return user;
    }

    public User put(@RequestBody User user) {
        userStorage.updateUser(user);

        return user;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(int userId) {
        return userStorage.findUserById(userId);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriendFromUser(userId, friendId);
    }

    public List<User> findFriends(int userId) {
        User user = findUserById(userId);
        Set<Integer> friends = user.getFriends();

        List<User> listFriends = new ArrayList<>();
        for (int friendId : friends) {
            listFriends.add(findUserById(friendId));
        }
        return listFriends;
    }

    public List<User> findCommonFriends(int userId, int otherId) {
        List<User> listCommonFriends = new ArrayList<>();
        User user = findUserById(userId);
        User other = findUserById(otherId);

        List<Integer> commonId = user.getCommonFriends(other);
        commonId.stream().forEach(id -> listCommonFriends.add(findUserById(id)));
        return listCommonFriends;
    }

    public int delete(int userId) {
        return userStorage.deleteUserById(userId);
    }
}
