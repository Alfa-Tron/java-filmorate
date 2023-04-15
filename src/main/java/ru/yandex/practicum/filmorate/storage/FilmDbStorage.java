package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO FILM (FILM_NAME, DESCRIPTION, RELEASEDATE, DURATION, RATING) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRating());

        return film;
    }

    @Override
    public Film getFilm(int id) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("select * from FILM where ID = ?", id);
        Film film = new Film();
        if(sql.next()){
            film.setId(id);
            film.setDescription(sql.getString("DESCRIPTION"));
            film.setName(sql.getString("NAME"));
            film.setDuration(sql.getLong("DURATION"));
            film.setRating(sql.getString("RATING"));
            film.setReleaseDate(sql.getDate("RELEASEDATE"));
            film.setRating(sql.getString("RATING"));
        }
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from FILM");
        while (filmRows.next()) {
            films.add(getFilm(filmRows.getInt("ID")));
        }
        return films;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update("UPDATE FILM SET FILM_NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?,RATING =? WHERE id = ?",
               film.getName(),film.getDescription(),film.getReleaseDate(),film.getDuration(),film.getRating());
        return film;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        return null;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        return null;
    }

    @Override
    public Collection<Film> getPopularityFilms(Integer count) {
        return null;
    }
}
