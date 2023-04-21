package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.Release;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Film implements Comparable<Film> {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private final String name;
    @Length(max = 200, message = "Длина описания превышает допустимый предел.")
    private final String description;
    @Release
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private final int duration;
    private Set<Integer> likes;


    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
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

    public boolean addLike(Integer userId) {
        return likes.add(userId);
    }

    public boolean deleteLike(Integer userId) {
        return likes.remove(userId);
    }

    public int numberOfLikes() {
        return likes.size();
    }

    @Override
    public int compareTo(Film o) {
        return Integer.compare(this.numberOfLikes(), o.numberOfLikes());
    }
}
