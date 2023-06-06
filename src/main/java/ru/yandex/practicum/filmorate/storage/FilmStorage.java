package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public List<Film> findAll();

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public Film findFilmById(long filmId);

    public long deleteFilmById(long filmId);

    public List<Film> findPopularFilms(int count);
}
