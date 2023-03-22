package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
;

@RestController
@RequestMapping("/films")
public class FilmController extends FilmService {

    @Autowired
    private FilmService filmService;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
       return filmService.addFilm(film);
    }

    @GetMapping
    public Collection<Film> getUsers() {
        return filmService.getUsers();
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
       return filmService.update(film);
    }

}
