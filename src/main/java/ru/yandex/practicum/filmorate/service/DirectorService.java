package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Directors;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Directors addNewDirector(Directors director) {
        return directorStorage.addNewDirector(director);
    }

    public Directors updateDirector(Directors director) {
        return directorStorage.updateDirector(director);
    }

    public List<Directors> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Directors getDirectorById(Integer id) {
        return directorStorage.getDirectorById(id);
    }

    public void deleteDirectorById(Integer id) {
        directorStorage.deleteDirectorById(id);
    }
}
