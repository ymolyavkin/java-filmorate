package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.Validator;

import java.util.Map;

@Service
public class UserService {
    private static int id = 0;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private int generationId() {
        return ++id;
    }

    public User create(@RequestBody User user) {
        user.setId(generationId());
        /*if (Validator.validationFailedIsEmptyAndContainsSpaces(user.getLogin())) {
            log.error("Логин пользователя пуст или содержит пробелы.");
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        if (Validator.validationFailedEmail(user.getEmail())) {
            log.error("Адрес электронной почты не прошел проверку.");
            throw new ValidationException("Адрес электронной почты не прошел проверку.");
        }
        if (Validator.validationFailedBirthdayIsAfterNow(user.getBirthday())) {
            log.error("Некорректная дата рождения.");
            throw new ValidationException("Некорректная дата рождения.");
        }
        if (Validator.validationFailedIsEmpty(user.getName())) {
            user.setId(2);
            user.setName(user.getLogin());
            log.debug("Имя пользователя пусто");
            System.out.println("Имя пользователя пусто");
        }*/
        userStorage.addUser(user);

        return user;
    }
    public User put(@RequestBody User user) {

        if (Validator.validationFailedIsEmptyAndContainsSpaces(user.getLogin())) {
            log.error("Логин пользователя пуст или содержит пробелы.");
            throw new ValidationException("Логин пользователя пуст или содержит пробелы.");
        }
        if (Validator.validationFailedEmail(user.getEmail())) {
            log.error("Адрес электронной почты не прошел проверку.");
            throw new ValidationException("Адрес электронной почты не прошел проверку.");
        }
        if (Validator.validationFailedBirthdayIsAfterNow(user.getBirthday())) {
            log.error("Некорректная дата рождения.");
            throw new ValidationException("Некорректная дата рождения.");
        }
        userStorage.updateUser(user);

        return user;
    }
    public Map<Integer, User> findAll() {
   return userStorage.findAll();
    }
    public User findUserById(Integer userId) {
        log.debug("Id user: " + userId);

        return userStorage.findUserById(userId);
    }
    //**********
    /*

    //*******************************
    /*
    @Service
public class PostService {
    private final UserService userService;
    private final List<Post> posts = new ArrayList<>();

    private static Integer globalId = 0;

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Post create(Post post) {
        User postAuthor = userService.findUserByEmail(post.getAuthor());
        if (postAuthor == null) {
            throw new UserNotFoundException(String.format(
                    "Пользователь %s не найден",
                    post.getAuthor()));
        }

        post.setId(getNextId());
        posts.add(post);
        return post;
    }

    public Post findPostById(Integer postId) {
        return posts.stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst()
                .orElseThrow(() -> new PostNotFoundException(String.format("Пост № %d не найден", postId)));
    }

    public List<Post> findAll(Integer size, Integer from, String sort) {
        return posts.stream()
                .sorted((p0, p1) -> compare(p0, p1, sort))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    public List<Post> findAllByUserEmail(String email, Integer size, String sort) {
        return posts.stream()
                .filter(p -> email.equals(p.getAuthor()))
                .sorted((p0, p1) -> compare(p0, p1, sort))
                .limit(size)
                .collect(Collectors.toList());
    }

    private static Integer getNextId() {
        return globalId++;
    }

    private int compare(Post p0, Post p1, String sort) {
        int result = p0.getCreationDate().compareTo(p1.getCreationDate()); //прямой порядок сортировки
        if (sort.equals(DESCENDING_ORDER)) {
            result = -1 * result; //обратный порядок сортировки
        }
        return result;
    }
}

     */

}
