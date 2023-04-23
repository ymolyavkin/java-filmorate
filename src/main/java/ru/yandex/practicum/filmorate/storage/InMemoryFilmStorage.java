package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    @Override
    public Map<Integer, Film> findAll() {
        return films;
    }

    @Override
    public void addFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Film findFilmById(Integer filmId) {
        return films.get(filmId);
    }

    @Override
    public void deleteFilm(Film film) {
        films.remove(film.getId());
    }
}
