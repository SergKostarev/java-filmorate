package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
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

    @PastOrPresent(message = "Дата рождения не может больше текущей")
    private LocalDate birthday;

    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
