package ru.yandex.practicum.filmorate.model;

import java.time.Duration;
import java.time.LocalDate;


import lombok.Data;
import org.springframework.cglib.core.Local;


@Data
public class Film {
    private int i = 0;
    private final int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    //private final Duration duration;
    private final int duration;

   // public Film(String name, String description, LocalDate releaseDate, Duration duration) {
    public Film(String name, String description, LocalDate releaseDate, int duration) {
        id = ++i;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
