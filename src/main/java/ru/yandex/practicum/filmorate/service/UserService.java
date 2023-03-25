package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.*;


@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService() {
        this.userStorage = new InMemoryUserStorage();
    }

    public User register(User user) {
        if (!user.getLogin().contains(" ")) {
            return userStorage.register(user);
        } else {
            log.error("Логин содержит пробелы");
            throw new ValidationException("Логин содержит пробелы");
        }

    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserOne(int id) {
        return userStorage.getUserOne(id);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User addFriend(int id, int friendId) {
        return userStorage.addFriend(id, friendId);
    }

    public User deleteFriend(int id, int friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getFriends(int id) {
        return userStorage.getFriends(id);
    }

    public List<User> getGeneralFriends(int id, int friendId) {
        return userStorage.getGeneralFriends(id, friendId);
    }

}

