package ru.yandex.practicum.filmorate.model;

import lombok.Data;


import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
//@Entity(name ="USERFILMORATE")
public class User {
    private Integer id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
  //  private String status;


    public User() {

    }
}
