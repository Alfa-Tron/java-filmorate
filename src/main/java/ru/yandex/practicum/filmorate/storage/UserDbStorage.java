package ru.yandex.practicum.filmorate.storage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
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
        jdbcTemplate.update("UPDATE FRIENDSHIP SET STATUS = ? WHERE (USER_ID = ? AND FRIEND_ID = ?) OR (USER_ID = ? AND FRIEND_ID = ?)",
                true, id, friendId, friendId, id);


        return getUserOne(id);
    }

    @Override
    public User deleteFriend(int id, int friendId) {
        jdbcTemplate.update("DELETE FROM FRIENDSHIP WHERE (USER_ID=? AND FRIEND_ID=?)OR(USER_ID=? AND FRIEND_ID=?)", id, friendId, id, friendId);
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

//        String sql = "DELETE FROM FilmLikes " +
//                "WHERE film_id =?";
//        jdbcTemplate.update(sql, userId);
//
//        sql = "DELETE FROM Friendship " +
//                "WHERE friend_id =?";
//        jdbcTemplate.update(sql, userId);
//
//        sql = "DELETE FROM Friendship " +
//                "WHERE user_id =?";
//        jdbcTemplate.update(sql, userId);

        var sql = "DELETE FROM userFilmorate " +
                "WHERE id =?";
        jdbcTemplate.update(sql, userId);

    }
}
