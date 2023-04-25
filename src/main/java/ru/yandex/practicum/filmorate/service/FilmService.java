package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {
    private static int id = 0;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private int generationId() {
        return ++id;
    }

    public Film create(@RequestBody Film film) {
        film.setId(generationId());
        filmStorage.addFilm(film);

        return film;
    }

    public Film put(@RequestBody Film film) {
        filmStorage.updateFilm(film);

        return film;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public List<Film> findPopularFilms(Integer count) {
        List<Film> allFilms = filmStorage.findAll();
        return allFilms.stream()
                .sorted(Collections.reverseOrder())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Integer likeFilm(Integer filmId, Integer userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);

        film.addLike(userId);
        put(film);

        return userId;
    }

    public Integer deleteLikeFilm(Integer filmId, Integer userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);

        film.deleteLike(userId);
        put(film);

        return userId;
    }

    public void delete(Film film) {
        filmStorage.deleteFilm(film);
    }
}

