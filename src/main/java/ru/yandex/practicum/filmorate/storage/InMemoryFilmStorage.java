package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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
    public int addFilm(Film film) {
        films.put(film.getId(), film);
        return film.getId();
    }

    @Override
    public void updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        films.put(film.getId(), film);
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
    public List<String> findAllGenres() {
        return null;
    }

    @Override
    public List<String> findGenreById(Integer genreId) {
        return null;
    }

    @Override
    public String createGenre(String genre) {
        return null;
    }

    @Override
    public String findMpaById(Integer mpaId) {
        return null;
    }

    @Override
    public List<String> findAllMpa() {
        return null;
    }
}
