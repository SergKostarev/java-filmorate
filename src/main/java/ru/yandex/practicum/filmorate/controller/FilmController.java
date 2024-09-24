package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.idValidityCheck;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable Long id) {
        idValidityCheck(id, "Некорректное значение параметра id фильма, должно быть больше 0");
        return filmService.findFilm(id);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService
                .findAll()
                .stream()
                .toList();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        idValidityCheck(id, "Некорректное значение параметра id фильма, должно быть больше 0");
        idValidityCheck(userId,"Некорректное значение параметра id пользователя," +
                "должно быть больше 0");
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void dislike(@PathVariable Long id, @PathVariable Long userId) {
        idValidityCheck(id, "Некорректное значение параметра id фильма, должно быть больше 0");
        idValidityCheck(userId,"Некорректное значение параметра id пользователя," +
                "должно быть больше 0");
        filmService.dislike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(@Positive @RequestParam(defaultValue = "10") Integer count) {
        return filmService
                .getMostPopular(count)
                .stream()
                .toList();
    }

}
