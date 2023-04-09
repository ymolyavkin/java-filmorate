package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Film {
    private int id = 0;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;


    public Film(String name, String description, LocalDate releaseDate, int duration) {
        id = ++id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
