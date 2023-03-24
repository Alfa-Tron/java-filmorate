package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @PastOrPresent
    @NotNull
    private LocalDate releaseDate;
    @Min(1)
    private Long duration;
    private Set<Integer> likes = new HashSet<>();

    public boolean dateAfter() {
        LocalDate date = LocalDate.of(1895, 11, 28);
        return releaseDate.isAfter(date);

    }


}
