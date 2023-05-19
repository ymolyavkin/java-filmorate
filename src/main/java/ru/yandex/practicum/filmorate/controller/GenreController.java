package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    @GetMapping
    public List<String> findAllGenres() {
        return filmService.findAllGenres();
    }

    @GetMapping("{genreId}")
    public Map.Entry<Integer, String> findGenreById(@PathVariable("genreId") Integer genreId) {
        return filmService.findGenreById(genreId);
    }

    @PostMapping
    public String createGenre(@RequestBody String genre) {
        return filmService.createGenre(genre);
    }
}
