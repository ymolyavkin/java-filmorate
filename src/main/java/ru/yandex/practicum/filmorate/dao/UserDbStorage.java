package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select id, name, email, login,  birthday from `user`";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        int id = stringToInt(resultSet.getString("id"));
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = LocalDate.parse(resultSet.getString("birthday"), formatter);
        User user = new User(name, email, login, birthday);

        user.setId(id);
        return user;
    }

    //@Override
   /* public void addUserOld(User user) {
        String sqlQuery = "insert into `user`(email, login, name, birthday) values (?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
    }*/

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

    @Override
    public void updateUser(User user) {
        String userId = Integer.toString(user.getId());
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from `user` where id = ?", userId);
        if (userRows.next()) {
            String sqlQuery = "update `user` set email = ?, login = ?, name = ?, birthday = ? where id = ?";

            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    @Override
    public User findUserById(Integer userId) {
        String id = userId.toString();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from `user` where id = ?", id);
        if (userRows.next()) {
            String email = userRows.getString("email");
            String login = userRows.getString("login");
            String name = userRows.getString("name");
            LocalDate birthday = LocalDate.parse(userRows.getString("birthday"), formatter);
            log.info("Найден пользователь: {} {}", userRows.getString("id"), login);

            User user = new User(name, email, login, birthday);
            user.setId(userId);

            return user;
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    @Override
    public int deleteUserById(Integer userId) {
        String id = Integer.toString(userId);
        String sqlQuery = "delete from `user` where id = ?";

        if (jdbcTemplate.update(sqlQuery, id) > 0) {
            log.info("Удалён пользователь: {}", id);
        } else {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        return 0;
    }

    /*@Override
    public Optional<User> findUserById(String id) {
        // выполняем запрос к базе данных.
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from `user` where id = ?", id);
        if (userRows.next()) {
            // log.info("Найден пользователь: {} {}", userRows.getString("id"), userRows.getString("nickname"));
            // вы заполните данные пользователя в следующем уроке
            //  User user = new User();
            //   user.setId(id);
            //----------------------------------------------
            User user = new User("Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
            int userId = stringToInt(id);
            user.setId(userId);
            //----------------------------------------------
            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
    }
*/
    public void insertIntoGenre() {
        String sqlQuery = "insert into genre(name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, "first");

            return stmt;
        }, keyHolder);


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

    static int stringToInt(String userInput) {
        // Шаблон выбирает первое число из строки
        Pattern pattern = Pattern.compile(".*?(\\d+).*");
        Matcher matcher = pattern.matcher(userInput);
        String number = "-1";
        if (matcher.find()) {
            number = matcher.group(1);
        }
        return Integer.valueOf(number);
    }
}