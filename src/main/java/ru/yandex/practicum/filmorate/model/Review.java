package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.NonNull;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Jacksonized
@Getter
public class Review {
    private Long reviewId;
    @NonNull
    private final String content;
    @NonNull
    private final Boolean isPositive;
    @NonNull
    private final Integer userId;
    @NonNull
    private final Integer filmId;
    @Builder.Default
    private final Long useful = 0L;
}
