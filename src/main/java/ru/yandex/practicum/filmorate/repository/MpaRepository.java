package ru.yandex.practicum.filmorate.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

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

    public Collection<Mpa> getMpa() {
        List<Mpa> mpas = new ArrayList<>();
        String sql = "SELECT * FROM MPA";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(sqlRowSet.getInt("ID"));
            mpa.setName(sqlRowSet.getString("NAME"));
            mpas.add(mpa);
        }
        return mpas;
    }

    public Mpa getMpaOne(int id) {
        String sql = "SELECT * FROM MPA where ID=?";
        try {
            return jdbcTemplate.query(sql, rs -> {
                if (rs.next()) {
                    return new Mpa(rs.getInt("ID"), rs.getString("NAME"));
                } else {
                    throw new EntityNotFoundException("такого id нет");
                }
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("такого id нет");
        }

    }


}

