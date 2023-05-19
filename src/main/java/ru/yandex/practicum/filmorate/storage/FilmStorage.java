package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {
    public List<Film> findAll();

    public int addFilm(Film film);

    public void updateFilm(Film film);

    public Film findFilmById(int filmId);

    public int deleteFilmById(int filmId);

    public List<String> findAllGenres();

    public Genre findGenreById(Integer genreId);

    public String createGenre(String genre);

    public String findMpaById(Integer mpaId);

    public List<String> findAllMpa();
}
