package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    @GetMapping
    public List<Genre> findAllGenres() {
        return filmService.findAllGenres();
    }

    @GetMapping("{genreId}")
    public Genre findGenreById(@PathVariable("genreId") Integer genreId) {
        return filmService.findGenreById(genreId);
    }
}
