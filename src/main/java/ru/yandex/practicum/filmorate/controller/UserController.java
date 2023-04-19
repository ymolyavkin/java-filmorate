package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.Map;

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
    //private final Map<Integer, User> users = new HashMap<>();
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Map<Integer, User> findAll() {

        return userService.findAll();
    }
    @GetMapping("{userId}")
    public User findUserById(@PathVariable("userId") Integer userId) {
        return userService.findUserById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User put(@RequestBody User user) {
      userService.put(user);

        return user;
    }
}
