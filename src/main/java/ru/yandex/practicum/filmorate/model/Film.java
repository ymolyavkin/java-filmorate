package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.Release;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Film implements Comparable<Film> {
    private long id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private final String name;
    @Length(max = 200, message = "Длина описания превышает допустимый предел.")
    private final String description;
    @Release
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private final int duration;
    private Set<Long> likes;
    private final Mpa mpa;
    private final List<Genre> genres;

    public Film(String name, String description, LocalDate releaseDate, int duration,
                Mpa mpa, List<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
        likes = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id && Objects.equals(name, film.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @JsonIgnore
    public int getMpaId() {
        return mpa.getId();
    }

    public boolean addLike(long userId) {
        return likes.add(userId);
    }

    public boolean deleteLike(long userId) {
        return likes.remove(userId);
    }

    public long numberOfLikes() {
        return likes.size();
    }

    @Override
    public int compareTo(Film o) {
        return Long.compare(this.numberOfLikes(), o.numberOfLikes());
    }
}
