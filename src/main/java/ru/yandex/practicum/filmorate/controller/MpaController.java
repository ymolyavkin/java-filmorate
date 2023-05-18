package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;

    @GetMapping
    public List<String> findAllMpa() {
        return filmService.findAllMpa();
    }

    @GetMapping("{mpaId}")
    public String findmpaById(@PathVariable("mpaId") Integer mpaId) {
        return filmService.findMpaById(mpaId);
    }
}
