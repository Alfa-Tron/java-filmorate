package ru.yandex.practicum.filmorate.model;

import lombok.Data;


import javax.persistence.Entity;
import javax.validation.constraints.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name ="FILM")
public class Film {

    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @PastOrPresent
    @NotNull
    private Date releaseDate;
    @Min(1)
    private Long duration;
    private Set<Integer> likes = new HashSet<>();
    @NotBlank
    private String genre;
    private String rating;

    public boolean dateAfter() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1895);
        calendar.set(Calendar.MONTH, 10); // 10 - это ноябрь (отсчет месяцев начинается с 0)
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        Date date = calendar.getTime();
        return releaseDate.after(date);
    }


}
