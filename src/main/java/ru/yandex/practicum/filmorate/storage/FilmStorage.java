package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {
    public List<Film> findAll();

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public Film findFilmById(int filmId);

    public int deleteFilmById(int filmId);

    public List<Genre> findAllGenres();

    public Genre findGenreById(int genreId);

    public Mpa findMpaById(int mpaId);

    public List<Mpa> findAllMpa();

    public List<Film> findPopularFilms(int count);
}
