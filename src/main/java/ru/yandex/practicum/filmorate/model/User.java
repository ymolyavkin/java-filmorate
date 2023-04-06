package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Date;
@Data
public class User {
    private final int id;
    private final String name;
    private final String email;
    private final String description;
    private final Date birthday;

    public User(int id, String name, String email, String description, Date birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.birthday = birthday;
    }
}
