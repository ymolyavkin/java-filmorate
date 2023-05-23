package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {
    @Autowired
    private final UserDbStorage userStorage;
    @Autowired
    private final FilmDbStorage filmStorage;
    private static Film film;
    private static User userOne;
    private static User userTwo;

    private Film createTestFilm() {
        Mpa ratingMpa = new Mpa(1, "G");
        Genre genreOne = new Genre(1, "Комедия");
        Genre genreTwo = new Genre(2, "Драма");


        var genres = Arrays.asList(genreOne, genreTwo);
        Film film = new Film("nisi eiusmod", "adipisicing",
                LocalDate.of(1967, 3, 25), 100, ratingMpa, genres);
        film.setId(1);

        return film;
    }

    private Film createUpdatedFilm() {
        Mpa ratingMpa = new Mpa(2, "PG");
        Genre genre = new Genre(1, "Комедия");


        List<Genre> genres = Arrays.asList(genre);
        Film updatedFilm = new Film("updated nisi eiusmod", "updated adipisicing",
                LocalDate.of(1987, 5, 15), 150, ratingMpa, genres);

        updatedFilm.setId(1);

        return updatedFilm;
    }

    private void createUsers() {
        userOne = new User("Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        long userOneId = userStorage.addUser(userOne);

        userTwo = new User("Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        long userTwoId = userStorage.addUser(userTwo);
    }


    @BeforeEach
    void setUp() {
        film = createTestFilm();
        filmStorage.addFilm(film);
        createUsers();
    }

    @Test
    void findAll() {
        List<Film> allFilms = filmStorage.findAll();

        Film actual = allFilms.get(0);
        Film expected = createTestFilm();

        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
        assertEquals(actual.getId(), 1);
    }

    @Test
    void addFilm() {
        Film newFilm = filmStorage.addFilm(film);

        assertThat(newFilm).usingRecursiveComparison().ignoringFields("id").isEqualTo(film);
        assertEquals(newFilm.getId(), 1);
    }

    @Test
    void updateFilm() {
        Film updatedFilm = createUpdatedFilm();
        Film newFilm = filmStorage.updateFilm(updatedFilm);

        assertThat(newFilm).usingRecursiveComparison().ignoringFields("id").isEqualTo(updatedFilm);
        assertEquals(1, newFilm.getId());
    }

    @Test
    void findFilmById() {
        Film foundFilm = filmStorage.findFilmById(1);
        Film expected = createTestFilm();

        assertThat(foundFilm).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
        assertEquals(foundFilm.getId(), 1);
    }

    @Test
    public void testFindFilmById() {
        Film actualFilm = filmStorage.findFilmById(1);
        long filmId = actualFilm.getId();

        assertThat(actualFilm).isNotNull();
        assertEquals(filmId, 1);
    }

    @Test
    void deleteFilmById() {
        List<Film> allFilms = filmStorage.findAll();
        assertEquals(1, allFilms.size());

        filmStorage.deleteFilmById(1);

        allFilms = filmStorage.findAll();
        assertEquals(0, allFilms.size());
    }

    @Test
    void findAllGenres() {
        List<Genre> allGenres = filmStorage.findAllGenres();

        assertEquals(6, allGenres.size());
        assertEquals("Комедия", allGenres.get(0).getName());
    }

    @Test
    void findGenreById() {
        Genre genre1 = filmStorage.findGenreById(1);
        Genre genre2 = filmStorage.findGenreById(2);
        Genre genre3 = filmStorage.findGenreById(3);
        Genre genre4 = filmStorage.findGenreById(4);
        Genre genre5 = filmStorage.findGenreById(5);
        Genre genre6 = filmStorage.findGenreById(6);

        assertEquals("Комедия", genre1.getName());
        assertEquals("Драма", genre2.getName());
        assertEquals("Мультфильм", genre3.getName());
        assertEquals("Триллер", genre4.getName());
        assertEquals("Документальный", genre5.getName());
        assertEquals("Боевик", genre6.getName());
    }

    @Test
    void findMpaById() {
        Mpa mpa1 = filmStorage.findMpaById(1);
        Mpa mpa2 = filmStorage.findMpaById(2);
        Mpa mpa3 = filmStorage.findMpaById(3);
        Mpa mpa4 = filmStorage.findMpaById(4);
        Mpa mpa5 = filmStorage.findMpaById(5);

        assertEquals("G", mpa1.getName());
        assertEquals("PG", mpa2.getName());
        assertEquals("PG-13", mpa3.getName());
        assertEquals("R", mpa4.getName());
        assertEquals("NC-17", mpa5.getName());
    }

    @Test
    void findAllMpa() {
        List<Mpa> allMpa = filmStorage.findAllMpa();

        assertEquals(5, allMpa.size());
        assertEquals("G", allMpa.get(0).getName());
    }

    @Test
    void findPopularFilms() {
        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        likes.add(2L);
        film.setLikes(likes);
        filmStorage.updateFilm(film);

        Film otherFilm = createUpdatedFilm();
        filmStorage.addFilm(otherFilm);

        List<Film> popularFilms = filmStorage.findPopularFilms(2);

        assertEquals(2, popularFilms.size());
        assertThat(film).usingRecursiveComparison().ignoringFields("id").isEqualTo(popularFilms.get(0));
    }

    @Test
    void findAllUsers() {
        List<User> allUsers = userStorage.findAll();
        assertEquals(2, allUsers.size());
    }

    @Test
    void addUser() {
        long userId = userStorage.addUser(userOne);

        assertEquals(3, userId);
    }

    @Test
    void updateUser() {
        User updatedUser = createUpdatedUser();
        userStorage.updateUser(updatedUser);

        User actual = userStorage.findUserById(1);
        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(updatedUser);
    }

    private User createUpdatedUser() {
        User updatedUser = new User("Updated Nick Name", "newmail@mail.ru", "upddolore", LocalDate.of(1956, 8, 20));
        updatedUser.setId(1);

        return updatedUser;
    }

    @Test
    void findUserById() {
        User actualUser = userStorage.findUserById(1);

        assertThat(actualUser).isNotNull();
        assertThat(actualUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(userOne);
        assertEquals(actualUser.getId(), 1);
    }

    @Test
    void deleteUserById() {
        List<User> allUsers = userStorage.findAll();
        assertEquals(2, allUsers.size());

        userStorage.deleteUserById(1);

        allUsers = userStorage.findAll();
        assertEquals(1, allUsers.size());
    }

    @Test
    void deleteFriendFromUser() {
        userStorage.addFriend(1, 2);

        User actual = userStorage.findUserById(1);
        Set friends = actual.getFriends();

        assertEquals(1, friends.size());

        userStorage.deleteFriendFromUser(1, 2);
        actual = userStorage.findUserById(1);

        friends = actual.getFriends();
        assertEquals(0, friends.size());
    }

    @Test
    void addFriend() {
        User actual = userStorage.findUserById(1);
        Set friends = actual.getFriends();

        assertEquals(0, friends.size());

        userStorage.addFriend(1, 2);
        actual = userStorage.findUserById(1);

        friends = actual.getFriends();
        assertEquals(1, friends.size());
    }
}
