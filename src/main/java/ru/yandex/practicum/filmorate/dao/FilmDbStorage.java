package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public void addFilm(Film film) {

    }

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public Film findFilmById(int filmId) {
        return null;
    }

    @Override
    public int deleteFilmById(int filmId) {

        return 0;
    }
}
