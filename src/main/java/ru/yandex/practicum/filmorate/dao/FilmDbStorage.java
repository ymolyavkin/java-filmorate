package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Map.Entry<String, Integer> genre = new AbstractMap.SimpleEntry<String, Integer>("id", 1);
        List<Map.Entry<String, Integer>> genres = new ArrayList<>();
        genres.add(genre);

        Film film = new Film(name, description, release, duration, mpa_entry, genres);

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

        return keyHolder.getKey().intValue();
    }
    /*
     @Override
    public int addUser(User user) {
        String sqlQuery = "insert into `user`(email, login, name, birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getBirthday().format(formatter));
            return stmt;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }
     */

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public Film findFilmById(int filmId) {
        return null;
    }

    @Override
    public int deleteFilmById(int filmId) {
        return 0;
    }

    private boolean filmExists(int id) {
        String sqlQuery = "select count(*) from `user` where id = ?";
        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);

        return result == 1;
    }

    private void selectAllGenres() {
        // выполняем запрос к базе данных.
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genre");
        if (genreRows.next()) {
            log.info("Найден жанр: {} {}", genreRows.getString("id"), genreRows.getString("name"));
        } else {
            log.info("Жанр не найден");

        }
    }

}
