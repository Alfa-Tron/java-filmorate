package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@SuppressWarnings("unused")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService service;

    @PostMapping("/reviews")
    public Review addReview(@RequestBody @Valid @NonNull Review review) {
        return service.addReview(review);
    }

    @PutMapping("/reviews")
    public Review updateReview(@RequestBody @Valid @NonNull Review review) {
        return service.updateReview(review);
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable("id") @NonNull Long reviewId) {
        service.deleteReview(reviewId);
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable("id") @NonNull Long reviewId) {
        return service.getReviewById(reviewId);
    }

    @GetMapping("/reviews")
    public Collection<Review> getAllReviews(
            @RequestParam("filmId") @Nullable Integer filmId,
            @RequestParam(value = "count", defaultValue = "10") Long count
    ) {
        return service.getAllReviews(filmId, count);
    }

    @PutMapping("reviews/{id}/like/{userId}")
    public void likeReview(
            @PathVariable("id") @NonNull Long reviewId,
            @PathVariable("userId") @NonNull Integer userId
    ) {
        service.likeReview(reviewId, userId);
    }

    @PutMapping("reviews/{id}/dislike/{userId}")
    public void dislikeReview(
            @PathVariable("id") @NonNull Long reviewId,
            @PathVariable("userId") @NonNull Integer userId
    ) {
        service.dislikeReview(reviewId, userId);
    }

    @DeleteMapping("reviews/{id}/like/{userId}")
    public void deleteLikeReview(
            @PathVariable("id") @NonNull Long reviewId,
            @PathVariable("userId") @NonNull Integer userId
    ) {
        service.deleteLikeReview(reviewId, userId);
    }

    @DeleteMapping("reviews/{id}/dislike/{userId}")
    public void deleteDislikeReview(
            @PathVariable("id") @NonNull Long reviewId,
            @PathVariable("userId") @NonNull Integer userId
    ) {
        service.deleteDislikeReview(reviewId, userId);
    }
}