package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("feedDbStorage")
    private final FeedStorage feedStorage;

    @Override
    public List<Feed> getFeed(int id) {
        if (userStorage.getUserOne(id) == null) {
            log.warn("Пользователь не существует");
            throw new EntityNotFoundException();
        }
        log.info("Возвращаем ленту событий пользователя с ид {}", id);
        return feedStorage.getFeed(id);
    }
}