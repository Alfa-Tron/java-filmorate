package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
public class GenreController {
    @Autowired
    private GenreService genreService;

    @GetMapping("/genres")
    public Collection<Film.Genre> getGenres() {
        return genreService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Film.Genre getGenreOne(@PathVariable int id) {
        return genreService.getGenreOne(id);
    }
}
