package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final FilmStorage filmStorage;

    public Rating findRating(Long id) {
        return filmStorage.getRating(id);
    }

    public Collection<Rating> findAllRatings() {
        return filmStorage.getAllRatings();
    }
}
