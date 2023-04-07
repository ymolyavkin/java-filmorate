package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            //user.setName(user.getLogin());
            log.debug("Логин пользователя пуст или содержит пробелы.");
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            //log.debug("Логин пользователя пуст или содержит пробелы.");
            //throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        /*if (user.getId() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Логин пользователя пуст или содержит пробелы.");
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }*/
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Адрес электронной почты не прошел проверку.");
            throw new ValidationException("Адрес электронной почты не прошел проверку.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Некорректная дата рождения.");
            throw new ValidationException("Некорректная дата рождения.");
        }
        /*if (users.containsKey(user.getId())) {
            log.debug("Пользователь с id " + user.getId() + " уже зарегистрирован.");
            throw new UserAlreadyExistException("Пользователь с id " + user.getId() + " уже зарегистрирован.");
        }*/
        // TODO: 06.04.2023 this check should be moved to the toString() method
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пусто");
            System.out.println("Имя пользователя пусто");
        }

        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.debug("Пользователь с id " + user.getId() + " не найден.");
            throw new ValidationException("Пользователь с id " + user.getId() + " не найден.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Логин пользователя пуст или содержит пробелы.");
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Адрес электронной почты не прошел проверку.");
            throw new ValidationException("Адрес электронной почты не прошел проверку.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Некорректная дата рождения.");
            throw new ValidationException("Некорректная дата рождения.");
        }
        users.put(user.getId(), user);

        return user;
    }
}
