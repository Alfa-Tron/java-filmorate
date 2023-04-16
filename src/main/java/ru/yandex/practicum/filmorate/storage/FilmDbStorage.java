package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;


import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO film (film_name, description, releaseDate, duration, rate, mpa) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId());

        for (Film.Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO FILMGENRE (FILM_ID, GENRE_ID) VALUES (?,?)",
                    film.getId(), genre.getId());
        }

        return film;
    }

    @Override
    public Film getFilm(int id) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("select * from FILM where ID = ?", id);
        Film film = new Film();

        if (sql.next()) {
            film.setId(id);
            film.setName(sql.getString("film_name"));
            film.setDescription(sql.getString("description"));
            film.setReleaseDate(LocalDate.parse(sql.getString("releaseDate")));
            film.setDuration(sql.getLong("duration"));
            film.setRate(sql.getInt("rate"));


            Film.Mpa mpa = new Film.Mpa();
            mpa.setId(sql.getInt("mpa"));

            film.setMpa(mpa);

            List<Film.Genre> genres = new ArrayList<>();
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from FILMGENRE where FILM_ID = ?", film.getId());
            while (sqlRowSet.next()) {
                Film.Genre genre = new Film.Genre();
                genre.setId(sqlRowSet.getInt("genre_id"));
                genres.add(genre);
            }
            film.setGenres(genres);

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
        String sql = "UPDATE FILM SET FILM_NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?,RATE =? , MPA =? WHERE id = ? ";
        int t = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        if (t == 0) {
            log.error("фильма с id {} нет", film.getId());
            throw new EntityNotFoundException("фильма с таким id нет");
        }

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
