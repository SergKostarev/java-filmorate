package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.idValidityCheck;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable Long id) {
        idValidityCheck(id, "Некорректное значение параметра id жанра, должно быть больше 0");
        return genreService.findGenre(id);
    }

    @GetMapping
    public Collection<Genre> findAllGenres() {
        return genreService.findAllGenres();
    }
}
