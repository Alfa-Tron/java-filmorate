package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static ru.yandex.practicum.filmorate.storage.InMemoryUserStorage.users;

@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    public static final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Film addFilm(Film film) {

        if (film.getId() == null) {
            film.setId(id++);
        }
        films.put(film.getId(), film);
        log.debug("Фильм с id {} добавлен", film.getId());
        return film;
    }

    @Override
    public Film getFilm(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.error("Фильма с id {} нет", id);
            throw new EntityNotFoundException("Фильма с таким id нет");
        }
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);

        } else {
            log.error("Фильма с таким id нет");
            throw new EntityNotFoundException("Фильма с таким id нет");
        }
        return film;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        if (films.containsKey(filmId) && users.containsKey(userId)) {
            films.get(filmId).getLikes().add(userId);
            return films.get(filmId);
        } else {
            log.error("Пользователь или фильм с id не найден");
            throw new NullPointerException("Неверный Id");
        }
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        if (films.containsKey(filmId) && users.containsKey(userId)) {
            films.get(filmId).getLikes().remove(userId);
            return films.get(filmId);
        } else {
            log.error("Пользователь или фильм с id не найден");
            throw new NullPointerException("Неверный Id");
        }
    }

    @Override
    public Collection<Film> getPopularityFilms(Integer count) {
        if (count == null) count = 10;
        List<Film> result = new ArrayList<>();
        List<Film> popularityFilms = new ArrayList<Film>(films.values());
        popularityFilms.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());

        for (int i = 0; i < count && i < popularityFilms.size(); i++) {
            result.add(popularityFilms.get(i));
        }
        return result;
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return null;
    }
}