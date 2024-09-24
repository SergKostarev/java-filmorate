package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

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

    /*
    public Rating addRating(Rating rating) {
        return filmStorage.addRating(rating);
    }
     */
}
