package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidFilmNameException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if(film.getName() == null || film.getName().isBlank()) {
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }
        if(films.containsKey(film.getId())) {
            throw new UserAlreadyExistException("Фильм с id " +
                    film.getId() + " уже есть в коллекции.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        if(film.getName() == null || film.getName().isBlank()) {
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }
        films.put(film.getId(), film);

        return film;
    }
}
