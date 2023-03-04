package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    private int id = 1;

    @PostMapping
    public User register(@RequestBody @Valid User user) {
        int k = 1;
        if (!user.getLogin().contains(" ")) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            if (user.getId() == null) {
                user.setId(id++);
            }
            users.put(user.getId(), user);
            log.debug("Пользователь с логином {} добавлен", user.getLogin());
        } else {
            log.error("Логин содержит пробелы");
            throw new ValidationException();
        }

        return user;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName().isBlank() || user.getName() == null) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
        } else {
            log.error("Пользователя с id {} нет", user.getId());
            throw new ValidationException();
        }
        return user;
    }

}
