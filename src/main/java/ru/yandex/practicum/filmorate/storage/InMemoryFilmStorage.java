package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    public static final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
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

    @Override
    public Film getFilm(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.error("Фильма с id {} нет", id);
            throw new NullPointerException("Фильма с таким id нет");
        }
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
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