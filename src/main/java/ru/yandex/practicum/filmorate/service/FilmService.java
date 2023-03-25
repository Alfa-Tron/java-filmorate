package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;


import javax.validation.Valid;
import java.util.*;



@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService() {
        this.filmStorage = new InMemoryFilmStorage();
    }

    public Film update(@RequestBody @Valid Film film) {
        return filmStorage.update(film);
    }

    public Collection<Film> getFilms() {
        return  filmStorage.getFilms();
    }

    public Film addFilm(@RequestBody @Valid Film film) {
        return filmStorage.addFilm(film);
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

