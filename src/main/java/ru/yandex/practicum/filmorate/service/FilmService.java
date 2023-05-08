package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;


import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getSortedDirectors(Integer directorId, String sortBy) {
        if (sortBy.equals("year")) {
            return filmStorage.getSortedDirectorsByYear(directorId);
        } else if (sortBy.equals("likes")) {
            return filmStorage.getSortedDirectorsByLikes(directorId);
        } else {
            throw new EntityNotFoundException("ссылка не найдена.");
        }
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        if (film.dateAfter()) {
            return filmStorage.addFilm(film);
        } else {
            log.error("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException();
        }
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public Film addLike(int filmId, int userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(int filmId, int userId) {
        return filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getPopularityFilms(Integer count) {
        return filmStorage.getPopularityFilms(count);
    }

    public Collection<Film> mostPopularFilms(int count, int genreId, int year) {
        return filmStorage.mostPopularFilms(count, genreId, year);
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}