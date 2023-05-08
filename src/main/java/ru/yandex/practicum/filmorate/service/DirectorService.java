package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Directors;
import ru.yandex.practicum.filmorate.storage.DirectorStorageInterface;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService implements DirectorServiceInterface {
    private final DirectorStorageInterface directorStorage;

    @Override
    public Directors addNewDirector(Directors director) {
        return directorStorage.addNewDirector(director);
    }

    @Override
    public Directors updateDirector(Directors director) {
        return directorStorage.updateDirector(director);
    }

    @Override
    public List<Directors> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    @Override
    public Directors getDirectorById(Integer id) {
        return directorStorage.getDirectorById(id);
    }

    @Override
    public void deleteDirectorById(Integer id) {
        directorStorage.deleteDirectorById(id);
    }
}
