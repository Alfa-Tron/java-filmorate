package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.Collection;

@Service("mpaService")
public class MpaService {

    @Autowired
    private final MpaRepository mpaRepository;

    public MpaService(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    public Collection<Mpa> getMpa() {
        return mpaRepository.getMpa();
    }

    public Mpa getMpaOne(int id) {
        return mpaRepository.getMpaOne(id);
    }
}
