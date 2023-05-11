package ru.yandex.practicum.filmorate.repository;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewRepository {
    @NonNull
    Long addReview(@NonNull Review review);

    @NonNull
    Review updateReview(@NonNull Review review);

    void deleteReview(@NonNull Long reviewId);

    Review getReviewById(@NonNull Long reviewId);

    Collection<Review> getAllReviews(@NonNull Long count);

    Collection<Review> getAllReviews(Integer filmId, Long count);

    void likeReview(@NonNull Long reviewId, @NonNull Integer userId);

    void dislikeReview(@NonNull Long reviewId, @NonNull Integer userId);

    void deleteLikeReview(@NonNull Long reviewId, @NonNull Integer userId);

    void deleteDislikeReview(@NonNull Long reviewId, @NonNull Integer userId);
}
