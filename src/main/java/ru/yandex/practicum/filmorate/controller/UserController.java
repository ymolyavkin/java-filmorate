package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.controller.validation.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int generationId() {
        return ++id;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        user.setId(generationId());
        if (Validator.validationFailedIsEmptyAndContainsSpaces(user.getLogin())) {
            log.error("Логин пользователя пуст или содержит пробелы.");
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        if (Validator.validationFailedEmail(user.getEmail())) {
            log.error("Адрес электронной почты не прошел проверку.");
            throw new ValidationException("Адрес электронной почты не прошел проверку.");
        }
        if (Validator.validationFailedBirthdayIsAfterNow(user.getBirthday())) {
            log.error("Некорректная дата рождения.");
            throw new ValidationException("Некорректная дата рождения.");
        }
        if (Validator.validationFailedIsEmpty(user.getName())) {
            user.setId(2);
            user.setName(user.getLogin());
            log.debug("Имя пользователя пусто");
            System.out.println("Имя пользователя пусто");
        }

        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id " + user.getId() + " не найден.");
            throw new ValidationException("Пользователь с id " + user.getId() + " не найден.");
        }
        if (Validator.validationFailedIsEmptyAndContainsSpaces(user.getLogin())) {
            log.error("Логин пользователя пуст или содержит пробелы.");
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        if (Validator.validationFailedEmail(user.getEmail())) {
            log.error("Адрес электронной почты не прошел проверку.");
            throw new ValidationException("Адрес электронной почты не прошел проверку.");
        }
        if (Validator.validationFailedBirthdayIsAfterNow(user.getBirthday())) {
            log.error("Некорректная дата рождения.");
            throw new ValidationException("Некорректная дата рождения.");
        }
        users.put(user.getId(), user);

        return user;
    }
}
