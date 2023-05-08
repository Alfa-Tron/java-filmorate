package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User register(User user) {
        return userStorage.register(user);
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

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }

    public Collection<Film> getRecommendation(int id) {
        return  new ArrayList<>(userStorage.getRecommendation(id));
    }
}