package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage.films;
import static ru.yandex.practicum.filmorate.storage.InMemoryUserStorage.users;

@Slf4j
@Service
public class FilmService {

    public Film addLike(int filmId, int userId) {
        if (films.containsKey(filmId) && users.containsKey(userId)) {
            films.get(filmId).getLikes().add(userId);
            return films.get(filmId);
        } else {
            log.error("Пользователь или фильм с id не найден");
            throw new NullPointerException();
        }
    }

    public Film deleteLike(int filmId, int userId) {
        if (films.containsKey(filmId) && users.containsKey(userId)) {
            films.get(filmId).getLikes().remove(userId);
            return films.get(filmId);
        } else {
            log.error("Пользователь или фильм с id не найден");
            throw new NullPointerException();
        }
    }

    public Collection<Film> getPopularityFilms(Integer count) {
       if(count==null) count=10;
        List<Film> result = new ArrayList<>();
        List<Film> popularityFilms = new ArrayList<Film>(films.values());
        popularityFilms.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());
        for (int i = 0; i < count && i < popularityFilms.size(); i++) {
            result.add(popularityFilms.get(i));
        }
        return result;
    }


}

