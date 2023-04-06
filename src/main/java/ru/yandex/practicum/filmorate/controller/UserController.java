package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidUserNameException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if(user.getName() == null || user.getName().isBlank()) {
            throw new InvalidUserNameException("Имя пользователя не может быть пустым.");
        }
        if(users.containsKey(user.getId())) {
            throw new UserAlreadyExistException("Пользователь с id " +
                    user.getId() + " уже зарегистрирован.");
        }
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidUserNameException("Имя пользователя не может быть пустым.");
        }
        users.put(user.getId(), user);

        return user;
    }
}
