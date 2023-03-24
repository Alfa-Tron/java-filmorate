package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private InMemoryFilmStorage inMemoryFilmStorage;
    @Autowired
    private FilmService filmService;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return inMemoryFilmStorage.getFilm(id);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        return inMemoryFilmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film setLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularity(@RequestParam(name = "count", defaultValue = "10") int count) {
        return filmService.getPopularityFilms(count);

    }
}
