package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Review {
    private Long reviewId;
    @NonNull
    private final String content;
    private final boolean isPositive;
    @NonNull
    private final Integer userId;
    @NonNull
    private final Integer filmId;
    @Builder.Default
    private final Long useful = 0L;
}
