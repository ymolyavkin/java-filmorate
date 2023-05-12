package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public List<Film> findAll();

    public void addFilm(Film film);

    public void updateFilm(Film film);

    public Film findFilmById(Integer filmId);

    public void deleteFilm(Film film);
}
