package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa findMpaById(int mpaId) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select name from `mpa` where id = ?", mpaId);
        if (mpaRows.next()) {
            String name = mpaRows.getString("name");
            Mpa mpa = new Mpa(mpaId, name);
            return mpa;
        } else {
            throw new NotFoundException("Жанр с id " + mpaId + " не найден.");
        }
    }

    @Override
    public List<Mpa> findAllMpa() {
        String sqlQuery = "select id, name from `mpa`";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        int mpaId = resultSet.getInt("id");
        String name = resultSet.getString("name");

        Mpa mpa = new Mpa(mpaId, name);
        return mpa;
    }
}
