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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;


@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageIntegrationTests {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testAddFilm() {
        Film originalFilm = new Film();
        originalFilm.setName("Test Film");
        originalFilm.setDescription("Test description");
        originalFilm.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm.setDuration(120L);
        originalFilm.setRate(5);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        originalFilm.setMpa(mpa);

        Film addedFilm = filmDbStorage.addFilm(originalFilm);

        assertNotNull(addedFilm.getId());
        originalFilm.setId(addedFilm.getId());
        Assertions.assertEquals(originalFilm.getName(), addedFilm.getName());
    }

    @Test
    public void testGetFilm() {
        Film originalFilm = new Film();
        originalFilm.setName("Test Film");
        originalFilm.setDescription("Test description");
        originalFilm.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm.setDuration(120L);
        originalFilm.setRate(5);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        originalFilm.setMpa(mpa);
        filmDbStorage.addFilm(originalFilm);
        Assertions.assertEquals(1, filmDbStorage.getFilm(1).getId());

    }

    @Test
    public void testGetFilms() {
        Film originalFilm = new Film();
        originalFilm.setName("Test Film");
        originalFilm.setDescription("Test description");
        originalFilm.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm.setDuration(120L);
        originalFilm.setRate(5);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        originalFilm.setMpa(mpa);
        filmDbStorage.addFilm(originalFilm);
        Film originalFilm1 = new Film();
        originalFilm1.setName("Test123Film");
        originalFilm1.setDescription("Test description");
        originalFilm1.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm1.setDuration(120L);
        originalFilm1.setRate(5);
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        originalFilm1.setMpa(mpa1);
        filmDbStorage.addFilm(originalFilm1);
        Assertions.assertEquals(2, filmDbStorage.getFilms().size());

    }

    @Test
    public void testUpdate() {
        Film originalFilm = new Film();
        originalFilm.setName("Test Film");
        originalFilm.setDescription("Test description");
        originalFilm.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm.setDuration(120L);
        originalFilm.setRate(5);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        originalFilm.setMpa(mpa);
        filmDbStorage.addFilm(originalFilm);
        mpa.setId(2);
        originalFilm.setMpa(mpa);
        filmDbStorage.update(originalFilm);
        System.out.println(filmDbStorage.getFilm(1));
        Assertions.assertEquals(2, filmDbStorage.getFilm(1).getMpa().getId());
    }

    @Test
    public void testAddLike() {
        Film originalFilm = new Film();
        originalFilm.setName("Test Film");
        originalFilm.setDescription("Test description");
        originalFilm.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm.setDuration(120L);
        originalFilm.setRate(5);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        originalFilm.setMpa(mpa);
        filmDbStorage.addFilm(originalFilm);

        User user = new User();
        user.setName("John Doe");
        user.setLogin("johndoe");
        user.setEmail("johndoe@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.register(user);
        filmDbStorage.addLike(1, 1);

        Assertions.assertEquals(1, filmDbStorage.getFilm(1).getRate());
    }

    @Test
    public void testDeleteLike() {
        Film originalFilm = new Film();
        originalFilm.setName("Test Film");
        originalFilm.setDescription("Test description");
        originalFilm.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm.setDuration(120L);
        originalFilm.setRate(5);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        originalFilm.setMpa(mpa);
        filmDbStorage.addFilm(originalFilm);

        User user = new User();
        user.setName("John Doe");
        user.setLogin("johndoe");
        user.setEmail("johndoe@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.register(user);
        filmDbStorage.addLike(1, 1);
        Assertions.assertEquals(1, filmDbStorage.getFilm(1).getRate());
        filmDbStorage.deleteLike(1, 1);
        Assertions.assertEquals(0, filmDbStorage.getFilm(1).getRate());

    }

    @Test
    public void testGetPopularityFilms() {
        Film originalFilm = new Film();
        originalFilm.setName("Test Film");
        originalFilm.setDescription("Test description");
        originalFilm.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm.setDuration(120L);
        originalFilm.setRate(5);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        originalFilm.setMpa(mpa);
        filmDbStorage.addFilm(originalFilm);
        Film originalFilm1 = new Film();
        originalFilm1.setName("Test123Film");
        originalFilm1.setDescription("Test description");
        originalFilm1.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm1.setDuration(120L);
        originalFilm1.setRate(6);
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        originalFilm1.setMpa(mpa1);
        filmDbStorage.addFilm(originalFilm1);
        Assertions.assertEquals(2, filmDbStorage.getPopularityFilms(5).size());
    }
}
