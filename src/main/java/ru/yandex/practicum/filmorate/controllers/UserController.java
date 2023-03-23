package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private InMemoryUserStorage userStorage;
    @Autowired
    private UserService userService;

    @PostMapping
    public User register(@RequestBody @Valid User user) {

        return userStorage.register(user);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userStorage.update(user);
    }

    @GetMapping("/{id}")
    public User getUser(@RequestParam int id) {
        return userStorage.getUsers().get(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@RequestParam int id, @RequestParam int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@RequestParam int id, @RequestParam int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@RequestParam int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getGeneralFriends(@RequestParam int id,@RequestParam int otherId){
        return userService.getGeneralFriends(id,otherId);
    }


}
