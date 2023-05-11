package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewRepositoryImplTest {
    private final ReviewRepositoryImpl reviewRepository;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    private static Long reviewId;
    private static Integer filmId;
    private static Integer userId;

    @Test
    @Order(1)
    public void setup() {
        Film originalFilm = new Film();
        originalFilm.setName("Test Film");
        originalFilm.setDescription("Test description");
        originalFilm.setReleaseDate(LocalDate.of(1967, 3, 25));
        originalFilm.setDuration(120L);
        originalFilm.setRate(5);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        originalFilm.setMpa(mpa);
        filmId = filmStorage.addFilm(originalFilm).getId();

        User user1 = new User();
        user1.setName("JD");
        user1.setLogin("JD");
        user1.setEmail("JD@example.com");
        user1.setBirthday(LocalDate.of(2012, 1, 1));
        userId = userStorage.register(user1).getId();

        User user2 = new User();
        user2.setName("BL");
        user2.setLogin("BL");
        user2.setEmail("BL@example.com");
        user2.setBirthday(LocalDate.of(1995, 1, 1));
        userStorage.register(user2);

        User user3 = new User();
        user3.setName("John Doe");
        user3.setLogin("johndoe");
        user3.setEmail("johndoe@example.com");
        user3.setBirthday(LocalDate.of(1853, 1, 1));
        userStorage.register(user3);
    }

    @Test
    @Order(2)
    public void addReview() {
        List<Film> films = ((List<Film>) filmStorage.getFilms());
        List<User> users = ((List<User>) userStorage.getUsers());

        reviewId = reviewRepository.addReview(Review.builder()
                .userId(users.get(0).getId())
                .filmId(films.get(0).getId())
                .content("jnps")
                .positive(true)
                .build());
        Assertions.assertNotNull(reviewId);
    }

    @Test
    @Order(3)
    public void updateReview() {
        Review review = Objects.requireNonNull(reviewRepository.getReviewById(reviewId));
        String content = "content";
        reviewRepository.updateReview(review.toBuilder()
                .content(content)
                .positive(false)
                .filmId(-1)
                .userId(-1)
                .build()
        );
        Review reviewById = reviewRepository.getReviewById(review.getId());

        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(content, reviewById.getContent());
        Assertions.assertEquals(review.getFilmId(), reviewById.getFilmId());
        Assertions.assertEquals(review.getUserId(), reviewById.getUserId());
        Assertions.assertFalse(reviewById.isPositive());
    }

    @Test
    @Order(11)
    public void deleteReview() {
        Review review = Objects.requireNonNull(reviewRepository.getReviewById(reviewId));
        Long reviewId = review.getId();
        reviewRepository.deleteReview(reviewId);

        Assertions.assertEquals(0, reviewRepository.getAllReviews(1L).size());
    }

    @Test
    @Order(4)
    public void getReviewById() {
        Review review = Objects.requireNonNull(reviewRepository.getReviewById(reviewId));
        Assertions.assertEquals(reviewId, review.getId());
    }

    @Test
    @Order(5)
    public void getAllReviews() {
        Collection<Review> allReviews = reviewRepository.getAllReviews(Long.MAX_VALUE);
        Assertions.assertEquals(1, allReviews.size());
    }

    @Test
    @Order(6)
    public void getAllReviewsByFilm() {
        Collection<Review> allReviews = reviewRepository.getAllReviews(filmId, Long.MAX_VALUE);
        Assertions.assertEquals(1, allReviews.size());

        Collection<Review> reviews = reviewRepository.getAllReviews(-1, Long.MAX_VALUE);
        Assertions.assertEquals(0, reviews.size());
    }

    @Test
    @Order(7)
    public void likeReview() {
        Review reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(0, reviewById.getUseful());

        reviewRepository.likeReview(reviewId, userId);

        reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(1, reviewById.getUseful());

        reviewRepository.likeReview(reviewId, userId);

        reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(1, reviewById.getUseful());
    }

    @Test
    @Order(8)
    public void dislikeReview() {
        Review reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(1, reviewById.getUseful());

        reviewRepository.dislikeReview(reviewId, userId);

        reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(0, reviewById.getUseful());

        reviewRepository.dislikeReview(reviewId, userId);

        reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(0, reviewById.getUseful());
    }

    @Test
    @Order(9)
    public void deleteLikeReview() {
        Review reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(0, reviewById.getUseful());

        reviewRepository.deleteLikeReview(reviewId, userId);
        reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(-1, reviewById.getUseful());
    }

    @Test
    @Order(10)
    public void deleteDislikeReview() {
        Review reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(-1, reviewById.getUseful());

        reviewRepository.deleteDislikeReview(reviewId, userId);
        reviewById = reviewRepository.getReviewById(reviewId);
        Assertions.assertNotNull(reviewById);
        Assertions.assertEquals(0, reviewById.getUseful());
    }
}