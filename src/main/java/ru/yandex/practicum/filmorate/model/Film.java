package ru.yandex.practicum.filmorate.model;

import java.time.Duration;
import java.util.Date;

import lombok.Data;


@Data
public class Film {
    private final int id;
    private final String name;
    private final String description;
    private final Date releaseDate;
    private final Duration duration;

    public Film(int id, String name, String description, Date releaseDate, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
