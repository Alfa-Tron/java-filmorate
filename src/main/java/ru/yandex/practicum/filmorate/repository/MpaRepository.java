package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class MpaRepository {
    private final JdbcTemplate jdbcTemplate;

    public MpaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Film.Mpa> getMpa() {
        List<Film.Mpa> mpas = new ArrayList<>();
        String sql = "SELECT * FROM MPA";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            Film.Mpa mpa = new Film.Mpa();
            mpa.setId(sqlRowSet.getInt("ID"));
            mpa.setName(sqlRowSet.getString("NAME"));
            mpas.add(mpa);
        }
        return mpas;
    }

    public Film.Mpa getMpaOne(int id) {
        String sql = "SELECT * FROM MPA where ID=" + id;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            Film.Mpa mpa = new Film.Mpa();
            mpa.setId(sqlRowSet.getInt("ID"));
            mpa.setName(sqlRowSet.getString("NAME"));
            return mpa;
        }
        throw new EntityNotFoundException("такого id нет");

    }

}

