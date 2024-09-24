package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.idValidityCheck;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Validated
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/{id}")
    public Rating findRating(@PathVariable Long id) {
        idValidityCheck(id, "Некорректное значение параметра id рейтинга, должно быть больше 0");
        return ratingService.findRating(id);
    }

    @GetMapping
    public Collection<Rating> findAllRatings() {
        return ratingService
                .findAllRatings()
                .stream()
                .toList();
    }

}
