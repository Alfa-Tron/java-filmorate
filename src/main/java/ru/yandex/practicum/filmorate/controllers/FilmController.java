package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
;

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

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
       return inMemoryFilmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@RequestParam int id,@RequestParam int userId){
         filmService.addLike(id,userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@RequestParam int id,@RequestParam int userId){
        filmService.deleteLike(id,userId);
    }

    @GetMapping("/popular?count={count}")
    public Collection<Film> getPopularity(@RequestParam int count){
        return filmService.getPopularityFilms(count);

    }
}
