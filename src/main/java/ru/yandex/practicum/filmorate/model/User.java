package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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

    @Email(message = "Некорректный email")
    @NotBlank(message = "Пустой email")
    private String email;

    @NotNull(message = "Пустой логин")
    @NotBlank(message = "Пустой логин")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть текущей")
    private LocalDate birthday;
}
