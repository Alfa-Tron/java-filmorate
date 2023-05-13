package ru.yandex.practicum.filmorate.service;


import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {

    Review addReview(@NonNull Review review);

    Review updateReview(@NonNull Review review);

    void deleteReview(@NonNull Long reviewId);

    Review getReviewById(@NonNull Long reviewId);

    Collection<Review> getAllReviews(@Nullable Integer filmId, @NonNull Long count);

    void likeReview(@NonNull Long reviewId, @NonNull Integer userId);

    void dislikeReview(@NonNull Long reviewId, @NonNull Integer userId);

    void deleteLikeReview(@NonNull Long reviewId, @NonNull Integer userId);

    void deleteDislikeReview(@NonNull Long reviewId, @NonNull Integer userId);
}
