package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        int userId = stringToInt(resultSet.getString("id"));
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = LocalDate.parse(resultSet.getString("birthday"), formatter);
        User user = new User(name, email, login, birthday);

        user.setId(userId);
        user.setFriends(findUsersFriends(userId));
        return user;
    }
    private int mapRowToFriendId(ResultSet resultSet, int rowNum) throws SQLException {
        int userId = stringToInt(resultSet.getString("one_friend_id"));
        int friendId = stringToInt(resultSet.getString("other_friend_id"));

        return friendId;
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

    /*
 public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(String.format(
                    "Пользователь с id %s не найден.",
                    user.getId()
            ));
        }
        users.put(user.getId(), user);
    }
 */
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
            //log.info("Найден пользователь: {} {}", userRows.getString("id"), login);

            User user = new User(name, email, login, birthday);
            user.setId(userId);
            user.setFriends(findUsersFriends(userId));

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
    @Override
    public void addFriend(int userId, int friendId) {
        if (userExists(userId) && userExists(friendId)) {
            String sqlQuery = "insert into `friendship`(one_friend_id, other_friend_id) values (?, ?)";

            jdbcTemplate.update(sqlQuery, userId, friendId);
            log.info("Пользователь с идентификатором {} добавлен в друзья пользователю {}.", friendId, userId);
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " и/или с id " + friendId + " не найден.");
        }
    }
private Set<Integer> findUsersFriends(int userId) {
    if (userExists(userId)){
        String sqlQuery = "select other_friend_id from `friendship` where one_friend_id = ?";
      //  List<String> listFriends = jdbcTemplate.queryForList(sqlQuery, String.class);
        List<Integer> listFriends = jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);

        return new HashSet<Integer>(listFriends);
    } else {
        throw new NotFoundException("Пользователь с id " + userId + " не найден.");
    }
}
    /*protected void relation(Role role, boolean newer) {
        // role -> actions
        String ALL_ACTIONID =
                "SELECT action.action_id FROM action RIGHT JOIN role_and_action ON role_id WHERE role_id = (?);";
        String REMOVE_ACTIONID =
                "DELETE FROM cucgp.`role_and_action` WHERE action_id = (?) AND role_id = (?);";
        String ADD_ACTIONID = "INSERT INTO cucgp.`role_and_action` (action_id, role_id) VALUES (?, ?);";

        List<Integer> actions =
                jdbcTemplate.queryForList(ALL_ACTIONID, new Object[]{role.getRoleId()}, Integer.class);
        ArrayList<Integer> remove = new ArrayList<Integer>();
        ArrayList<Integer> add = new ArrayList<Integer>();

        if (newer) {
            remove.addAll(actions);
            remove.removeAll(role.getActions());
            for (Integer actionId : remove) {
                jdbcTemplate.update(REMOVE_ACTIONID, actionId, role.getRoleId());
            }
            add.addAll(role.getActions());
            add.removeAll(actions);
            for (Integer actionId : add) {
                jdbcTemplate.update(ADD_ACTIONID, actionId, role.getRoleId());
            }
        } else {
            role.setActions(null);
            role.setActions(new TreeSet<Integer>(actions));
        }
    }*/
    private boolean userExists(int id) {
        String sqlQuery = "select count(*) from `user` where id = ?";
        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);

        return result == 1;
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