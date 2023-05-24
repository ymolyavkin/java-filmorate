package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAllGenres() {
        String sqlQuery = "select id, name from `genre`";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int genreId = resultSet.getInt("id");
        String name = resultSet.getString("name");

        Genre genre = new Genre(genreId, name);
        return genre;
    }

    @Override
    public Genre findGenreById(int genreId) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select name from `genre` where id = ?", genreId);
        if (genreRows.next()) {
            String name = genreRows.getString("name");
            Genre genre = new Genre(genreId, name);
            return genre;
        } else {
            throw new NotFoundException("Жанр с id " + genreId + " не найден.");
        }
    }
}
