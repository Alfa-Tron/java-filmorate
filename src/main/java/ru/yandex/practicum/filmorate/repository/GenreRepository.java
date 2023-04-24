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
public class GenreRepository {

    private final JdbcTemplate jdbcTemplate;

    public GenreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Film.Genre> getGenres() {
        List<Film.Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM genre";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            Film.Genre genre = new Film.Genre();
            genre.setId(sqlRowSet.getInt("ID"));
            genre.setName(sqlRowSet.getString("NAME"));
            genres.add(genre);
        }
        return genres;
    }

    public Film.Genre getGenreOne(int id) {
        String sql = "SELECT * FROM genre where ID=" + id;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            Film.Genre genre = new Film.Genre();
            genre.setId(sqlRowSet.getInt("ID"));
            genre.setName(sqlRowSet.getString("NAME"));
            return genre;
        }
        throw new EntityNotFoundException("такого id нет");
    }
}
