package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface Search {
    Collection<Film> searchByTitleOrDirector(String query, boolean searchByTitle, boolean searchByDirector);
}
