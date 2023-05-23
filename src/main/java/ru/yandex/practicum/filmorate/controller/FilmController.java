package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

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
        return filmService.create(film);
    }

    @GetMapping("{filmId}")
    public Film findFilmById(@PathVariable("filmId") long filmId) {
        return filmService.findFilmById(filmId);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        return filmService.put(film);
    }

    @GetMapping("popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.findPopularFilms(count);
    }

    @PutMapping("{filmId}/like/{userId}")
    public long likeFilm(@PathVariable("filmId") long filmId,
                            @PathVariable("userId") long userId) {

        return filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public long deleteLikeFilm(@PathVariable("filmId") long filmId,
                                  @PathVariable("userId") long userId) {

        return filmService.deleteLikeFilm(filmId, userId);
    }

    @DeleteMapping("{filmId}")
    public void delete(@PathVariable("filmId") long filmId) {
        filmService.delete(filmId);
    }
}
