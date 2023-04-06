package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidUserNameException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        if(user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        if(user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Адрес электронной почты не прошел проверку.");
        }
        if(users.containsKey(user.getId())) {
            throw new UserAlreadyExistException("Пользователь с id " +
                    user.getId() + " уже зарегистрирован.");
        }
        // TODO: 06.04.2023 this check should be moved to the toString() method
        if (user.getName()==null || user.getName().isBlank()) {
            System.out.println("Имя пользователя пусто");
        }
        // TODO: 06.04.2023 We should add a date of birth validation 
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
