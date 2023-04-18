package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> findAll();
    public Film addFilm(Film film);
    public Film updateFilm(Film film);
    public Film findFilmById(Long filmId);
    public Film deleteFilm(Film film);
}
