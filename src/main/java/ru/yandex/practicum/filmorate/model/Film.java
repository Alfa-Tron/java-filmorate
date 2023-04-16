package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
//@Entity(name ="FILM")
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
    private Integer rate = 0;
    private List<Genre> genres = null;
    private Mpa mpa = null;

    public boolean dateAfter() {
        LocalDate date = LocalDate.of(1895, 11, 28);
        return releaseDate.isAfter(date);

    }

    @Data
    public static class Mpa {
        private int id;
        private  String name;
    }

    @Data
    public static class Genre {
        private int id;
        private String name;
    }


}
