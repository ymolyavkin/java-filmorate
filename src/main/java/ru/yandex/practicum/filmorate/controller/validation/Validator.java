package ru.yandex.practicum.filmorate.controller.validation;


import java.time.LocalDate;

public class Validator {
    public static boolean validationFailedIsEmptyAndContainsSpaces(String s) {
        return (s == null || s.isBlank() || s.contains(" "));
    }

    public static boolean validationFailedIsEmpty(String s) {
        return (s == null || s.isBlank());
    }

    public static boolean validationFailedEmail(String s) {
        return (s == null || s.isBlank() || !s.contains("@"));
    }

    public static boolean validationFailedBirthdayIsAfterNow(LocalDate date) {
        return (date.isAfter(LocalDate.now()));
    }

    public static boolean validationLengthStringOverLimit(String s, int limit) {
        return (s.length() > limit);
    }

    public static boolean validationDateIsBeforeFirstFilm(LocalDate date) {
        return (date.isBefore(LocalDate.of(1895, 12, 28)));
    }
}
