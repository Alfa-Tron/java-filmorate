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
import java.text.SimpleDateFormat;
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
        String sql = "INSERT INTO USERFILMORATE (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?,?,?,?)";

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
    //  private List<Integer> friends(int id){
    //     SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERFILMORATE where id = ?", id);

    //  }

    @SneakyThrows
    @Override
    public User getUserOne(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERFILMORATE where id = ?", id);
        User user = new User();
        if (userRows.next()) {
            user.setId(id);
            user.setName(userRows.getString("NAME"));
            user.setLogin(userRows.getString("LOGIN"));
            user.setEmail(userRows.getString("EMAIL"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            user.setBirthday(dateFormat.parse(dateFormat.format(userRows.getDate("BIRTHDAY"))));


        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
        SqlRowSet userRowsFr = jdbcTemplate.queryForRowSet("select FRIEND_ID, STATUS from FRIENDSHIP where USER_ID = ?", id);
        Set<Integer> friends = new HashSet<>();
        while (userRowsFr.next()) {
            if (userRowsFr.getBoolean("status")) {
                friends.add(userRowsFr.getInt("FRIEND_ID"));
            }
        }
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
}
