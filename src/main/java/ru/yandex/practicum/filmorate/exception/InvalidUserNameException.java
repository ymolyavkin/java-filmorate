package ru.yandex.practicum.filmorate.exception;

public class InvalidUserNameException extends RuntimeException {
    public InvalidUserNameException(String s) {
        super(s);
    }
}
