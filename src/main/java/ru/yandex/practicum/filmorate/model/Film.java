package ru.yandex.practicum.filmorate.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;

import lombok.Data;
import org.springframework.cglib.core.Local;


@Data
public class Film {
    private final int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Duration duration;

    public Film(int id, String name, String description, LocalDate releaseDate, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
