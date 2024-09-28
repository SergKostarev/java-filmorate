package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.idValidityCheck;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/{id}")
    public Rating findRating(@PathVariable Long id) {
        idValidityCheck(id, "Некорректное значение параметра id рейтинга, должно быть больше 0");
        return ratingService.findRating(id);
    }

    @GetMapping
    public Collection<Rating> findAllRatings() {
        return ratingService.findAllRatings();
    }
}
