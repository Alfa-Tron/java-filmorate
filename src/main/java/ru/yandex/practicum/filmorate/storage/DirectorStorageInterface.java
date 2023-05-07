package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Directors;

import java.util.List;

public interface DirectorStorageInterface {
    Directors addNewDirector(Directors director);

    Directors updateDirector(Directors director);

    List<Directors> getAllDirectors();

    Directors getDirectorById(Integer id);

    void deleteDirectorById(Integer id);
}
