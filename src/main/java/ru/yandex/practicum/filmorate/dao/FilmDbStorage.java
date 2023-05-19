package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.Constants.formatter;
import static ru.yandex.practicum.filmorate.dao.UserDbStorage.stringToInt;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select id, name, description, release,  duration, mpa_id from `film`";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int filmId = stringToInt(resultSet.getString("id"));
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        int duration = stringToInt(resultSet.getString("duration"));
        int mpa_id = stringToInt(resultSet.getString("mpa_id"));
        LocalDate release = LocalDate.parse(resultSet.getString("release"), formatter);
        Map.Entry<String, Integer> mpa_entry = new AbstractMap.SimpleEntry<String, Integer>("id", mpa_id);

       // List<Map.Entry<String, Integer>> genres = new ArrayList<>();
        List<Genre> filmGenres = new ArrayList<>();
        Set<Integer> genresIds = findFilmsGenres(filmId);
        for (Integer genreId : genresIds) {
            filmGenres.add(findGenreById(genreId));
            //Map.Entry<String, Integer> filmGenre = new AbstractMap.SimpleEntry<String, Integer>("id", genreId);
            //genres.add(filmGenre);
        }

        Film film = new Film(name, description, release, duration, mpa_entry, filmGenres);

        film.setId(filmId);
        return film;
    }

    @Override
    public int addFilm(Film film) {
        String sqlQuery = "insert into `film`(name, description, release, duration, mpa_id) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate().format(formatter));
            stmt.setString(4, Integer.toString(film.getDuration()));
            stmt.setString(5, Integer.toString(film.getMpa().getValue()));
            return stmt;
        }, keyHolder);

        if (film.getGenres() != null) {
            addFilmGenre(film);
        }
        if (!film.getLikes().isEmpty()) {
            addUserLikeFilm(film);
        }

        return keyHolder.getKey().intValue();
    }

    private void addFilmGenre(Film film) {
        int filmId = film.getId();
        //List<Map.Entry<String, Integer>> idGenres = film.getGenres();
        List<Genre> idGenres = film.getGenres();
        for (Genre genre : idGenres) {
            String sqlQuery = "insert into `film_genre`(film_id, genre_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, genre.getId());
            log.info("Фильму с id {} добавлен id жанра {}.", filmId, genre.getId());
        }
    }

    private void addUserLikeFilm(Film film) {
        int filmId = film.getId();
        Set<Integer> likes = film.getLikes();
        for (Integer userIdLike : likes) {
            String sqlQuery = "insert into `user_likefilm`(user_id, film_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, userIdLike, filmId);
            log.info("Фильму с id {} поставил лайк пользователь с id {}.", filmId, userIdLike);
        }
    }

    private Set<Integer> findFilmsGenres(int filmId) {
        if (filmExists(filmId)) {
            String sqlQuery = "select genre_id from `film_genre` where film_id = ?";
            List<Integer> listGenres = jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);

            return new HashSet<Integer>(listGenres);
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    @Override
    public void updateFilm(Film film) {
        int filmId = film.getId();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from `film` where id = ?", filmId);
        if (filmRows.next()) {
            String sqlQuery = "update `film` set name = ?, description = ?, release = ?, duration = ?, mpa_id = ? where id = ?";

            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpaId(),
                    film.getId());

            if (film.getGenres() != null) {
                deleteFilmGenre(film);
                addFilmGenre(film);
            }
            if (!film.getLikes().isEmpty()) {
                deleteUserLikeFilm(film);
                addUserLikeFilm(film);
            }
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    @Override
    public Film findFilmById(int filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from `film` where id = ?", filmId);
        if (filmRows.next()) {
            String name = filmRows.getString("name");
            String description = filmRows.getString("description");
            LocalDate release = LocalDate.parse(filmRows.getString("release"), formatter);
            int duration = filmRows.getInt("duration");
            int mpa_id = filmRows.getInt("mpa_id");
            Map.Entry<String, Integer> mpa_entry = new AbstractMap.SimpleEntry<String, Integer>("id", mpa_id);
            //List<Map.Entry<String, Integer>> genres = new ArrayList<>();
            List<Genre> filmGenres = new ArrayList<>();

            SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select genre_id from `film_genre` where film_id = ?", filmId);
            if (genreRows.next()) {
                int idGenre = genreRows.getInt("genre_id");
                filmGenres.add(findGenreById(idGenre));
                //Map.Entry<String, Integer> genre = new AbstractMap.SimpleEntry<String, Integer>("id", idGenre);
                //genres.add(genre);

            }


            Film film = new Film(name, description, release, duration, mpa_entry, filmGenres);
            film.setId(filmId);

            return film;
        } else {
            throw new NotFoundException("Пользователь с id " + filmId + " не найден.");
        }
    }

    @Override
    public int deleteFilmById(int filmId) {
        String sqlQuery = "delete from `film` where id = ?";

        if (jdbcTemplate.update(sqlQuery, filmId) > 0) {
            log.info("Удалён фильм: {}", filmId);
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        return filmId;
    }

    @Override
    public List<Genre> findAllGenres() {
        String sqlQuery = "select id, name from `genre`";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
        /*String sqlQuery = "select name from `genre`";
        List<String> listGenres = jdbcTemplate.queryForList(sqlQuery, String.class);

        return listGenres;*/
    }
    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int genreId = stringToInt(resultSet.getString("id"));
        String name = resultSet.getString("name");

        Genre genre = new Genre(genreId, name);
        return genre;
    }

    @Override
    public Genre findGenreById(Integer genreId) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select name from `genre` where id = ?", genreId);
        if (genreRows.next()) {
            String name = genreRows.getString("name");
            Genre genre = new Genre(genreId, name);
            return genre;
        } else {
            throw new NotFoundException("Жанр с id " + genreId + " не найден.");
        }
    }


    @Override
    public String findMpaById(Integer mpaId) {
        return null;
    }

    @Override
    public List<String> findAllMpa() {
        return null;
    }

    private void deleteUserLikeFilm(Film film) {
        int filmId = film.getId();
        Set<Integer> likes = film.getLikes();
        for (Integer userId : likes) {
            String sqlQuery = "delete from `user_likefilm` where film_id = ? and user_id = ?";
            if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
                log.info("Удалён лайк пользователя {} у фильма {}", userId, filmId);
            }
        }

    }

    private void deleteFilmGenre(Film film) {
        int filmId = film.getId();
        Set<Integer> setGenres = new HashSet<>();

        //List<Map.Entry<String, Integer>> mapsGenres = film.getGenres();
        List<Genre> mapsGenres = film.getGenres();
        for (Genre genre : mapsGenres) {
            setGenres.add(genre.getId());
        }
        for (Integer genreId : setGenres) {
            String sqlQuery = "delete from `film_genre` where film_id = ? and genre_id = ?";
            if (jdbcTemplate.update(sqlQuery, filmId, genreId) > 0) {
                log.info("Удалён жанр {} у фильма {}", genreId, filmId);
            }
        }
    }

    private boolean filmExists(int id) {
        String sqlQuery = "select count(*) from `film` where id = ?";
        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);

        return result == 1;
    }

    private void selectAllGenres() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genre");
        if (genreRows.next()) {
            log.info("Найден жанр: {} {}", genreRows.getString("id"), genreRows.getString("name"));
        } else {
            log.info("Жанр не найден");

        }
    }

}
