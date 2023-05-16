package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        System.out.println();
        return filmService.create(film);
    }

    @GetMapping("{filmId}")
    public Film findFilmById(@PathVariable("filmId") Integer filmId) {
        return filmService.findFilmById(filmId);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        filmService.put(film);
        return film;
    }

    @GetMapping("popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.findPopularFilms(count);
    }

    @PutMapping("{filmId}/like/{userId}")
    public Integer likeFilm(@PathVariable("filmId") Integer filmId,
                            @PathVariable("userId") Integer userId) {

        return filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public Integer deleteLikeFilm(@PathVariable("filmId") Integer filmId,
                                  @PathVariable("userId") Integer userId) {

        return filmService.deleteLikeFilm(filmId, userId);
    }

    @DeleteMapping("{filmId}")
    public void delete(@PathVariable("filmId") Integer filmId) {
        filmService.delete(filmId);
    }
}
