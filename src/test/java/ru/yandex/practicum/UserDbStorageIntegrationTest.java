package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageIntegrationTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testRegisterUser() {

        User user = new User();
        user.setName("John Doe");
        user.setLogin("johndoe");
        user.setEmail("johndoe@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User registeredUser = userStorage.register(user);

        Assertions.assertNotNull(registeredUser.getId());
        assertThat(user.getId()).isEqualTo(1);
    }


    @Test
    public void testGetUserOne() {
        String insertUserSql = "INSERT INTO USERFILMORATE ( NAME, LOGIN, EMAIL, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        jdbcTemplate.update(insertUserSql, "Test User", "testuser", "testuser@example.com",
                LocalDate.parse("1990-01-01"));
        String insertUser2Sql = "INSERT INTO USERFILMORATE ( NAME, LOGIN, EMAIL, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        jdbcTemplate.update(insertUser2Sql, "Test User2", "testuser2", "testuser@example.com",
                LocalDate.parse("1990-01-01"));

        String insertFriendSql = "INSERT INTO FRIENDSHIP (user_id, friend_id, status) VALUES (?, ?, true)";
        jdbcTemplate.update(insertFriendSql, 1, 2);

        User user = userStorage.getUserOne(1);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getLogin()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("testuser@example.com");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.parse("1990-01-01"));
        assertThat(user.getFriends()).containsExactly(2);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userStorage.getUserOne(99);
        });

        String expectedMessage = "Пользователь с таким id не найден";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetAllUsers() {
        String insertUserSql = "INSERT INTO USERFILMORATE ( NAME, LOGIN, EMAIL, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        jdbcTemplate.update(insertUserSql, "Test User", "testuser", "testuser@example.com",
                LocalDate.parse("1990-01-01"));
        String insertUser2Sql = "INSERT INTO USERFILMORATE ( NAME, LOGIN, EMAIL, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        jdbcTemplate.update(insertUser2Sql, "Test User2", "testuser2", "testuser@example.com",
                LocalDate.parse("1990-01-01"));
        Collection<User> users = userStorage.getUsers();
        assertThat(users.size()).isEqualTo(2);

    }

    @Test
    public void testUpdateUser() {
        String insertUserSql = "INSERT INTO USERFILMORATE ( NAME, LOGIN, EMAIL, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        jdbcTemplate.update(insertUserSql, "Test User", "testuser", "testuser@example.com",
                LocalDate.parse("1990-01-01"));
        String insertUser2Sql = "INSERT INTO USERFILMORATE ( NAME, LOGIN, EMAIL, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        jdbcTemplate.update(insertUser2Sql, "Test User2", "testuser2", "testuser@example.com",
                LocalDate.parse("1990-01-01"));
        User user1 = new User();
        user1.setId(1);
        user1.setName("qwe");
        user1.setBirthday(LocalDate.parse("1990-01-02"));
        user1.setLogin("wwww");
        user1.setEmail("q@yandex.ru");
        userStorage.update(user1);

        User user = userStorage.getUserOne(1);
        assertThat(user.getName()).isEqualTo("qwe");
        assertThat(user.getLogin()).isEqualTo("wwww");
        assertThat(user.getEmail()).isEqualTo("q@yandex.ru");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.parse("1990-01-02"));
        user1.setId(99);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userStorage.update(user1);
        });

        String expectedMessage = "Пользователя с таким id нет";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testAddFriend() {
        User user = new User();
        user.setName("John Doe");
        user.setLogin("johndoe");
        user.setEmail("johndoe@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.register(user);
        User user2 = new User();
        user2.setName("John Doe2");
        user2.setLogin("johndoe2");
        user2.setEmail("johe@example.com");
        user2.setBirthday(LocalDate.of(1992, 1, 1));
        userStorage.register(user2);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(2, 1);
        List<Map<String, Object>> friendshipRows = jdbcTemplate.queryForList("SELECT * FROM FRIENDSHIP");
        Assertions.assertEquals(2, friendshipRows.size());
        Map<String, Object> row1 = friendshipRows.get(0);
        Assertions.assertEquals(1, row1.get("USER_ID"));
        Assertions.assertEquals(2, row1.get("FRIEND_ID"));
        Map<String, Object> row2 = friendshipRows.get(1);
        Assertions.assertEquals(2, row2.get("USER_ID"));
        Assertions.assertEquals(1, row2.get("FRIEND_ID"));
        Assertions.assertEquals(true, row2.get("STATUS"));

    }

    @Test
    public void testDeleteFriend() {
        User user = new User();
        user.setName("John Doe");
        user.setLogin("johndoe");
        user.setEmail("johndoe@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.register(user);
        User user2 = new User();
        user2.setName("John Doe2");
        user2.setLogin("johndoe2");
        user2.setEmail("johe@example.com");
        user2.setBirthday(LocalDate.of(1992, 1, 1));
        userStorage.register(user2);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(2, 1);
        userStorage.deleteFriend(2, 1);
        List<Map<String, Object>> friendshipRows = jdbcTemplate.queryForList("SELECT * FROM FRIENDSHIP");
        Assertions.assertEquals(1, friendshipRows.size());
    }

    @Test
    public void testGetFriends() {
        User user = new User();
        user.setName("John Doe");
        user.setLogin("johndoe");
        user.setEmail("johndoe@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.register(user);
        User user2 = new User();
        user2.setName("John Doe2");
        user2.setLogin("johndoe2");
        user2.setEmail("johe@example.com");
        user2.setBirthday(LocalDate.of(1992, 1, 1));
        userStorage.register(user2);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(2, 1);

        Collection<User> friends = userStorage.getFriends(1);

        assertThat(friends).hasSize(1);

    }

    @Test
    public void testGetGeneralFriends() {
        User user = new User();
        user.setName("John Doe");
        user.setLogin("johndoe");
        user.setEmail("johndoe@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.register(user);
        User user2 = new User();
        user2.setName("John Doe2");
        user2.setLogin("johndoe2");
        user2.setEmail("johe@example.com");
        user2.setBirthday(LocalDate.of(1992, 1, 1));
        User user3 = new User();
        user3.setName("John Doe2");
        user3.setLogin("johndoe2");
        user3.setEmail("johe@example.com");
        user3.setBirthday(LocalDate.of(1992, 1, 1));
        userStorage.register(user2);
        userStorage.register(user3);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(2, 1);
        userStorage.addFriend(1, 3);
        userStorage.addFriend(3, 1);
        assertThat(userStorage.getGeneralFriends(2, 3)).hasSize(1);


    }

}