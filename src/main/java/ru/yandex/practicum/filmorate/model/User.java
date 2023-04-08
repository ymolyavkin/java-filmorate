package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Data
//@Builder
/*public class User {

    private final int id;
    private String name;
    private final String email;
    private final String login;
    private final LocalDate birthday;
    public static class Builder {
        // Обязательные параметры

        private final String email;
        private final String login;


        // Необязательные параметры с значениями по умолчанию

        private int id = 0;
        private String name = "";
        private LocalDate birthday=LocalDate.now();

        public Builder(String email, String login) {
            this.email = email;
            this.login = login;
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }
        public Builder birthday(LocalDate val) {
            birthday = val;
            return this;
        }


        public User build() {
            return new User(this);
        }
    }


    private User(Builder builder) {
        id = builder.id;
        name = builder.name;
        email = builder.email;
        login = builder.login;
        birthday = builder.birthday;
    }
}*/

public class User {
    private int i = 0;
    private final int id;
    private String name;
    private final String email;
    private final String login;
    private final LocalDate birthday;

    public User(String name, String email, String login, LocalDate birthday) {
        id = ++i;
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public void setName(String name) {
        this.name = name;
    }
}

