package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@RequiredArgsConstructor
@Service
public class FilmService {
    private static long id = 0;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    private long generationId() {
        return ++id;
    }

    public Film create(@RequestBody Film film) {
        film.setId(generationId());

        return filmStorage.addFilm(film);
    }

    public Film put(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(long filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.findPopularFilms(count);
    }

    public long likeFilm(long filmId, long userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);

        film.addLike(userId);
        put(film);

        return userId;
    }

    public long deleteLikeFilm(long filmId, long userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);

        film.deleteLike(userId);
        put(film);

        return userId;
    }

    public long delete(long filmId) {
        return filmStorage.deleteFilmById(filmId);
    }

    public List<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }

    public Genre findGenreById(int genreId) {
        return filmStorage.findGenreById(genreId);
    }

    public Mpa findMpaById(int mpaId) {
        return filmStorage.findMpaById(mpaId);
    }

    public List<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }
}

