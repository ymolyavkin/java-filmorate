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
import ru.yandex.practicum.filmorate.model.Mpa;
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
        int mpaId = stringToInt(resultSet.getString("mpa_id"));
        LocalDate release = LocalDate.parse(resultSet.getString("release"), formatter);

        String rating = findNameMpaById(mpaId);
        Mpa mpa = new Mpa(mpaId, rating);

        List<Genre> filmGenres = new ArrayList<>();
        Set<Integer> genresIds = findFilmsGenreIdsFromDb(filmId);
        for (Integer genreId : genresIds) {
            filmGenres.add(findGenreById(genreId));
        }

        Film film = new Film(name, description, release, duration, mpa, filmGenres);

        film.setId(filmId);
        return film;
    }

    private String findNameMpaById(int mpaId) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select name from `mpa` where id = ?", mpaId);
        if (genreRows.next()) {
            String name = genreRows.getString("name");

            return name;
        } else {
            throw new NotFoundException("mpa с id " + mpaId + " не найден.");
        }
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into `film`(name, description, release, duration, mpa_id) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate().format(formatter));
            stmt.setString(4, Integer.toString(film.getDuration()));
            stmt.setString(5, Integer.toString(film.getMpaId()));
            return stmt;
        }, keyHolder);

        if (film.getGenres() != null) {
            Set genreIds = getGenreIdsFromFilm(film);
            addGenresToFilm(film, genreIds);
        }
        if (!film.getLikes().isEmpty()) {
            addUserLikeFilm(film);
        }
        String sqlQueryFilm = "select * from `film` where id = ?";
        List<Film> result = jdbcTemplate.query(sqlQueryFilm, this::mapRowToFilm, film.getId());

        return result.get(0);
        //return keyHolder.getKey().intValue();
    }

    private Set<Integer> getGenreIdsFromFilm(Film film) {
        Set<Integer> genreIds = new HashSet<>();
        List<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            genreIds.add(genre.getId());
        }
        return genreIds;
    }

    private void addGenresToFilm(Film film, Set<Integer> genreIds) {
        int filmId = film.getId();

        for (int genreId : genreIds) {
            String sqlQuery = "insert into `film_genre`(film_id, genre_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, genreId);
            log.info("Фильму с id {} добавлен id жанра {}.", filmId, genreId);
        }
    }

    private void addLikesToFilm(Film film, Set<Integer> userIds) {
        int filmId = film.getId();

        for (int userId : userIds) {
            String sqlQuery = "insert into `user_likefilm`(user_id, film_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, filmId);
            log.info("Пользователь с id {} поставил лайк фильму с id {}.", userId, filmId);
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

    private Set<Integer> findFilmsGenreIdsFromDb(int filmId) {
        if (filmExists(filmId)) {
            String sqlQuery = "select genre_id from `film_genre` where film_id = ?";
            List<Integer> listGenres = jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);

            return new HashSet<Integer>(listGenres);
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }


    @Override
    public Film updateFilm(Film film) {
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
                updateGenres(film);
            }
            if (!film.getLikes().isEmpty()) {
                updateLikes(film);
            }

            /*if (!film.getLikes().isEmpty()) {
                deleteUserLikeFilm(film);
                addUserLikeFilm(film);
            }*/
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        String sqlQueryFilm = "select * from `film` where id = ?";
        List<Film> result = jdbcTemplate.query(sqlQueryFilm, this::mapRowToFilm, filmId);

        return result.get(0);
        //return film;
    }

    private void updateGenres(Film film) {
        Set<Integer> newSetGenreIds = getGenreIdsFromFilm(film);
        Set<Integer> oldSetGenreIds = findFilmsGenreIdsFromDb(film.getId());

        Set<Integer> saveNewSetGenreIds = newSetGenreIds;
        Set<Integer> saveOldSetGenreIds = oldSetGenreIds;

        oldSetGenreIds.removeAll(newSetGenreIds);
        saveNewSetGenreIds.removeAll(saveOldSetGenreIds);

        if (!oldSetGenreIds.isEmpty()) {
            deleteGenresFromFilm(film.getId(), oldSetGenreIds);
        }
        if (!saveNewSetGenreIds.isEmpty()) {
            addGenresToFilm(film, saveNewSetGenreIds);
        }
    }

    private void updateLikes(Film film) {
        Set<Integer> newSetLikeIds = film.getLikes();
        Set<Integer> oldSetLikeIds = findFilmsLikeIdsFromDb(film.getId());

        Set<Integer> saveNewSetLikeIds = newSetLikeIds;
        Set<Integer> saveOldSetLikeIds = oldSetLikeIds;

        oldSetLikeIds.removeAll(newSetLikeIds);
        saveNewSetLikeIds.removeAll(saveOldSetLikeIds);

        if (!oldSetLikeIds.isEmpty()) {
            deleteLikesFromFilm(film.getId(), oldSetLikeIds);
        }
        if (!saveNewSetLikeIds.isEmpty()) {
            addLikesToFilm(film, saveNewSetLikeIds);
        }
    }

    private Set<Integer> findFilmsLikeIdsFromDb(int filmId) {
        if (filmExists(filmId)) {
            String sqlQuery = "select user_id from `user_likefilm` where film_id = ?";
            List<Integer> listLikes = jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);

            return new HashSet<Integer>(listLikes);
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    private void deleteGenresFromFilm(int filmId, Set<Integer> genreIds) {
        for (int genreId : genreIds) {
            String sqlQuery = "delete from `film_genre` where film_id = ? and genre_id = ?";
            if (jdbcTemplate.update(sqlQuery, filmId, genreId) > 0) {
                log.info("Удалён жанр {} у фильма {}", genreId, filmId);
            }
        }
    }

    private void deleteLikesFromFilm(int filmId, Set<Integer> userIds) {
        for (int userId : userIds) {
            String sqlQuery = "delete from `user_likefilm` where film_id = ? and user_id = ?";
            if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
                log.info("Удалён лайк пользователя {} у фильма {}", userId, filmId);
            }
        }
    }

    @Override
    public Film findFilmById(int filmId) {
        String sqlQuery = "select * from `film` where id = ?";
        List<Film> result = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, filmId);
        if (result.size() < 1) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        return result.get(0);
    }

    // @Override
    public Film findFilmByIdOld(int filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select id, name, description, release,  duration, mpa_id from `film` where id = ?", filmId);
        if (filmRows.next()) {
            String name = filmRows.getString("name");
            String description = filmRows.getString("description");
            LocalDate release = LocalDate.parse(filmRows.getString("release"), formatter);
            int duration = filmRows.getInt("duration");
            int mpaId = filmRows.getInt("mpa_id");
            String rating = findNameMpaById(mpaId);
            Mpa mpa = new Mpa(mpaId, rating);
            //Map.Entry<String, Integer> mpa_entry = new AbstractMap.SimpleEntry<String, Integer>("id", mpa_id);
            //List<Map.Entry<String, Integer>> genres = new ArrayList<>();
            //List<Genre> filmGenres = new ArrayList<>();
            List<Genre> filmGenres = findAllGenresFilm(filmId);

           /* SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select genre_id from `film_genre` where film_id = ?", filmId);
            if (genreRows.next()) {
                int idGenre = genreRows.getInt("genre_id");
                filmGenres.add(findGenreById(idGenre));
                //Map.Entry<String, Integer> genre = new AbstractMap.SimpleEntry<String, Integer>("id", idGenre);
                //genres.add(genre);
            }*/

            Film film = new Film(name, description, release, duration, mpa, filmGenres);
            film.setId(filmId);

            return film;
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    private List<Genre> findAllGenresFilm(int filmId) {
        String sqlQuery = "select * from `film_genre` where film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    @Override
    public int deleteFilmById(int filmId) {
        deleteGenresFromFilm(filmId, findFilmsGenreIdsFromDb(filmId));
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
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int genreId = stringToInt(resultSet.getString("id"));
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
        int mpaId = stringToInt(resultSet.getString("id"));
        String name = resultSet.getString("name");

        Mpa mpa = new Mpa(mpaId, name);
        return mpa;
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

    /*private void deleteFilmGenre(Film film) {
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
    }*/

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
