package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class User {
    private int id = 0;
    private final String name;
    private final String email;
    private final String login;
    private final LocalDate birthday;

    public User(String name, String email, String login, LocalDate birthday) {
        id = ++id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }
}
