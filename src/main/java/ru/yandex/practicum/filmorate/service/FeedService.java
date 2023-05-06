package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FeedService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public FeedService(@Qualifier("userDbStorage") UserStorage userStorage, @Qualifier("feedDbStorage") FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
    }

    public List<Feed> getFeed(int id) throws Exception {
        if (userStorage.getUserOne(id) == null) {
            log.warn("Пользователь не существует");
            throw new Exception("Пользователь не существует");
        }
        log.info("Возвращаем ленту событий пользователя с ид {}", id);
        return feedStorage.getFeed(id);
    }
}