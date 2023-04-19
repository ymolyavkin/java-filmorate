package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class User {
    private int id;

    private String name;
    @Pattern(regexp = "^.+@.+\\..+$", message = "Некорректный адрес электронной почты")
    private final String email;
    @NotBlank
    @Pattern(regexp = "^^\\S+$", message = "Логин пользователя пуст или содержит пробелы.")
    private final String login;
    private final LocalDate birthday;
    private Set<Long> friends;

    public User(String name, String email, String login, LocalDate birthday) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        friends = new HashSet<>();
    }

    public boolean addFriend(Long friendId) {
        return friends.add(friendId);
    }
    public boolean deleteFriend(Long friendId) {
        return friends.remove(friendId);
    }
}

