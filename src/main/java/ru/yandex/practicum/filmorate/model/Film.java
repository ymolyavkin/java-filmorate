package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.Release;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private final String name;
    @Length(max=200, message = "Длина описания превышает допустимый предел.")
    private final String description;
    @Release
    private final LocalDate releaseDate;
    @Positive(message= "Продолжительность фильма должна быть положительной.")
    private final int duration;
    private Set<Long> likes;


    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        likes = new HashSet<>();
    }

    public boolean addLike(Long userId) {
        return likes.add(userId);
    }

    public boolean deleteLike(Long userId) {
        return likes.remove(userId);
    }

    public int numberOfLikes() {
        return likes.size();
    }
}
