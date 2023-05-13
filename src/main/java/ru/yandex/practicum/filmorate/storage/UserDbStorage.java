package ru.yandex.practicum.filmorate.storage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User register(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("USERFILMORATE").usingGeneratedKeyColumns("ID");
        Number key = jdbcInsert.executeAndReturnKey(parameters);
        user.setId(key.intValue());
        return user;
    }

    @SneakyThrows
    @Override
    public User getUserOne(int id) {
        String userSql = "SELECT * FROM USERFILMORATE WHERE id = ?";
        String friendSql = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ? AND STATUS = true";
        User user = jdbcTemplate.queryForObject(userSql, new Object[]{id}, (rs, rowNum) -> {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setName(rs.getString("name"));
            u.setLogin(rs.getString("login"));
            u.setEmail(rs.getString("email"));
            u.setBirthday(rs.getDate("birthday").toLocalDate());
            return u;
        });
        Set<Integer> friends = jdbcTemplate.query(friendSql, ps -> ps.setInt(1, id), rsFr -> {
            Set<Integer> set = new HashSet<>();
            while (rsFr.next()) {
                set.add(rsFr.getInt("FRIEND_ID"));
            }
            return set;
        });
        user.setFriends(friends);

        return user;
    }

    @Override
    public Collection<User> getUsers() {
        List<User> users = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERFILMORATE");
        while (userRows.next()) {
            users.add(getUserOne(userRows.getInt("ID")));
        }
        return users;
    }

    @Override
    public User update(User user) {
        int t = jdbcTemplate.update("UPDATE userFilmorate SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (t == 0) {
            log.error("Пользователя с id {} нет", user.getId());
            throw new EntityNotFoundException("Пользователя с таким id нет");
        }
        return user;
    }

    @Override
    public User addFriend(int id, int friendId) {
        if (id < 0 || friendId < 0) throw new EntityNotFoundException("Пользователя с таким id нет");
        jdbcTemplate.update("INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS) VALUES (?, ?, ?)",
                id, friendId, false);
        jdbcTemplate.update("UPDATE FRIENDSHIP SET STATUS = ?" +
                        " WHERE (USER_ID = ? AND FRIEND_ID = ?) OR (USER_ID = ? AND FRIEND_ID = ?)",
                true, id, friendId, friendId, id);
        return getUserOne(id);
    }

    @Override
    public User deleteFriend(int id, int friendId) {
        jdbcTemplate.update("DELETE FROM FRIENDSHIP WHERE" +
                " (USER_ID=? AND FRIEND_ID=?)OR(USER_ID=? AND FRIEND_ID=?)", id, friendId, id, friendId);
        return getUserOne(id);
    }

    @Override
    public Collection<User> getFriends(int id) {
        String sql = "SELECT COUNT(*) FROM userFilmorate WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (count == 0) throw new EntityNotFoundException("Friend не найден");
        SqlRowSet userRowsFr = jdbcTemplate.queryForRowSet("select FRIEND_ID, STATUS from FRIENDSHIP where USER_ID = ?", id);
        Set<User> friends = new HashSet<>();
        while (userRowsFr.next()) {
            if (userRowsFr.getBoolean("status")) {
                friends.add(getUserOne(userRowsFr.getInt("FRIEND_ID")));
            }
        }
        return friends;
    }

    @Override
    public List<User> getGeneralFriends(int id, int friendId) {
        String sql = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID IN (SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?)";
        List<Integer> friendIds = jdbcTemplate.queryForList(sql, Integer.class, id, friendId);
        List<User> generalFriends = new ArrayList<>();
        for (int fId : friendIds) {
            generalFriends.add(getUserOne(fId));
        }
        return generalFriends;
    }

    @Override
    public void deleteUser(int userId) {
        var sql = "DELETE FROM userFilmorate " +
                "WHERE id =?";
        jdbcTemplate.update(sql, userId);
    }

    public Collection<Film> getRecommendation(int userId) {
        // Найти пользователей с максимальным количеством пересечения по лайкам
        String sql1 = "SELECT F1.user_id AS likes_count " +
                "FROM FILMLIKES F1 " +
                "JOIN FILMLIKES F2 ON F1.film_id = F2.film_id AND F2.user_id = ? " +
                "WHERE F1.user_id <> ? " +
                "GROUP BY F1.user_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT 1;";
        List<Integer> similarUsers = jdbcTemplate.queryForList(sql1, Integer.class, userId, userId);
        if (similarUsers.isEmpty()) return new ArrayList<>();
        // Определить фильмы, которые один пролайкал, а другой нет
        String sql2 = "SELECT film_id " +
                "FROM filmLikes " +
                "WHERE user_id = ? AND film_id NOT IN (SELECT film_id " +
                "FROM filmLikes " +
                "WHERE user_id = ?)";
        List<Integer> recommendedMovies = jdbcTemplate.queryForList(sql2, Integer.class, similarUsers.get(0), userId);
        // Рекомендовать фильмы, которым поставил лайк пользователь с похожими вкусами, а тот, для кого составляется рекомендация, ещё не поставил
        String sql3 = "SELECT film_id " +
                "FROM filmLikes " +
                "WHERE user_id = ? AND film_id IN (SELECT film_id " +
                "FROM filmLikes " +
                "WHERE user_id = ?)";
        List<Integer> likedMovies = jdbcTemplate.queryForList(sql3, Integer.class, similarUsers.get(0), userId);
        recommendedMovies.removeAll(likedMovies);
        List<User> users = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERFILMORATE");
        while (userRows.next()) {
            users.add(getUserOne(userRows.getInt("ID")));
        }
        String sql = "Select * FROM Film WHERE id = ?";

        List<Film> films = new ArrayList<>();
        for (int i : recommendedMovies) {
            films.add(jdbcTemplate.queryForObject(sql, new Object[]{i}, (rs, rowNum) -> {
                Film film = new Film();
                film.setId(rs.getInt("id"));
                film.setName(rs.getString("film_name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
                film.setDuration((long) rs.getInt("duration"));
                film.setRate(rs.getInt("rate"));
                Mpa mpa = jdbcTemplate.queryForObject("Select * FROM MPA WHERE id=?",
                        new Object[]{rs.getInt("mpa")}, (rs1, rowNum1) -> {
                            Mpa mpa1 = new Mpa();
                            mpa1.setId(rs1.getInt("id"));
                            mpa1.setName(rs1.getString("name"));
                            return mpa1;
                        });
                film.setMpa(mpa);
                List<Genre> genres = new ArrayList<>();

                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * From FILMGENRE WHERE FILM_ID =" + rs.getInt("id"));
                while (sqlRowSet.next()) {
                    Genre genre = jdbcTemplate.queryForObject("SELECT * FROM Genre WHERE id = ?", new Object[]{sqlRowSet.getInt("genre_id")}, (rs2, rowNum2) -> {
                        Genre genre1 = new Genre();
                        genre1.setId(rs2.getInt("id"));
                        genre1.setName(rs2.getString("name"));
                        return genre1;
                    });
                    genres.add(genre);
                }
                film.setGenres(genres);
                return film;
            }));
        }
        return films;
    }
}