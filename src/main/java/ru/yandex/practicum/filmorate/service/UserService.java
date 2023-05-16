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
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private static int id = 0;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    private int generationId() {
        return ++id;
    }

    public User create(@RequestBody User user) {
        //user.setId(generationId());

        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пусто");
        }
        userStorage.addUser(user);

        return user;
    }

    public User put(@RequestBody User user) {
        userStorage.updateUser(user);

        return user;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(Integer userId) {
        Optional<User> optUser = userStorage.findUserById(userId.toString());
        return userStorage.findUserById(userId);
    }
    public Optional<User> findUserById(String userId) {
        //return userStorage.findUserById(userId);

        return userStorage.findUserById(userId);
    }
    public void addFriend(Integer userId, Integer friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        user.addFriend(friendId);
        put(user);

        friend.addFriend(userId);
        put(friend);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        user.deleteFriend(friendId);
        put(user);

        friend.deleteFriend(userId);
        put(friend);
    }

    public List<User> findFriends(int userId) {
        User user = findUserById(userId);
        Set<Integer> friends = user.getFriends();

        List<User> listFriends = new ArrayList<>();
        for (Integer friendId : friends) {
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

    public void delete(Integer userId) {
        userStorage.deleteUserById(userId);
    }
}
