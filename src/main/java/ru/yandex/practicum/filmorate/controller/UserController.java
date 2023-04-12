package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.controller.validation.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.controller.validation.Validator.validationIsEmptyAndContainsSpaces;


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
        //if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
        if (Validator.validationIsEmptyAndContainsSpaces(user.getLogin())) {
            log.error("Логин пользователя пуст или содержит пробелы.");
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Адрес электронной почты не прошел проверку.");
            throw new ValidationException("Адрес электронной почты не прошел проверку.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Некорректная дата рождения.");
            throw new ValidationException("Некорректная дата рождения.");
        }
       // if (user.getName() == null || user.getName().isBlank()) {
        if (Validator.validationIsEmpty(user.getName())) {
            user.setId(2);
            user.setName(user.getLogin());
            log.error("Имя пользователя пусто");
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
        if (Validator.validationIsEmptyAndContainsSpaces(user.getLogin())) {
            log.error("Логин пользователя пуст или содержит пробелы.");
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Адрес электронной почты не прошел проверку.");
            throw new ValidationException("Адрес электронной почты не прошел проверку.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Некорректная дата рождения.");
            throw new ValidationException("Некорректная дата рождения.");
        }
        users.put(user.getId(), user);

        return user;
    }
}
