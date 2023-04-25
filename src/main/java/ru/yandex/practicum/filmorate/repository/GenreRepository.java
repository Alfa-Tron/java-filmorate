package ru.yandex.practicum.filmorate.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class GenreRepository {

    private final JdbcTemplate jdbcTemplate;

    public GenreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM genre";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            Genre genre = new Genre();
            genre.setId(sqlRowSet.getInt("ID"));
            genre.setName(sqlRowSet.getString("NAME"));
            genres.add(genre);
        }
        return genres;
    }

    public Genre getGenreOne(int id) {
        String sql = "SELECT * FROM genre where ID=" + id;
        try {
            return jdbcTemplate.query(sql, rs -> {
                if (rs.next()) {
                    return new Genre(rs.getInt("ID"), rs.getString("NAME"));
                } else {
                    throw new EntityNotFoundException("такого id нет");
                }
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("такого id нет");
        }

    }
}
