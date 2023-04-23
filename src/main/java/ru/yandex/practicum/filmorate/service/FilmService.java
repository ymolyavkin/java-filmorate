package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static int id = 0;
    private final FilmStorage filmStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    private int generationId() {
        System.out.println("id="+id);
        return ++id;
    }

    public Film create(@RequestBody Film film) {
        film.setId(generationId());
        filmStorage.addFilm(film);
        System.out.println("FROM service create film with id = "+film.getId());
        return film;
    }

    public Film put(@RequestBody Film film) {
        filmStorage.updateFilm(film);

        return film;
    }

    public Map<Integer, Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public List<Film> findPopularFilms(Integer count) {
        Map<Integer, Film> allFilms = filmStorage.findAll();
        return allFilms.values().stream()
                //   .filter(s -> s.numberOfLikes() > 0)
                .sorted(Collections.reverseOrder())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film findUserById(Integer filmId) {
        return filmStorage.findFilmById(filmId);
    }
}

