package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.enums.OperationType.*;

@RequiredArgsConstructor
@Service
class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repository;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

    @Override
    public Review addReview(Review review) {
        checkFilm(review.getFilmId());
        checkUser(review.getUserId());
        Long id = repository.addReview(review);
        review = review.toBuilder()
                .reviewId(id)
                .build();
        feedStorage.addFeed(review.getReviewId().intValue(), review.getUserId(), Instant.now().toEpochMilli(), REVIEW, ADD);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        checkFilm(review.getFilmId());
        checkUser(review.getUserId());
        checkReview(review.getReviewId());
        review = repository.updateReview(review);
        feedStorage.addFeed(review.getReviewId().intValue(), review.getUserId(), Instant.now().toEpochMilli(), REVIEW, UPDATE);
        return review;
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = repository.getReviewById(reviewId);
        if (review == null) {
            throw new EntityNotFoundException();
        } else {
            repository.deleteReview(reviewId);
            feedStorage.addFeed(review.getReviewId().intValue(), review.getUserId(), Instant.now().toEpochMilli(), REVIEW, REMOVE);
        }
    }

    @Override
    public Review getReviewById(Long reviewId) {
        Review review = repository.getReviewById(reviewId);
        if (review == null) {
            throw new EntityNotFoundException();
        }
        return review;
    }

    @Override
    public Collection<Review> getAllReviews(Integer filmId, Long count) {
        if (filmId == null) {
            return repository.getAllReviews(count);
        }
        checkFilm(filmId);
        return repository.getAllReviews(filmId, count);
    }

    @Override
    public void likeReview(Long reviewId, Integer userId) {
        checkReview(reviewId);
        checkUser(userId);
        repository.likeReview(reviewId, userId);
    }

    @Override
    public void dislikeReview(Long reviewId, Integer userId) {
        checkReview(reviewId);
        checkUser(userId);
        repository.dislikeReview(reviewId, userId);
    }

    @Override
    public void deleteLikeReview(Long reviewId, Integer userId) {
        checkReview(reviewId);
        checkUser(userId);
        repository.deleteLikeReview(reviewId, userId);
    }

    @Override
    public void deleteDislikeReview(Long reviewId, Integer userId) {
        checkReview(reviewId);
        checkUser(userId);
        repository.deleteDislikeReview(reviewId, userId);
    }

    private void checkFilm(Integer filmId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new EntityNotFoundException("Фильм с идентификатором " + filmId + " не найден.");
        }
    }

    private void checkUser(Integer userId) {
        if (userStorage.getUserOne(userId) == null) {
            throw new EntityNotFoundException("Пользователь с идентификатором " + userId + " не найден.");
        }
    }

    private void checkReview(Long reviewId) {
        if (repository.getReviewById(reviewId) == null) {
            throw new EntityNotFoundException("Отзыв с идентификатором " + reviewId + " не найден.");
        }
    }
}
