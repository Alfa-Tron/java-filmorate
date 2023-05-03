package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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

            for (Genre genre : film.getGenres()) {
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
        String sql = "SELECT * FROM FILM WHERE ID = ?";
        Film film = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            Film f = new Film();
            f.setId(rs.getInt("id"));
            f.setName(rs.getString("film_name"));
            f.setDescription(rs.getString("description"));
            f.setReleaseDate(LocalDate.parse(rs.getString("releaseDate")));
            f.setDuration(rs.getLong("duration"));
            f.setRate(rs.getInt("rate"));

            int mpaId = rs.getInt("mpa");
            String mpaSql = "SELECT * FROM MPA WHERE ID = ?";
            Mpa mpa = jdbcTemplate.queryForObject(mpaSql, new Object[]{mpaId}, (rsMpa, rowNUm) -> {
                Mpa m = new Mpa();
                m.setId(rsMpa.getInt("id"));
                m.setName(rsMpa.getString("name"));
                return m;
            });
            f.setMpa(mpa);

            String filmGenreSql = "SELECT g.id, g.name FROM GENRE g JOIN FILMGENRE fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
            List<Genre> genres = jdbcTemplate.query(filmGenreSql, new Object[]{id}, rsGenre -> {
                List<Genre> list = new ArrayList<>();
                while (rsGenre.next()) {
                    Genre g = new Genre();
                    g.setId(rsGenre.getInt("id"));
                    g.setName(rsGenre.getString("name"));
                    list.add(g);
                }
                return list;
            });
            f.setGenres(genres);

            return f;
        });
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
            List<Genre> genres = new ArrayList<>();
            jdbcTemplate.update("DELETE FROM FILMGENRE WHERE FILM_ID=" + film.getId());
            for (Genre genre : film.getGenres()) {
                int count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM FILMGENRE WHERE FILM_ID = ? AND GENRE_ID = ?",
                        Integer.class, film.getId(), genre.getId());
                if (count == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO FILMGENRE (FILM_ID, GENRE_ID) VALUES (?,?)",
                            film.getId(), genre.getId());
                    Genre g = new Genre();
                    g.setId(genre.getId());
                    genres.add(g);

                }

            }
            film.setGenres(genres);

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
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT f.*, M.* " +
                "FROM FILMLIKES " +
                "JOIN FILMLIKES fl ON fl.FILM_ID = FILMLIKES.FILM_ID " +
                "JOIN FILM f on f.ID = fl.FILM_ID " +
                "JOIN MPA M on f.ID = M.ID " +
                "WHERE fl.USER_ID = ? AND FILMLIKES.USER_ID = ?" +
                "ORDER BY f.RATE desc ";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        while (sqlRowSet.next()) {
            films.add(getFilm(sqlRowSet.getInt("ID")));
        }
        return films;
    }
}