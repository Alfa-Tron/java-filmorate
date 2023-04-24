package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.ArrayList;
import java.util.Collection;

@RestController
public class MpaController {
    @Autowired
    private MpaService mpaService;

    @GetMapping("/mpa")
    public Collection<Film.Mpa> getMpa() {
        return new ArrayList<>(mpaService.getMpa());
    }

    @GetMapping("/mpa/{id}")
    public Film.Mpa getMpaOne(@PathVariable int id) {
        return mpaService.getMpaOne(id);
    }


}

