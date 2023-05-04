package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Directors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Primary
public class DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public Directors addNewDirector(Directors director) {
        String sql = "INSERT INTO DIRECTORS (id, name) " +
                "VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setInt(1, director.getId() - 9);
            ps.setString(2, director.getName());
            return ps;
        }, keyHolder); // установка полученного id фильма в объект film
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return director;
    }

    public Directors updateDirector(Directors director) {
        String sqlQuery = "UPDATE DIRECTORS SET NAME = ?";
        jdbcTemplate.update(sqlQuery,
                director.getName());
        return getDirectorById(director.getId());
    }

    public List<Directors> getAllDirectors() {
        String sqlQuery = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    public Directors getDirectorById(Integer id) {
        String sqlQuery = "SELECT * FROM DIRECTORS WHERE ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
    }

    public void deleteDirectorById(Integer id) {
        String sqlDeleteFromDirectors = "DELETE FROM DIRECTORS WHERE ID = ?";
        jdbcTemplate.update(sqlDeleteFromDirectors, id);
    }

    private Directors mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Directors.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
