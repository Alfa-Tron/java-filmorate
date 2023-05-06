package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.NonNull;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Jacksonized
@JsonIgnoreProperties("positive")
@Getter
public class Review {
    private Long reviewId;
    @NonNull
    private final String content;
    @JsonProperty("isPositive")
    private final boolean isPositive;
    @NonNull
    private final Integer userId;
    @NonNull
    private final Integer filmId;
    @Builder.Default
    private final Long useful = 0L;
}
