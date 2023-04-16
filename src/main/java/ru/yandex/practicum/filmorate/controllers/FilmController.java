package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;


@RestController
@Slf4j
//@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @PostMapping("/films")
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return new ArrayList<>(filmService.getFilms());
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/films")
    public Film update(@RequestBody @Valid Film film) {
        return filmService.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film setLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularity(@RequestParam(name = "count", defaultValue = "10") int count) {
        return filmService.getPopularityFilms(count);

    }

    @GetMapping("/mpa")
    public Collection<Film.Mpa> getMpa() {
        return new ArrayList<>(filmService.getMpa());
    }

    @GetMapping("/mpa/{id}")
    public Film.Mpa getMpaOne(@PathVariable int id) {
        return filmService.getMpaOne(id);
    }

    @GetMapping("/genres")
    public Collection<Film.Genre> getGenres() {
        return new ArrayList<>(filmService.getGenres());
    }

    @GetMapping("/genres/{id}")
    public Film.Genre getGenreOne(@PathVariable int id) {
        return filmService.getGenreOne(id);
    }
}
