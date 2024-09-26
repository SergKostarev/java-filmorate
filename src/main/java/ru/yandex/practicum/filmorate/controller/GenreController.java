package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.idValidityCheck;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Validated
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable Long id) {
        idValidityCheck(id, "Некорректное значение параметра id жанра, должно быть больше 0");
        return genreService.findGenre(id);
    }

    @GetMapping
    public Collection<Genre> findAllGenres() {
        return genreService
                .findAllGenres()
                .stream()
                .toList();
    }
}
