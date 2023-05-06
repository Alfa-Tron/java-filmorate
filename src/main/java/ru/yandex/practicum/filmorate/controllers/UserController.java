package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private FeedService feedService;

    public UserController(@Qualifier("userService") UserService userService, @Qualifier("feedService") FeedService feedService) {
        this.userService = userService;
        this.feedService = feedService;
    }


    @PostMapping
    public User register(@RequestBody @Valid User user) {
        return userService.register(user);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return new ArrayList<>(userService.getUsers());
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUserOne(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return new ArrayList<>(userService.getFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getGeneralFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getGeneralFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeed(@PathVariable int id) throws Exception {
        return feedService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendation(@PathVariable int id) {
        return userService.getRecommendation(id);
    }
}
