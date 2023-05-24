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
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.formatter;
import static ru.yandex.practicum.filmorate.dao.UserDbStorage.stringToLong;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select film.id, film.name, description, release,  duration, film.mpa_id, mpa.name " +
                "from `film` left join mpa on film.mpa_id = mpa.id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, long rowNum) throws SQLException {
        long filmId = resultSet.getLong("film.id");
        String name = resultSet.getString("film.name");
        String description = resultSet.getString("description");
        int duration = resultSet.getInt("duration");
        int mpaId = resultSet.getInt("film.mpa_id");
        LocalDate release = LocalDate.parse(resultSet.getString("release"), formatter);

        String rating = resultSet.getString("mpa.name");
        Mpa mpa = new Mpa(mpaId, rating);

        Set<Integer> genresIds = findFilmsGenreIdsFromDb(filmId);
        List<Genre> filmGenres = genresIds.stream().map(genreId -> findGenreById(genreId)).collect(Collectors.toList());

        Set<Long> likesFilm = findFilmsLikeIdsFromDb(filmId);

        Film film = new Film(name, description, release, duration, mpa, filmGenres);
        film.setId(filmId);
        film.setLikes(likesFilm);

        return film;
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
            Set<Integer> genreIds = getGenreIdsFromFilm(film);
            addGenresToFilm(film, genreIds);
        }
        if (!film.getLikes().isEmpty()) {
            addUserLikeFilm(film);
        }
        String sqlQueryFilm = "select film.id, film.name, description, release,  duration, film.mpa_id, mpa.name " +
                "from `film` join mpa on film.mpa_id = mpa.id and film.id = ?";
        List<Film> result = jdbcTemplate.query(sqlQueryFilm, this::mapRowToFilm, film.getId());

        return result.get(0);
    }

    private Set<Integer> getGenreIdsFromFilm(Film film) {
        return film.getGenres().stream().map(genre -> genre.getId()).collect(Collectors.toSet());
    }

    private void addGenresToFilm(Film film, Set<Integer> genreIds) {
        long filmId = film.getId();

        for (int genreId : genreIds) {
            String sqlQuery = "insert into `film_genre`(film_id, genre_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, genreId);
            log.info("Фильму с id {} добавлен id жанра {}.", filmId, genreId);
        }
    }

    private void addLikesToFilm(Film film, Set<Long> userIds) {
        long filmId = film.getId();

        for (long userId : userIds) {
            String sqlQuery = "insert into `user_likefilm`(user_id, film_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, filmId);
            log.info("Пользователь с id {} поставил лайк фильму с id {}.", userId, filmId);
        }
    }

    private void addUserLikeFilm(Film film) {
        long filmId = film.getId();
        Set<Long> likes = film.getLikes();
        for (long userIdLike : likes) {
            String sqlQuery = "insert into `user_likefilm`(user_id, film_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, userIdLike, filmId);
            log.info("Фильму с id {} поставил лайк пользователь с id {}.", filmId, userIdLike);
        }
    }

    private Set<Integer> findFilmsGenreIdsFromDb(long filmId) {
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
        long filmId = film.getId();
        if (!filmExists(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        String sqlQueryFilm = "select film.id, film.name, description, release,  duration, film.mpa_id, mpa.name \" +\n" +
                "                \"from `film` join mpa on film.mpa_id = mpa.id and film.id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQueryFilm, filmId);
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
        }
        List<Film> result = jdbcTemplate.query(sqlQueryFilm, this::mapRowToFilm, filmId);

        return result.get(0);
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
        Set<Long> newSetLikeIds = film.getLikes();
        Set<Long> oldSetLikeIds = findFilmsLikeIdsFromDb(film.getId());

        Set<Long> saveNewSetLikeIds = newSetLikeIds;
        Set<Long> saveOldSetLikeIds = oldSetLikeIds;

        oldSetLikeIds.removeAll(newSetLikeIds);
        saveNewSetLikeIds.removeAll(saveOldSetLikeIds);

        if (!oldSetLikeIds.isEmpty()) {
            deleteLikesFromFilm(film.getId(), oldSetLikeIds);
        }
        if (!saveNewSetLikeIds.isEmpty()) {
            addLikesToFilm(film, saveNewSetLikeIds);
        }
    }

    private Set<Long> findFilmsLikeIdsFromDb(long filmId) {
        if (filmExists(filmId)) {
            String sqlQuery = "select user_id from `user_likefilm` where film_id = ?";
            List<Long> listLikes = jdbcTemplate.queryForList(sqlQuery, Long.class, filmId);

            return new HashSet<Long>(listLikes);
        } else {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    private void deleteGenresFromFilm(long filmId, Set<Integer> genreIds) {
        for (int genreId : genreIds) {
            String sqlQuery = "delete from `film_genre` where film_id = ? and genre_id = ?";
            if (jdbcTemplate.update(sqlQuery, filmId, genreId) > 0) {
                log.info("Удалён жанр {} у фильма {}", genreId, filmId);
            }
        }
    }

    private void deleteLikesFromFilm(long filmId, Set<Long> userIds) {
        for (long userId : userIds) {
            String sqlQuery = "delete from `user_likefilm` where film_id = ? and user_id = ?";
            if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
                log.info("Удалён лайк пользователя {} у фильма {}", userId, filmId);
            }
        }
    }

    @Override
    public Film findFilmById(long filmId) {
        String sqlQuery = "select film.id, film.name, description, release,  duration, film.mpa_id, mpa.name \" +\n" +
                "                \"from `film` join mpa on film.mpa_id = mpa.id and film.id = ?";
        List<Film> result = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, filmId);
        if (result.size() < 1) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        return result.get(0);
    }

    @Override
    public long deleteFilmById(long filmId) {
        deleteGenresFromFilm(filmId, findFilmsGenreIdsFromDb(filmId));
        deleteLikesFromFilm(filmId, findFilmsLikeIdsFromDb(filmId));
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

    @Override
    public List<Film> findPopularFilms(int count) {
        List<Integer> idsPopularFilms = findIdsPopularFilms(count);
        List<Film> popFilms = new ArrayList<>();

        idsPopularFilms.forEach(id -> popFilms.add(findFilmById(id)));
        return popFilms;
    }

    private List<Integer> findIdsPopularFilms(int count) {
        String sql = "select film.id from film\n" +
                "left join user_likefilm on film.id = user_likefilm.film_id\n" +
                "group by film.id\n" +
                "order by count(user_likefilm.film_id) desc\n" +
                "limit ?";
        List<Integer> queryForList = jdbcTemplate.queryForList(sql, Integer.class, count);
        List<Integer> filmIdsList = new ArrayList<>();
        queryForList.forEach(id -> filmIdsList.add(id));
        return filmIdsList;
    }

    private void deleteUserLikeFilm(Film film) {
        long filmId = film.getId();
        Set<Long> likes = film.getLikes();
        for (long userId : likes) {
            String sqlQuery = "delete from `user_likefilm` where film_id = ? and user_id = ?";
            if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
                log.info("Удалён лайк пользователя {} у фильма {}", userId, filmId);
            }
        }
    }

    private void deleteFilmGenre(Film film) {
        long filmId = film.getId();

        List<Genre> mapsGenres = film.getGenres();
        Set<Integer> setGenres = mapsGenres.stream().map(genre -> genre.getId()).collect(Collectors.toSet());

        for (Integer genreId : setGenres) {
            String sqlQuery = "delete from `film_genre` where film_id = ? and genre_id = ?";
            if (jdbcTemplate.update(sqlQuery, filmId, genreId) > 0) {
                log.info("Удалён жанр {} у фильма {}", genreId, filmId);
            }
        }
    }

    private boolean filmExists(long id) {
        String sqlQuery = "select count(*) from `film` where id = ?";
        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        long result = jdbcTemplate.queryForObject(sqlQuery, Long.class, id);

        return result == 1;
    }
}
