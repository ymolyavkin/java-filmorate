package ru.yandex.practicum.filmorate.controller.validation;

public class Validator {
    public static boolean validationIsEmptyAndContainsSpaces (String s) {
        return (s == null || s.isBlank() || s.contains(" "));
    }
    public static boolean validationIsEmpty (String s) {
        return (s == null || s.isBlank());
    }
}
