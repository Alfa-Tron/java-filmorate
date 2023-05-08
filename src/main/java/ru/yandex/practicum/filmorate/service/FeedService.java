package ru.yandex.practicum.filmorate.service;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedService {
    @NonNull
    List<Feed> getFeed(@NonNull int id);
}