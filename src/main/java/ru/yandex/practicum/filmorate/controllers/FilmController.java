package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        int k = 1;
        if (film.dateAfter()) {
            if (film.getId() == null) {
                film.setId(id++);
            }
            films.put(film.getId(), film);
            log.debug("Фильм с id {} добавлен", film.getId());
        } else {
            log.error("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException();
        }

        return film;
    }

    @GetMapping
    public Collection<Film> getUsers() {
        return films.values();
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);

        } else {
            log.error("Фильма с таким id нет");
            throw new ValidationException();
        }
        return film;
    }

}
