package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Birthday;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Data
public class User {
    private int id;

    private String name;
    @Pattern(regexp = "^.+@.+\\..+$", message = "Некорректный адрес электронной почты")
    private final String email;
    @NotBlank
    @Pattern(regexp = "^^\\S+$", message = "Логин пользователя пуст или содержит пробелы.")
    private final String login;
    @Birthday
    private final LocalDate birthday;
    private Set<Integer> friends;

    public User(String name, String email, String login, LocalDate birthday) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        friends = new HashSet<>();
    }

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(int friendId) {
        friends.remove(friendId);
    }

    public List<Integer> getCommonFriends(User otherUser) {
        List<Integer> commonFriendsId;
        if (this.getId() == otherUser.getId()) {
            commonFriendsId = this.getFriends().stream().collect(Collectors.toList());
            return Collections.unmodifiableList(commonFriendsId);
        }
        Set<Integer> intersection = new HashSet<>(getFriends());
        intersection.retainAll(otherUser.getFriends());
        commonFriendsId = intersection.stream().collect(Collectors.toList());
        return Collections.unmodifiableList(commonFriendsId);
    }
}
