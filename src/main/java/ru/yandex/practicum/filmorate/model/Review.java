package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.NonNull;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Jacksonized
@Getter
public class Review {
    @JsonProperty("reviewId")
    private Long id;
    @NonNull
    private final String content;
    @NonNull
    @JsonProperty("isPositive")
    private final Boolean positive;
    @NonNull
    private final Integer userId;
    @NonNull
    private final Integer filmId;
    @Builder.Default
    private final Long useful = 0L;
}
