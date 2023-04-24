package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll().values().stream().collect(Collectors.toList());
    }

    @GetMapping("{userId}")
    public User findUserById(@PathVariable("userId") Integer userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("{userId}/friends")
    public List<User> findFriends(@PathVariable("userId") Integer userId) {
        return userService.findFriends(userId);
    }

    @GetMapping("{userId}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("userId") Integer userId, @PathVariable("otherId") Integer otherId) {
        return userService.findCommonFriends(userId, otherId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        return userService.put(user);
    }

    @PutMapping("{userId}/friends/{friendId}")
    public Integer addFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.addFriend(userId, friendId);

        return friendId;
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public Integer deleteFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(userId, friendId);

        return friendId;
    }
    @DeleteMapping
    public User delete(@Valid @RequestBody User user) {
        return userService.delete(user);
    }
}
