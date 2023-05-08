package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Directors;
import ru.yandex.practicum.filmorate.service.DirectorServiceInterface;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorServiceInterface directorService;

    @PostMapping
    public Directors addNewDirector(@RequestBody Directors director) {
        log.info("Пришел запрос на добавление нового режиссёра.");
        return directorService.addNewDirector(director);
    }

    @GetMapping
    public List<Directors> getAllDirectors() {
        log.info("Пришел запрос на получение списка всех режиссёров.");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Directors getDirectorById(@PathVariable Integer id) {
        log.info("Пришел запрос на получение режиссёра с id = {}.", id);
        return directorService.getDirectorById(id);
    }

    @PutMapping
    public Directors updateDirector(@RequestBody Directors director) {
        log.info("Пришел запрос на обновление режиссёра.");
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable Integer id) {
        log.info("Пришел запрос на удаление режиссёра с id = {}.", id);
        directorService.deleteDirectorById(id);
    }
}
