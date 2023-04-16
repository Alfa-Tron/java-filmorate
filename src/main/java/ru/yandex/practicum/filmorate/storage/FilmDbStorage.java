package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;


import javax.persistence.EntityNotFoundException;
import java.sql.PreparedStatement;
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

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setString(3, String.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setDouble(5, film.getRate());
            ps.setInt(6, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue()); // установка полученного id фильма в объект film

        if (film.getGenres() != null) {

            for (Film.Genre genre : film.getGenres()) {
                int count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM FILMGENRE WHERE FILM_ID = ? AND GENRE_ID = ?",
                        Integer.class, film.getId(), genre.getId());
                if (count == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO FILMGENRE (FILM_ID, GENRE_ID) VALUES (?,?)",
                            film.getId(), genre.getId());
                }

            }
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

            SqlRowSet sql1 = jdbcTemplate.queryForRowSet("select * from MPA where ID = ?", sql.getInt("mpa"));
            Film.Mpa mpa = new Film.Mpa();
            mpa.setId(sql.getInt("mpa"));
            if (sql1.next()) mpa.setName(sql1.getString("name"));
            film.setMpa(mpa);


            List<Film.Genre> genres = new ArrayList<>();
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from FILMGENRE where FILM_ID = ?", film.getId());
            while (sqlRowSet.next()) {
                Film.Genre genre = new Film.Genre();
                genre.setId(sqlRowSet.getInt("genre_id"));
                SqlRowSet sqlRowSetGenre = jdbcTemplate.queryForRowSet("select * from GENRE where ID = ?", sqlRowSet.getInt("genre_id"));
                if (sqlRowSetGenre.next()) genre.setName(sqlRowSetGenre.getString("name"));
                genres.add(genre);
            }
            film.setGenres(genres);

        }
        if (film.getName() == null) {
            log.error("фильма с id {} нет", film.getId());
            throw new EntityNotFoundException("фильма с таким id нет");
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
        if (film.getGenres() != null) {
            jdbcTemplate.update("DELETE FROM FILMGENRE WHERE FILM_ID=" + film.getId());
            for (Film.Genre genre : film.getGenres()) {
                int count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM FILMGENRE WHERE FILM_ID = ? AND GENRE_ID = ?",
                        Integer.class, film.getId(), genre.getId());
                if (count == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO FILMGENRE (FILM_ID, GENRE_ID) VALUES (?,?)",
                            film.getId(), genre.getId());
                }

            }
        }
        if (film.getMpa() != null) {
            String sqlMpa = "UPDATE FILM SET MPA = ? WHERE ID= ? ";
            jdbcTemplate.update(sqlMpa, film.getMpa().getId(), film.getId());
        }

        String sql = "UPDATE FILM SET FILM_NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?,RATE =? , MPA =? WHERE id =  " + film.getId();
        int t = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId());


        if (t == 0) {
            log.error("фильма с id {} нет", film.getId());
            throw new EntityNotFoundException("фильма с таким id нет");
        }

        return film;

    }

    @Override
    public Film addLike(int filmId, int userId) {
        String query = "INSERT INTO filmLikes (film_id, user_id) VALUES (?, ?)";
        int t = jdbcTemplate.update(query, filmId, userId);
        String sql = "UPDATE FILM SET RATE=RATE+1 WHERE id = ? ";

        int t1 = jdbcTemplate.update(sql, filmId);
        if (t == 0 || t1 == 0) throw new EntityNotFoundException("такого id нет");
        return getFilm(filmId);
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        String query = "DELETE FROM filmLikes WHERE FILM_ID=? AND USER_ID=?";
        int t = jdbcTemplate.update(query, filmId, userId);
        String sql = "UPDATE FILM SET RATE=RATE-1 WHERE id = ? ";

        int t1 = jdbcTemplate.update(sql, filmId);
        if (t == 0 || t1 == 0) throw new EntityNotFoundException("такого id нет");

        return getFilm(filmId);
    }

    @Override
    public Collection<Film> getPopularityFilms(Integer count) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT id FROM film ORDER BY rate DESC";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next() && count-- > 0) {
            films.add(getFilm(sqlRowSet.getInt("id")));
        }

        return films;
    }

    @Override
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

    @Override
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

    @Override
    public Collection<Film.Genre> getGenres() {
        List<Film.Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM GENRE";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            Film.Genre genre = new Film.Genre();
            genre.setId(sqlRowSet.getInt("ID"));
            genre.setName(sqlRowSet.getString("NAME"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Film.Genre getGenreOne(int id) {
        String sql = "SELECT * FROM GENRE where ID=" + id;
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
