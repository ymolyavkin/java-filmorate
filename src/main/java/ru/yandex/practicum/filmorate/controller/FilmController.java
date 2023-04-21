package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Validator;
import ru.yandex.practicum.filmorate.exception.InvalidFilmNameException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    //private final Map<Integer, Film> films = new HashMap<>();
    private final FilmService filmService;

    //private int id = 0;
    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Map<Integer, Film> findAll() {
        return filmService.findAll();
    }

    /*
     @GetMapping
        public Map<Integer, User> findAll() {

            return userService.findAll();
        }
     */
    //  public User create(@Valid @RequestBody User user) {
    //        return userService.create(user);
    //    }
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
        /*if (Validator.validationFailedIsEmpty(film.getName())) {
            log.error("Название фильма не может быть пустым.");
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Длина описания превышает допустимый предел.");
            throw new ValidationException("Длина описания превышает допустимый предел.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Некорректная дата релиза.");
            throw new ValidationException("Некорректная дата релиза.");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }*/
        /*if (films.containsKey(film.getId())) {
            log.error("Фильм с id " + film.getId() + " уже есть в коллекции.");
            throw new UserAlreadyExistException("Фильм с id " + film.getId() + " уже есть в коллекции.");
        }
        films.put(film.getId(), film);*/
        //return film;
    }
    /*
    @PutMapping
    public User put(@Valid @RequestBody User user) {
        userService.put(user);

        return user;
    }
     */

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        filmService.put(film);
        return film;
    }
       /* if (!films.containsKey(film.getId())) {
            log.error("Фильм с id " + film.getId() + " не найден.");
            throw new ValidationException("Фильм с id " + film.getId() + " не найден.");
        }*/
        /*if (Validator.validationFailedIsEmpty(film.getName())) {
            log.error("Название фильма не может быть пустым.");
            throw new InvalidFilmNameException("Название фильма не может быть пустым.");
        }
        if (Validator.validationLengthStringOverLimit(film.getDescription(), 200)) {
            log.error("Длина описания превышает допустимый предел.");
            throw new ValidationException("Длина описания превышает допустимый предел.");
        }
        if (Validator.validationDateIsBeforeFirstFilm(film.getReleaseDate())) {
            log.error("Некорректная дата релиза.");
            throw new ValidationException("Некорректная дата релиза.");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }*/
    // films.put(film.getId(), film);


    /*
    @GetMapping("/posts")
    public List<Post> findAll(
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(defaultValue = DESCENDING_ORDER, required = false) String sort
    )
     */
    @GetMapping("popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.findPopularFilms(count);
    }
    @PutMapping("{filmId}/like/{userId}")
    public Integer likeFilm(@PathVariable("filmId") Integer filmId,
                             @PathVariable("userId") Integer userId) {

        Film film = filmService.findFilmById(filmId);
        film.addLike(userId);
        filmService.put(film);

        return userId;
    }
    @DeleteMapping("{filmId}/like/{userId}")
    public Integer deleteLikeFilm(@PathVariable("filmId") Integer filmId,
                            @PathVariable("userId") Integer userId) {

        Film film = filmService.findFilmById(filmId);
        film.deleteLike(userId);
        filmService.put(film);

        return userId;
    }
}
