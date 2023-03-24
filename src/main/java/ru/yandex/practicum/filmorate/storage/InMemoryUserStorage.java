package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    public static final Map<Integer, User> users = new HashMap<>();

    private int id = 1;

    @Override
    public User register(@RequestBody @Valid User user) {
        int k = 1;
        if (!user.getLogin().contains(" ")) {
            if (user.getName() == null||user.getName().isBlank()) {
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

    @Override
    public User getUser(int id){
        if(users.containsKey(id)){
            return users.get(id);
        }
        else {
            log.error("Пользователя с таким {} нет",id);
           throw new NullPointerException();
        }
    }
    @Override
    public Map<Integer,User> getUsers() {
        return users;
    }

    @Override
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