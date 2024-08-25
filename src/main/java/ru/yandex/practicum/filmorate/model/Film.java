package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Film {

    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Название фильма не может быть пустым")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма может содержать не более 200 символов")
    private String description;

    private LocalDate releaseDate;

    @PositiveOrZero(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
}
