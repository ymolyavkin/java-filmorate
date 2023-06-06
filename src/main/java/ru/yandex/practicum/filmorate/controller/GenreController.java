package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> findAllGenres() {
        return genreService.findAllGenres();
    }

    @GetMapping("{genreId}")
    public Genre findGenreById(@PathVariable("genreId") int genreId) {
        return genreService.findGenreById(genreId);
    }
}
