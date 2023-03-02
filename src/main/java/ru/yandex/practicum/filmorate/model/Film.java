package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {

    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @Email
    private String email;
    @Past
    @NotNull
    private LocalDate realise;
    @Min(0)
    private Long duration;

    public boolean dateAfter(){
        LocalDate date = LocalDate.of(1895,11,28);
        return realise.isAfter(date);

    }

}
