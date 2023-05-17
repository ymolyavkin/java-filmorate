package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.Release;

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
    //private final Map<String, Integer> mpa;
    //private final List<Map<String, Integer>> genres;
    private Set<Integer> likes;
    private final Map.Entry<String, Integer> mpa;
    private final List<Map.Entry<String, Integer>> genres;
/*
Map.Entry<String,Integer> entry =
    new AbstractMap.SimpleEntry<String, Integer>("exmpleString", 42);
 */

    /*public Film(String name, String description, LocalDate releaseDate, int duration, Map<String,
            Integer> mpa, List<Map<String, Integer>> genres) {*/
    public Film(String name, String description, LocalDate releaseDate, int duration,
                Map.Entry<String, Integer> mpa, List<Map.Entry<String, Integer>> genres) {
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

    public boolean addLike(int userId) {
        return likes.add(userId);
    }

    public boolean deleteLike(int userId) {
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
