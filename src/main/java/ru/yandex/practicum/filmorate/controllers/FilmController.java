package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping("/films")
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> getSortedDirectors(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getSortedDirectors(directorId, sortBy);
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
    public Collection<Film> mostPopularFilms(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int genreId,
            @RequestParam(defaultValue = "0") int year) {
        return filmService.mostPopularFilms(count, genreId, year);
    }

    @GetMapping("/films/common")
    public Collection<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }




    @GetMapping("/films/search")
    public Collection<Film> searchByTitleOrDirector(@RequestParam(name = "query") String query,
                                                    @RequestParam(name = "by") List<String> by) {
        return filmService.searchByTitleOrDirector(query, by);
    }
}

