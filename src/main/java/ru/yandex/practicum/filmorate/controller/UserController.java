package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
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
