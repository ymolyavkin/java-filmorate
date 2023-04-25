package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private static int id = 0;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private int generationId() {
        return ++id;
    }

    public User create(@RequestBody User user) {
        user.setId(generationId());

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
        // В классе User я использую метод, который находит пересечение двух коллекций, но он у меня возвращает список
        // идентификаторов пользователей List<Integer>, а мне здесь нужен List<User>
        commonId.stream().forEach(id -> listCommonFriends.add(findUserById(id)));
        return listCommonFriends;
    }

    public User delete(User user) {
        userStorage.deleteUser(user);

        return user;
    }
}
