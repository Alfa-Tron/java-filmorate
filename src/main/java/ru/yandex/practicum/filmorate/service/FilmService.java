package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Search;


import javax.validation.ValidationException;
import java.util.*;


@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final Search search;


    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, Search search) {
        this.filmStorage = filmStorage;
        this.search = search;
    }


    public Collection<Film> searchByTitleOrDirector(String query, List<String> by) {
        if (by.size() == 2 && ((by.get(0).equals("director") || by.get(0).equals("title")) && (by.get(1)
                .equals("director") || by.get(1).equals("title")) && !(by.get(0).equals(by.get(1))))) {

            return search.searchByTitleOrDirector(query, "director", "title");

        } else {
            if ((by.get(0).equals("director"))) {
                return search.searchByTitleOrDirector(query, "director", "");
            } else if (by.get(0).equals("title")) {
                return search.searchByTitleOrDirector(query, "", "title");
            } else {
                return search.searchByTitleOrDirector(query, "", "");
            }
        }

    }

    public Collection<Film> getSortedDirectors(Integer directorId, String sortBy) {
        if (sortBy.equals("year")) {
            return filmStorage.getSortedDirectorsByYear(directorId);
        }
        return filmStorage.getSortedDirectorsByLikes(directorId);
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
}

