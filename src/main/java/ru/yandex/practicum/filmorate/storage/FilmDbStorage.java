package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Directors;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Repository
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate DATE_OF_FIRST_FILM = LocalDate.of(1895, Month.DECEMBER, 28);

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        filmValidation(film);
        String sql = "INSERT INTO film (film_name, description, releaseDate, duration, rate, mpa) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setString(3, String.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setDouble(5, 0);
            ps.setInt(6, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue()); // установка полученного id фильма в объект film

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

        if (film.getDirectors() != null) {
            for (Directors directors : film.getDirectors()) {
                int count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM FILMDIRECTORS WHERE FILM_ID = ? AND DIRECTORS_ID = ?",
                        Integer.class, film.getId(), directors.getId());
                if (count == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO FILMDIRECTORS (FILM_ID, DIRECTORS_ID) VALUES (?,?)",
                            film.getId(), directors.getId());
                }
            }
        }
        return getFilm(film.getId());
    }

    @Override
    public Collection<Film> getSortedDirectorsByYear(Integer directorId) {
        String sql = "SELECT f.id, f.film_name, f.description, f.releaseDate, f.duration, f.rate " +
                "FROM film f " +
                "INNER JOIN FilmDirectors fd ON f.id = fd.film_id " +
                "WHERE fd.directors_id = ? " +
                "ORDER BY f.releaseDate";
        return jdbcTemplate.query(sql, new Object[]{directorId},
                (rs, rowNum) -> getFilm(rs.getInt("id")));
    }

    @Override
    public Collection<Film> getSortedDirectorsByLikes(Integer directorId) {
        String sql = "SELECT f.id, f.film_name, f.description, f.releaseDate, f.duration, f.rate, COUNT(fl.user_id) AS likes " +
                "FROM film f " +
                "INNER JOIN FilmDirectors fd ON f.id = fd.film_id " +
                "LEFT JOIN filmLikes fl ON f.id = fl.film_id " +
                "WHERE fd.directors_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY likes DESC";
        List<Film> films = jdbcTemplate.query(sql, new Object[]{directorId},
                (rs, rowNum) -> getFilm(rs.getInt("id")));
        if (films.isEmpty()) {
            throw new EntityNotFoundException("не найден.");
        }
        return films;
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

            String filmDirectorsSql = "SELECT d.id, d.name FROM DIRECTORS d " +
                    "JOIN FILMDIRECTORS fd on d.id = fd.directors_id WHERE fd.film_id = ?";
            List<Directors> directors = jdbcTemplate.query(filmDirectorsSql, new Object[]{id}, rsDirectors -> {
                List<Directors> list = new ArrayList<>();
                while (rsDirectors.next()) {
                    Directors d = new Directors();
                    d.setId(rsDirectors.getInt("id"));
                    d.setName(rsDirectors.getString("name"));
                    list.add(d);
                }
                return list;
            });
            f.setDirectors(directors);

            String filmGenreSql = "SELECT g.id, g.name FROM GENRE g " +
                    "JOIN FILMGENRE fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
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
        filmValidation(film);
        String sql = "UPDATE FILM SET FILM_NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?," +
                " DURATION = ?, RATE = ?, MPA = ? WHERE ID = " + film.getId();
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        Optional<List<Genre>> filmGenres = Optional.ofNullable(film.getGenres());
        setGenresToFilm(filmGenres, film.getId());
        Optional<List<Directors>> filmDirectors = Optional.ofNullable(film.getDirectors());
        setDirectorsToFilm(filmDirectors, film.getId());
        return getFilm(film.getId());
    }

    @Override
    public Film addLike(int filmId, int userId) {
        String selectQuery = "SELECT COUNT(*) FROM filmLikes WHERE film_id = ? AND user_id = ?";
        int count = jdbcTemplate.queryForObject(selectQuery, Integer.class, filmId, userId);
        if (count == 0) {
            String query = "INSERT INTO filmLikes (film_id, user_id) VALUES (?, ?)";
            int t = jdbcTemplate.update(query, filmId, userId);
            String sql = "UPDATE FILM SET RATE=RATE+1 WHERE id = ? ";
            int t1 = jdbcTemplate.update(sql, filmId);
            if (t == 0 || t1 == 0) throw new EntityNotFoundException("такого id нет");
            return getFilm(filmId);
        }

        return null;
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
    public Collection<Film> mostPopularFilms(int count, int genreId, int year) {
        if (genreId == 0 && year == 0)  return getPopularityFilms(count);
        else if (genreId == 0 && year != 0) return mostPopularFilmsByYear(count, year);
        else if (genreId != 0 && year == 0) return mostPopularFilmsByGenre(count, genreId);
        else return mostPopularFilmsByGenreAndYear(count, genreId, year);
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

    public Collection<Film> mostPopularFilmsByGenre(int count, int genreId) {
        String sqlGenreOnly = "SELECT f.id " +
                "FROM film f " +
                "LEFT JOIN filmGenre fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "ORDER BY f.rate DESC " +
                "LIMIT ?";
        List<Integer> ids = jdbcTemplate.queryForList(sqlGenreOnly, Integer.class, genreId, count);
        List<Film> films = new ArrayList<>();
        for (int id : ids) {
            films.add(getFilm(id));
        }
        return films;
    }

    public Collection<Film> mostPopularFilmsByYear(int count, int year) {
        String sqlYearOnly = "SELECT f.id " +
                "FROM film f " +
                "LEFT JOIN filmGenre fg ON f.id = fg.film_id " +
                "WHERE EXTRACT(YEAR FROM f.releaseDate) = ? " +
                "ORDER BY f.rate DESC " +
                "LIMIT ?";
        List<Integer> ids = jdbcTemplate.queryForList(sqlYearOnly, Integer.class,  year, count);
        Set<Film> films = new HashSet<>();
        for (int id : ids) {
            films.add(getFilm(id));
        }
        return films;
    }

    public Collection<Film> mostPopularFilmsByGenreAndYear(int count, int genreId, int year) {
        String sqlGenreAndYear = "SELECT f.id " +
                "FROM film f " +
                "LEFT JOIN filmGenre fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "AND EXTRACT(YEAR FROM f.releaseDate) = ? " +
                "ORDER BY f.rate DESC " +
                "LIMIT ?";
        List<Integer> ids = jdbcTemplate.queryForList(sqlGenreAndYear, Integer.class, genreId, year, count);
        List<Film> films = new ArrayList<>();
        for (int id : ids) {
            films.add(getFilm(id));
        }
        return films;
    }

    @Override
    public void deleteFilm(int filmId) {
        var sql = "DELETE FROM Film WHERE id =?";
        jdbcTemplate.update(sql, filmId);
        log.info("Фильм с id '{}' удален", filmId);
    }

    private void setDirectorsToFilm(Optional<List<Directors>> filmDirectors, int filmId) {
        String sqlQuery = "DELETE FROM FILMDIRECTORS WHERE FILM_ID = ?";
        List<Directors> directorsAsList = new ArrayList<>();
        filmDirectors.ifPresent(directorsAsList::addAll);
        jdbcTemplate.update(sqlQuery, filmId);
        if (filmDirectors.isPresent() && !filmDirectors.get().isEmpty()) {
            String sqlQuery2 = "MERGE INTO FILMDIRECTORS (DIRECTORS_ID, FILM_ID) KEY (DIRECTORS_ID, FILM_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sqlQuery2, directorsAsList, directorsAsList.size(), (PreparedStatement ps, Directors d) -> {
                ps.setInt(1, d.getId());
                ps.setInt(2, filmId);
            });
        }
    }

    private void setGenresToFilm(Optional<List<Genre>> filmGenres, int filmId) {
        String sqlQuery = "DELETE FROM FILMGENRE WHERE FILM_ID = ?";
        List<Genre> genresAsList = new ArrayList<>();
        filmGenres.ifPresent(genresAsList::addAll);
        jdbcTemplate.update(sqlQuery, filmId);
        if (filmGenres.isPresent() && !filmGenres.get().isEmpty()) {
            String sqlQuery2 = "MERGE INTO FILMGENRE (GENRE_ID, FILM_ID) KEY (GENRE_ID, FILM_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sqlQuery2, genresAsList, 6, (PreparedStatement ps, Genre g) -> {
                ps.setInt(1, g.getId());
                ps.setInt(2, filmId);
            });
        }
    }

    private void filmValidation(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Ошибка регистрации названия фильма.");
        } else if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Ошибка регистрации описания фильма.");
        } else if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(DATE_OF_FIRST_FILM)) {
            throw new ValidationException("Ошибка регистрации даты релиза фильма.");
        } else if (film.getDuration() < 0) {
            throw new ValidationException("Ошибка регистрации длительности фильма.");
        }
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