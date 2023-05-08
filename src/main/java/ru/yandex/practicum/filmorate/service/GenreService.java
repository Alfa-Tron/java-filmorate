package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service("genreService")
public class GenreService {
    @Autowired
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Collection<Genre> getGenres() {
        return new ArrayList<>(genreRepository.getGenres());
    }

    public Genre getGenreOne(int id) {
        return genreRepository.getGenreOne(id);
    }
}
