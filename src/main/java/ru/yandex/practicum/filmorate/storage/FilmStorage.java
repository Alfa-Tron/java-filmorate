package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Collections;

public interface FilmStorage {
    Film addFilm(Film film);

    Collection<Film> getFilms();

    Film update(Film film);

}
