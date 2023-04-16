package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Film getFilm(int id);

    Collection<Film> getFilms();

    Film update(Film film);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    Collection<Film> getPopularityFilms(Integer count);

    Collection<Film.Mpa> getMpa();

    Film.Mpa getMpaOne(int id);
}
