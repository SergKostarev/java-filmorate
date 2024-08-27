package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmStorage filmStorage;

    private final FilmService filmService;

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable Long id) {
        idValidityCheck(id, "Некорректное значение параметра id фильма, должно быть больше 0");
        return filmStorage.findFilm(id);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmStorage.update(newFilm);
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
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") Integer count) {
        if (count < 1) {
            String message = "Некорректное значение " +
                    "параметра count, должно быть больше 0";
            String value = String.valueOf(count);
            log.error(value, message);
            throw new ParameterNotValidException(value, message);
        }
        return filmService.getMostPopular(count);
    }

    private void idValidityCheck(Long id, String message) {
        if (id == null || id < 1) {
            String value = String.valueOf(id);
            log.error(value, message);
            throw new ParameterNotValidException(String.valueOf(id), message);
        }
    }
}
