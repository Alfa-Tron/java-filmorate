package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;

@RequiredArgsConstructor
@Service
class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository repository;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Override
    public Review addReview(Review review) {
        checkFilm(review.getFilmId());
        checkUser(review.getUserId());
        Long id = repository.addReview(review);
        return review.toBuilder()
                .reviewId(id)
                .build();
    }

    @Override
    public Review updateReview(Review review) {
        checkFilm(review.getFilmId());
        checkUser(review.getUserId());
        checkReview(review.getReviewId());
        repository.updateReview(review);
        return review;
    }

    @Override
    public void deleteReview(Long reviewId) {
        checkReview(reviewId);
        repository.deleteReview(reviewId);
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
        repository.likeReview(reviewId,userId);
    }

    @Override
    public void dislikeReview(Long reviewId, Integer userId) {
        checkReview(reviewId);
        checkUser(userId);
        repository.dislikeReview(reviewId,userId);
    }

    @Override
    public void deleteLikeReview(Long reviewId, Integer userId) {
        checkReview(reviewId);
        checkUser(userId);
        repository.deleteLikeReview(reviewId,userId);
    }

    @Override
    public void deleteDislikeReview(Long reviewId, Integer userId) {
        checkReview(reviewId);
        checkUser(userId);
        repository.deleteDislikeReview(reviewId,userId);
    }

    private void checkFilm(Integer filmId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new EntityNotFoundException();
        }
    }

    private void checkUser(Integer userId) {
        if (userStorage.getUserOne(userId) == null) {
            throw new EntityNotFoundException();
        }
    }

    private void checkReview(Long reviewId) {
        if (repository.getReviewById(reviewId) == null) {
            throw new EntityNotFoundException();
        }
    }

}
