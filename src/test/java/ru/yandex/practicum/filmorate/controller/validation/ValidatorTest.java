package ru.yandex.practicum.filmorate.controller.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validation.Validator;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void shouldTrueIfIsEmptyAndContainsSpaces() {
        String testBlank = "";
        String testNull = null;
        String testString = "test string";

        assertTrue(Validator.validationFailedIsEmptyAndContainsSpaces(testBlank));
        assertTrue(Validator.validationFailedIsEmptyAndContainsSpaces(testNull));
        assertTrue(Validator.validationFailedIsEmptyAndContainsSpaces(testString));
    }

    @Test
    void shouldTrueIfIsEmpty() {
        String testBlank = "";
        String testNull = null;

        assertTrue(Validator.validationFailedIsEmptyAndContainsSpaces(testBlank));
        assertTrue(Validator.validationFailedIsEmptyAndContainsSpaces(testNull));
    }

    @Test
    void shouldTrueIfEmailIncorrect() {
        String testBlank = "";
        String testNull = null;
        String withoutAt = "email.mail.com";

        assertTrue(Validator.validationFailedEmail(testBlank));
        assertTrue(Validator.validationFailedEmail(testNull));
        assertTrue(Validator.validationFailedEmail(withoutAt));
    }

    @Test
    void shouldTrueIfBirthdayIsAfterNow() {
        LocalDate date = LocalDate.of(2028, 8, 8);

        assertTrue(Validator.validationFailedBirthdayIsAfterNow(date));
    }

    @Test
    void shouldTrueIfLengthStringOverLimit() {
        int limit = 200;
        //String s = IntStream.generate(() -> 1).limit(201).mapToObj(x -> ch).collect(Collectors.joining());
        String s = Stream.generate(() -> "s").limit(limit + 1).collect(Collectors.joining());

        assertEquals(s.length(), 201);
        assertTrue(s.length() > 200);
        assertTrue(Validator.validationLengthStringOverLimit(s, 200));
    }

    @Test
    void shouldTrueIfDateIsBeforeFirstFilm() {
        LocalDate dateFirstFilm = LocalDate.of(1895, 12, 28);
        LocalDate date = LocalDate.of(1800, 12, 28);

        assertTrue(date.isBefore(dateFirstFilm));
    }
}