package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data
public class User {
    private final int id;
    private final String name;
    private final String email;
    private final String login;
    private final LocalDate birthday;

    public User(int id, String name, String email, String login, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }
}
