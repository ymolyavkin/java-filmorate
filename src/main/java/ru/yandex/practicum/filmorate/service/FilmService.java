package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    //  private final UserStorage userDaoStorage;
    // private final FilmStorage filmDaoStorage;

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

    public Film findFilmById(int filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public List<Film> findPopularFilms(int count) {
        List<Film> allFilms = filmStorage.findAll();
        return allFilms.stream()
                .sorted(Collections.reverseOrder())
                .limit(count)
                .collect(Collectors.toList());
    }

    public int likeFilm(int filmId, int userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);

        film.addLike(userId);
        put(film);

        return userId;
    }

    public int deleteLikeFilm(int filmId, int userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);

        film.deleteLike(userId);
        put(film);

        return userId;
    }

    public int delete(int filmId) {
        return filmStorage.deleteFilmById(filmId);
    }

    public List<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }

    public Genre findGenreById(Integer genreId) {
        return filmStorage.findGenreById(genreId);
    }

    public String createGenre(String genre) {
        return filmStorage.createGenre(genre);
    }

    public String findMpaById(Integer mpaId) {
        return filmStorage.findMpaById(mpaId);
    }

    public List<String> findAllMpa() {
        return filmStorage.findAllMpa();
    }
}

