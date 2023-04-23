package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
@RestController
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<Post> findAll(
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(defaultValue = DESCENDING_ORDER, required = false) String sort
    ) {
        if (!SORTS.contains(sort)) {
            throw new IncorrectParameterException("sort");
        }
        if (page < 0) {
            throw new IncorrectParameterException("page");
        }
        if (size <= 0) {
            throw new IncorrectParameterException("size");
        }
        Integer from = page * size;
        return postService.findAll(size, from, sort);
    }

    @PostMapping(value = "/post")
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @GetMapping("/post/{postId}")
    public Post findPost(@PathVariable("postId") Integer postId) {
        return postService.findPostById(postId);
    }
}

 */
@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll().values().stream().collect(Collectors.toList());
    }

    @GetMapping("{userId}")
    public User findUserById(@PathVariable("userId") Integer userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("{userId}/friends")
    public List<User> findFriends(@PathVariable("userId") Integer userId) {
        return userService.findFriends(userId);
    }

    @GetMapping("{userId}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("userId") Integer userId, @PathVariable("otherId") Integer otherId) {
        return userService.findCommonFriends(userId, otherId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        // System.out.println("CreateUser." + user.getId() + "List Users: " + userService.findAll().values().stream().collect(Collectors.toList()));
        return userService.create(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        // System.out.println("PutUser." + user.getId() + "List Users: " + userService.findAll().values().stream().collect(Collectors.toList()));
        return userService.put(user);
    }

    @PutMapping("{userId}/friends/{friendId}")
    public Integer addFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.addFriend(userId, friendId);
        // System.out.println("controller: trying to add a user with id=" + userId + " to a friend with id= " + friendId);
       /* User user = userService.findUserById(userId);
        User friend = userService.findUserById(friendId);

        user.addFriend(friendId);
        userService.put(user);


        friend.addFriend(userId);
        userService.put(friend);*/

        return friendId;
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public Integer deleteFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(userId, friendId);
        /*User user = userService.findUserById(userId);
        user.deleteFriend(friendId);
        userService.put(user);

        User friend = userService.findUserById(friendId);
        friend.deleteFriend(userId);
        userService.put(friend);*/

        return friendId;
    }
}
