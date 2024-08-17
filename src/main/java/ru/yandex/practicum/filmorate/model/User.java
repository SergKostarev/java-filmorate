package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class User {

    @EqualsAndHashCode.Include
    private Long id;

    @Email
    @NotBlank
    private String email;

    private String login;

    private String name;

    private LocalDate birthday;
}
