package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
@Component
public class InMemoryFilmStorage implements FilmStorage {
    @Override
    public Collection<Film> findAll() {
        return null;
    }

    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Film findFilmById(Long filmId) {
        return null;
    }

    @Override
    public Film deleteFilm(Film film) {
        return null;
    }
}
