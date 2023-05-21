package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        return films.values().stream().collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        return films.get(filmId);
    }

    @Override
    public int deleteFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        films.remove(filmId);
        return filmId;
    }

    @Override
    public List<Genre> findAllGenres() {
        return null;
    }

    @Override
    public Genre findGenreById(int genreId) {
        return null;
    }

    @Override
    public Mpa findMpaById(int mpaId) {
        return null;
    }

    @Override
    public List<Mpa> findAllMpa() {
        return null;
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        return null;
    }
}
