package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidFilmNameException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым.");
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Длина описания превышает допустимый предел.");
            throw new ValidationException("Длина описания превышает допустимый предел.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Некорректная дата релиза.");
            throw new ValidationException("Некорректная дата релиза.");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (films.containsKey(film.getId())) {
            log.error("Фильм с id " + film.getId() + " уже есть в коллекции.");
            throw new UserAlreadyExistException("Фильм с id " + film.getId() + " уже есть в коллекции.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id " + film.getId() + " не найден.");
            throw new ValidationException("Фильм с id " + film.getId() + " не найден.");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым.");
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Длина описания превышает допустимый предел.");
            throw new ValidationException("Длина описания превышает допустимый предел.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Некорректная дата релиза.");
            throw new ValidationException("Некорректная дата релиза.");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        films.put(film.getId(), film);
        return film;
    }
}
