package ru.yandex.practicum.filmorate.exception;

public class InvalidFilmNameException extends RuntimeException {
    public InvalidFilmNameException(String s) {
        super(s);
    }
}
