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
    private long id;

    private String name;
    @Pattern(regexp = "^.+@.+\\..+$", message = "Некорректный адрес электронной почты")
    private final String email;
    @NotBlank
    @Pattern(regexp = "^^\\S+$", message = "Логин пользователя пуст или содержит пробелы.")
    private final String login;
    @Birthday
    private final LocalDate birthday;
    private Set<Long> friends;

    public User(String name, String email, String login, LocalDate birthday) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        friends = new HashSet<>();
    }

    public void addFriend(long friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(long friendId) {
        friends.remove(friendId);
    }

    public List<Long> getCommonFriends(User otherUser) {
        List<Long> commonFriendsId;
        if (this.getId() == otherUser.getId()) {
            commonFriendsId = this.getFriends().stream().collect(Collectors.toList());
            return Collections.unmodifiableList(commonFriendsId);
        }
        Set<Long> intersection = new HashSet<>(getFriends());
        intersection.retainAll(otherUser.getFriends());
        commonFriendsId = intersection.stream().collect(Collectors.toList());
        return Collections.unmodifiableList(commonFriendsId);
    }
}
