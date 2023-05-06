package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.enums.OperationType.ADD;
import static ru.yandex.practicum.filmorate.enums.OperationType.REMOVE;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, @Qualifier("feedDbStorage") FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
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
        if (userStorage.getUserOne(id) == null || userStorage.getUserOne(friendId) == null) {
            log.warn("Получен некорректный идентификатор");
            throw new EntityNotFoundException();
        } else {
            feedStorage.addFeed(friendId, id, Instant.now().toEpochMilli(), FRIEND, ADD);
        }
        return userStorage.addFriend(id, friendId);
    }

    public User deleteFriend(int id, int friendId) {
        if (userStorage.getUserOne(id) == null || userStorage.getUserOne(friendId) == null) {
            log.warn("Получен некорректный идентификатор");
            throw new EntityNotFoundException();
        } else {
            feedStorage.addFeed(friendId, id, Instant.now().toEpochMilli(), FRIEND, REMOVE);
        }
        return userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getFriends(int id) {
        return userStorage.getFriends(id);
    }

    public List<User> getGeneralFriends(int id, int friendId) {
        return userStorage.getGeneralFriends(id, friendId);
    }

    public Collection<Film> getRecommendation(int id) {
        return new ArrayList<>(userStorage.getRecommendation(id));
    }
}

