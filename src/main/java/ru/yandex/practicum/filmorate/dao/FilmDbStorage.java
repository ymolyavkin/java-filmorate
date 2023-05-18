package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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

        List<Map.Entry<String, Integer>> genres = new ArrayList<>();
        Set<Integer> genresIds = findFilmsGenres(filmId);
        for (Integer genreId : genresIds) {
            Map.Entry<String, Integer> filmGenre = new AbstractMap.SimpleEntry<String, Integer>("id", genreId);
            genres.add(filmGenre);
        }

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
        List<Map.Entry<String, Integer>> idGenres = film.getGenres();
        for (Map.Entry<String, Integer> entryGenre : idGenres) {
            String sqlQuery = "insert into `film_genre`(film_id, genre_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, entryGenre.getValue());
            log.info("Фильму с id {} добавлен id жанра {}.", filmId, entryGenre.getValue());
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

    /*
    public void addFriend(int userId, int friendId) {
            if (userExists(userId) && userExists(friendId)) {
                String sqlQuery = "insert into `friendship`(one_friend_id, other_friend_id) values (?, ?)";

                jdbcTemplate.update(sqlQuery, userId, friendId);
                log.info("Пользователь с идентификатором {} добавлен в друзья пользователю {}.", friendId, userId);
            } else {
                throw new NotFoundException("Пользователь с id " + userId + " и/или с id " + friendId + " не найден.");
            }
        }
        --------------------------------------
    private Set<Integer> findUsersFriends(int userId) {
            if (userExists(userId)) {
                String sqlQuery = "select other_friend_id from `friendship` where one_friend_id = ?";
                List<Integer> listFriends = jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);

                return new HashSet<Integer>(listFriends);
            } else {
                throw new NotFoundException("Пользователь с id " + userId + " не найден.");
            }
        }
     */
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
        //String filmId = Integer.toString(film.getId());
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


    /*private void updateUserLikeFilm(Film film) {
        int filmId = film.getId();
        SqlRowSet likeRows = jdbcTemplate.queryForRowSet("select * from `user_likefilm` where film_id = ?", filmId);
        if (likeRows.next()) {
            String sqlQuery = "update `user_likefilm` set user_id = ? where film_id = ?";

            jdbcTemplate.update(sqlQuery,
                    film.getLikes()
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpaId(),
                    film.getId());

            updateFilmGenre(film);
            updateUserLikeFilm(film);
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }*/

    /*private void updateFilmGenre(Film film) {
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

            updateFilmGenre(film);
            updateUserLikeFilm(film);
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }*/


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
            List<Map.Entry<String, Integer>> genres = new ArrayList<>();

            SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select genre_id from `film_genre` where film_id = ?", filmId);
            if (genreRows.next()) {
                int idGenre = genreRows.getInt("genre_id");
                Map.Entry<String, Integer> genre = new AbstractMap.SimpleEntry<String, Integer>("id", idGenre);
                genres.add(genre);
            }

            Film film = new Film(name, description, release, duration, mpa_entry, genres);
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
    public List<String> findAllGenres() {
        return null;
    }

    @Override
    public String findGenreById(Integer genreId) {
        return null;
    }

    @Override
    public String createGenre(String genre) {
        return null;
    }

    @Override
    public String findMpaById(Integer mpaId) {
        return null;
    }

    @Override
    public List<String> findAllMpa() {
        return null;
    }

    /*
    String sqlQuery = "delete from canned_message where msg_no in (:msgNos)";
    List<Integer> params = <array list of number>;
    Map namedParameters = Collections.singletonMap("msgNos", params);
    int rows = smsdbJdbcTemplate.update(sqlQuery, namedParameters);
     */
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

        List<Map.Entry<String, Integer>> mapsGenres = film.getGenres();
        for (Map.Entry<String, Integer> mapsGenre : mapsGenres) {
            setGenres.add(mapsGenre.getValue());
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
